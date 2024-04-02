/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.intellifire.internal;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.openhab.binding.intellifire.internal.discovery.intellifireDiscoveryService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link intellifireBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class intellifireBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(intellifireBridgeHandler.class);
    private final HttpClient httpClient;
    private int commFailureCount;
    private @Nullable intellifireConfiguration config;
    private @Nullable ScheduledFuture<?> initializeFuture;
    private @Nullable ScheduledFuture<?> pollTelemetryFuture;
    private @Nullable ScheduledFuture<?> pollAlarmsFuture;
    private @Nullable CookieStore cs;

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(intellifireDiscoveryService.class);
    }

    public intellifireBridgeHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        this.httpClient = httpClient;
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        initializeFuture = scheduler.schedule(this::scheduledInitialize, 1, TimeUnit.SECONDS);
        return;
    }

    public void scheduledInitialize() {
        config = getConfigAs(intellifireConfiguration.class);

        try {
            login();
            intellifireUsername username = getUsername();
            updateStatus(ThingStatus.ONLINE);
        } catch (InterruptedException e) {
            logger.error("Intellifire fireplace thing", e);
        }
    }

    public synchronized boolean login() throws InterruptedException {
        final Gson gson = new Gson();

        // Login to Intellifire cloud server and retrieve cookies
        String httpResponse = httpResponseContent(intellifireBindingConstants.URL_LOGIN, HttpMethod.POST,
                // "username=mmyers75@icloud.com&password=R39gVXg@ng&zQ3^6");
                "username=" + config.username + "&password=" + config.password);
        return true;
    }

    public synchronized intellifireUsername getUsername() throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent(intellifireBindingConstants.URL_GETUSERNAME, HttpMethod.POST, "");
        intellifireUsername username = gson.fromJson(httpResponse, intellifireUsername.class);
        logger.trace("getUsername: {}", username.username);

        return username;
    }

    public synchronized @Nullable intellifireAccount getAccountLocations() throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent(intellifireBindingConstants.URL_ENUMLOCATIONS, HttpMethod.POST, "");
        intellifireAccount locations = gson.fromJson(httpResponse, intellifireAccount.class);

        return locations;
    }

    public synchronized @Nullable intellifireLocation getFireplaces(String locationID) throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://iftapi.net/a/enumfireplaces?location_id=" + locationID,
                HttpMethod.POST, "");
        intellifireLocation fireplaces = gson.fromJson(httpResponse, intellifireLocation.class);

        return fireplaces;
    }

    public synchronized @Nullable intellifirePollData cloudPollFireplace(String serialNumber)
            throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://iftapi.net/a/" + serialNumber + "/apppoll", HttpMethod.POST,
                "");
        intellifirePollData pollData = gson.fromJson(httpResponse, intellifirePollData.class);

        return pollData;
    }

    public synchronized @Nullable intellifirePollData localPollFireplace(String IPaddress) throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://" + IPaddress + "/poll", HttpMethod.GET, "");
        intellifirePollData pollData = gson.fromJson(httpResponse, intellifirePollData.class);

        return pollData;
    }

    public synchronized String httpResponseContent(String url, HttpMethod method, String content)
            throws InterruptedException {

        for (int retry = 0; retry <= 5; retry++) {
            try {
                Request request = httpRequestBuilder(url, method);

                cs = httpClient.getCookieStore();

                // load cookies into request
                cs.get(intellifireBindingConstants.URI_COOKIE).forEach(cookie -> {
                    request.cookie(cookie);
                });

                ContentResponse httpResponse = httpRequestBuilder(url, method)
                        .content(new StringContentProvider(content), "text/plain;charset=UTF-8")
                        .header(HttpHeader.CONTENT_LENGTH, Integer.toString(content.length())).send();

                if (httpResponse.getStatus() != 200 && httpResponse.getStatus() != 204) {
                    logger.info("Login failed with http status code: {}", httpResponse.getStatus());
                }

                logger.trace("httpResponseCode: {}", httpResponse.getStatus());

                // List<HttpCookie> cookies = httpClient.getCookieStore().getCookies();

                logger.trace("Headers:\n{}", request.getHeaders());
                logger.trace("Cookies: {}", httpClient.getCookieStore().getCookies().toString());
                logger.debug("httpResponseContent: {}", httpResponse.getContentAsString());

                // store any received cookies
                cs = httpClient.getCookieStore();

                return httpResponse.getContentAsString();

            } catch (ExecutionException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Unable to resolve host.  Check host and your internet connection. " + e.getMessage());
                return "";
            } catch (TimeoutException e) {
                if (retry >= 2) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "Connection Timeout.  Check host and your internet connection. " + e.getMessage());
                    return "";
                } else {
                    logger.warn("Intellifire Account thing Timeout:  {} Try:  {} ", getCallingMethod(), retry + 1);
                }
            }
        }
        return "";
    }

    private Request httpRequestBuilder(String url, HttpMethod method) {
        Request request = httpClient.newRequest(url)
                .header(HttpHeader.HOST, intellifireBindingConstants.HTTP_HEADERS_HOST)
                .header(HttpHeader.CONNECTION, intellifireBindingConstants.HTTP_HEADERS_CONNECTION)
                .agent("openHAB Intellifire Binding org.openhab.binding.intellifire").method(method)
                .header(HttpHeader.ACCEPT, intellifireBindingConstants.HTTP_HEADERS_ACCEPT)
                .header(HttpHeader.ACCEPT_LANGUAGE, intellifireBindingConstants.HTTP_HEADERS_lANGUAGE)
                .header(HttpHeader.ACCEPT_ENCODING, intellifireBindingConstants.HTTP_HEADERS_ACCEPTENCODING)
                .version(HttpVersion.HTTP_1_1).timeout(10, TimeUnit.SECONDS);
        return request;
    }

    private Request httpLocalRequestBuilder(String url, HttpMethod method) {
        Request request = httpClient.newRequest(url)
                // .header(HttpHeader.HOST, intellifireBindingConstants.HTTP_HEADERS_HOST)
                // .header(HttpHeader.CONNECTION, intellifireBindingConstants.HTTP_HEADERS_CONNECTION)
                // .agent("IntellifireTouch/1 CFNetwork/1492.0.1 Darwin/23.3.0")
                .agent("openHAB").method(method)
                // .header(HttpHeader.ACCEPT, intellifireBindingConstants.HTTP_HEADERS_ACCEPT)
                // .header(HttpHeader.ACCEPT_LANGUAGE, intellifireBindingConstants.HTTP_HEADERS_lANGUAGE)
                // .header(HttpHeader.ACCEPT_ENCODING, intellifireBindingConstants.HTTP_HEADERS_ACCEPTENCODING)
                // .version(HttpVersion.HTTP_1_1)
                .timeout(10, TimeUnit.SECONDS);
        return request;
    }

    private void addCookie(CookieStore cookieStore, String name, String value, String domain) {
        HttpCookie cookie = new HttpCookie(name, value);
        cookie.setDomain("." + domain);
        cookie.setPath("/");
        cookieStore.add(URI.create("https://" + domain), cookie);
    }

    private Request sendRequestBuilder(String url, HttpMethod method) {
        return this.httpClient.newRequest(url).agent("NextGenForIPhone/16565 CFNetwork/887 Darwin/17.0.0")
                .method(method).header(HttpHeader.ACCEPT_LANGUAGE, "en-us").header(HttpHeader.ACCEPT, "*/*")
                .header(HttpHeader.ACCEPT_ENCODING, "gzip, deflate").version(HttpVersion.HTTP_1_1)
                .header(HttpHeader.CONNECTION, "keep-alive").header(HttpHeader.HOST, "www.haywardomnilogic.com:80")
                .timeout(10, TimeUnit.SECONDS);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (intellifireBindingConstants.CHANNEL_FIREPLACE_FLAME.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    private String getCallingMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];
        return e.getMethodName();
    }
}
