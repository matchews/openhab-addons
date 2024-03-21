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

import static org.openhab.binding.intellifire.internal.intellifireBindingConstants.CHANNEL_FIREPLACE_FLAME;

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
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link intellifireHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class intellifireHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(intellifireHandler.class);
    private @Nullable intellifireConfiguration config;
    private final HttpClient httpClient;
    private @Nullable ScheduledFuture<?> initializeFuture;
    private @Nullable ScheduledFuture<?> pollTelemetryFuture;
    private @Nullable ScheduledFuture<?> pollAlarmsFuture;
    private int commFailureCount;

    public intellifireHandler(Thing thing, HttpClient httpClient) {
        super(thing);
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
        String xmlResponse;
        String status;

        config.hostname = "http://iftapi.net/";
        // 54.186.69.10

        /*
         * data = {"username": username, "password": password}
         *
         *
         * PACKAGE_VERSION = importlib.metadata.version("intellifire4py")
         * USER_AGENT = f"intellifire4py/{PACKAGE_VERSION}"
         *
         *
         * headers={"user-agent": USER_AGENT}
         * self._session.post( # type: ignore[union-attr]
         * f"{self.prefix}://iftapi.net/a/login", data=data
         */

        // *****Login to Hayward server
        String urlParameters = "username=mmyers75@icloud.com&password=R39gVXg@ng&zQ3^6";

        String test = httpXmlResponse(urlParameters);
        return true;
    }

    public synchronized String httpXmlResponse(String urlParameters) throws InterruptedException {
        String urlParameterslength = Integer.toString(urlParameters.length());
        String statusMessage;
        Request request;

        for (int retry = 0; retry <= 5; retry++) {
            try {
                for (int i = 0; i < 100; i++) {

                    /*
                     * for (Enumeration<?> e = headers.keys(); e.hasMoreElements();) {
                     * String key = (String) e.nextElement();
                     * String val = (String) headers.get(key);
                     * request.header(key, val);
                     * }
                     */
                    /*
                     * httpClient.newRequest(uri, httpMethod, httpContent, httpContentType).thenAccept(request -> {
                     * request.timeout(timeout, TimeUnit.MILLISECONDS);
                     * headers.forEach(request::header);
                     */
                    config.hostname = "http://iftapi.net/a/login";

                    request = httpClient.newRequest(config.hostname).header(HttpHeader.HOST, "iftapi.net")
                            .header(HttpHeader.CONNECTION, "keep-alive")
                            .agent("IntellifireTouch/1 CFNetwork/1492.0.1 Darwin/23.3.0").method(HttpMethod.POST)
                            .header(HttpHeader.ACCEPT, "*/*").header(HttpHeader.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
                            .header(HttpHeader.ACCEPT_ENCODING, "gzip, deflate").version(HttpVersion.HTTP_1_1)
                            .timeout(10, TimeUnit.SECONDS);

                    ContentResponse httpResponse = request
                            .content(new StringContentProvider(urlParameters), "text/plain;charset=UTF-8")
                            .header(HttpHeader.CONTENT_LENGTH, urlParameterslength).send();

                    CookieStore cs = httpClient.getCookieStore();
                    // addCookie(cs, "user", "adf", "iftapi.net");

                    if (httpResponse.getStatus() != 204) {
                        logger.info("Login failed with http status code: {}", httpResponse.getStatus());
                    }

                    logger.info("httpResponseCode: {}", httpResponse.getStatus());
                    logger.info("Headers: {}\tCookies: {}", request.getHeaders(), request.getCookies());
                    logger.info("httpResponseContent: {}", httpResponse.getContentAsString());

                    config.hostname = "http://iftapi.net/a/enumlocations";
                    config.hostname = "http://iftapi.net/a/getusername";

                    Request request2 = httpClient.newRequest(config.hostname).header(HttpHeader.HOST, "iftapi.net")
                            .header(HttpHeader.CONNECTION, "keep-alive")
                            .agent("IntellifireTouch/1 CFNetwork/1492.0.1 Darwin/23.3.0").method(HttpMethod.POST)
                            .header(HttpHeader.ACCEPT, "*/*").header(HttpHeader.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
                            .header(HttpHeader.ACCEPT_ENCODING, "gzip, deflate").version(HttpVersion.HTTP_1_1)
                            .timeout(10, TimeUnit.SECONDS);

                    final URI baseUri = URI.create("http://iftapi.net");

                    cs.get(baseUri).forEach(cookie -> {
                        request2.cookie(cookie);
                    });

                    httpResponse = request2
                            .content(new StringContentProvider(urlParameters), "text/plain;charset=UTF-8")
                            .header(HttpHeader.CONTENT_LENGTH, urlParameterslength).send();

                    if (httpResponse.getStatus() != 200) {
                        logger.info("Login failed with http status code: {}", httpResponse.getStatus());
                    }

                    logger.info("httpResponseCode: {}", httpResponse.getStatus());
                    logger.info("Headers: {}\tCookies: {}", request.getHeaders(), request.getCookies());
                    logger.info("httpResponseContent: {}", httpResponse.getContentAsString());

                    final Gson gson = new Gson();

                    getUsername getUsername = gson.fromJson(httpResponse.getContentAsString(), getUsername.class);

                    // KodiUniqueID uniqueID = gson.fromJson(item.get(PROPERTY_UNIQUEID), KodiUniqueID.class);
                    logger.info("getUsername: {}", getUsername.username);
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
        if (CHANNEL_FIREPLACE_FLAME.equals(channelUID.getId())) {
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
