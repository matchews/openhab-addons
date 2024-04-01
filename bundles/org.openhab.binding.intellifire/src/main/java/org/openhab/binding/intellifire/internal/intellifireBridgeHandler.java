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
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
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
        } catch (InterruptedException e) {
            logger.error("Intellifire fireplace thing", e);
        }
    }

    public synchronized boolean login() throws InterruptedException {
        final Gson gson = new Gson();
        String httpResponse;

        // *****Login to Intellifire server and retrieve cookies
        httpResponse = httpResponseContent(intellifireBindingConstants.URL_LOGIN,
                "username=mmyers75@icloud.com&password=R39gVXg@ng&zQ3^6");

        httpResponse = httpResponseContent(intellifireBindingConstants.URL_GETUSERNAME, "");
        intellifireUsername username = gson.fromJson(httpResponse, intellifireUsername.class);
        logger.info("getUsername: {}", username.username);

        httpResponse = httpResponseContent(intellifireBindingConstants.URL_ENUMLOCATIONS, "");
        intellifireLocations locations = gson.fromJson(httpResponse, intellifireLocations.class);

        httpResponse = httpResponseContent("http://iftapi.net/a/enumfireplaces?location_id=16340519383768891688", "");
        intellifireFireplaces fireplaces = gson.fromJson(httpResponse, intellifireFireplaces.class);

        httpResponse = httpResponseContent("http://iftapi.net/a/8A9F68701D0441F3AE22F4D9A0591841/apppoll", "");
        intellifirePollData pollData = gson.fromJson(httpResponse, intellifirePollData.class);

        return true;
    }

    public synchronized String httpResponseContent(String url, String content) throws InterruptedException {

        for (int retry = 0; retry <= 5; retry++) {
            try {
                for (int i = 0; i < 100; i++) {

                    Request request = httpRequestBuilder(url);

                    cs = httpClient.getCookieStore();
                    // addCookie(cs, "user", "adf", "iftapi.net");

                    // load cookies into request
                    cs.get(intellifireBindingConstants.URI_COOKIE).forEach(cookie -> {
                        request.cookie(cookie);
                    });

                    ContentResponse httpResponse = httpRequestBuilder(url)
                            .content(new StringContentProvider(content), "text/plain;charset=UTF-8")
                            .header(HttpHeader.CONTENT_LENGTH, Integer.toString(content.length())).send();

                    if (httpResponse.getStatus() != 204) {
                        logger.info("Login failed with http status code: {}", httpResponse.getStatus());
                    }

                    logger.info("httpResponseCode: {}", httpResponse.getStatus());
                    logger.info("Headers: {}\tCookies: {}", request.getHeaders(),
                            httpClient.getCookieStore().toString());
                    logger.info("httpResponseContent: {}", httpResponse.getContentAsString());

                    // store any received cookies
                    cs = httpClient.getCookieStore();

                    return httpResponse.getContentAsString();

                }

            } catch (ExecutionException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Unable to resolve host.  Check Hayward hostname and your internet connection. "
                                + e.getMessage());
                return "";
            } catch (TimeoutException e) {
                if (retry >= 2) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "Connection Timeout.  Check Hayward hostname and your internet connection. "
                                    + e.getMessage());
                    return "";
                } else {
                    logger.warn("Hayward Connection thing Timeout:  {} Try:  {} ", getCallingMethod(), retry + 1);
                }
            }
        }
        return "";
    }

    private Request httpRequestBuilder(String url) {
        Request request = httpClient.newRequest(url)
                .header(HttpHeader.HOST, intellifireBindingConstants.HTTP_HEADERS_HOST)
                .header(HttpHeader.CONNECTION, intellifireBindingConstants.HTTP_HEADERS_CONNECTION)
                .agent("IntellifireTouch/1 CFNetwork/1492.0.1 Darwin/23.3.0").method(HttpMethod.POST)
                .header(HttpHeader.ACCEPT, intellifireBindingConstants.HTTP_HEADERS_ACCEPT)
                .header(HttpHeader.ACCEPT_LANGUAGE, intellifireBindingConstants.HTTP_HEADERS_lANGUAGE)
                .header(HttpHeader.ACCEPT_ENCODING, intellifireBindingConstants.HTTP_HEADERS_ACCEPTENCODING)
                .version(HttpVersion.HTTP_1_1).timeout(10, TimeUnit.SECONDS);
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
