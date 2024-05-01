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
package org.openhab.binding.intellifire.internal.handlers;

import java.net.CookieStore;
import java.util.Collection;
import java.util.Map;
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
import org.openhab.binding.intellifire.internal.IntellifireAccount;
import org.openhab.binding.intellifire.internal.IntellifireBindingConstants;
import org.openhab.binding.intellifire.internal.IntellifireConfiguration;
import org.openhab.binding.intellifire.internal.IntellifireException;
import org.openhab.binding.intellifire.internal.IntellifireLocation;
import org.openhab.binding.intellifire.internal.IntellifirePollData;
import org.openhab.binding.intellifire.internal.IntellifireUsername;
import org.openhab.binding.intellifire.internal.discovery.IntellifireDiscoveryService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link IntellifireBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class IntellifireBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(IntellifireBridgeHandler.class);
    private final HttpClient httpClient;
    private int commFailureCount;
    private @Nullable ScheduledFuture<?> initializeFuture;
    private @Nullable ScheduledFuture<?> pollTelemetryFuture;
    private @Nullable CookieStore cs;
    public IntellifireAccount account = new IntellifireAccount();
    private IntellifireConfiguration config = new IntellifireConfiguration();

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(IntellifireDiscoveryService.class);
    }

    public IntellifireBridgeHandler(Bridge bridge, HttpClient httpClient) {
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
        config = getConfigAs(IntellifireConfiguration.class);

        try {
            if (login() && setupAccountData() && poll(IntellifireBindingConstants.CLOUD_POLLING)) {
                logger.debug("Succesfully opened connection to Intellifire's server: {} Username:{} ",
                        IntellifireBindingConstants.URI_COOKIE, config.username);
                initPolling(0);
                logger.trace("Intellifire polling scheduled");
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Unable to Login to Intellifire's server");
                clearPolling();
                commFailureCount = 50;
                initPolling(60);
                return;
            }
        } catch (InterruptedException e) {
            logger.error("Intellifire fireplace thing", e);
        } catch (IntellifireException e) {
            logger.error("Intellifire fireplace thing", e);
        }
    }

    public synchronized void initPolling(int initalDelay) {
        pollTelemetryFuture = scheduler.scheduleWithFixedDelay(() -> {
            try {
                logger.trace("Intellifire Polling");
                if (commFailureCount >= 5) {
                    commFailureCount = 0;
                    clearPolling();
                    initialize();
                    return;
                }

                if (!(poll(IntellifireBindingConstants.LOCAL_POLLING))) {
                    commFailureCount++;
                    return;
                }
                if (this.thing.getStatus() != ThingStatus.ONLINE) {
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (InterruptedException e) {
                logger.debug("Intellifire Account thing: Exception during poll: {}", e.getMessage());
                return;
            } catch (IntellifireException e) {
                logger.debug("Intellifire Account thing: Exception during poll: {}", e.getMessage());
                return;
            }
        }, initalDelay, config.refreshInterval, TimeUnit.SECONDS);
        return;
    }

    public void clearPolling() {
        if (pollTelemetryFuture != null) {
            pollTelemetryFuture.cancel(false);
        }
        if (initializeFuture != null) {
            initializeFuture.cancel(false);
        }
    }

    public boolean setupAccountData() throws IntellifireException, InterruptedException {
        getAccountLocations();
        for (int i = 0; i < account.locations.size(); i++) {
            String locationID = account.locations.get(i).locationId;
            IntellifireLocation fireplaces = getFireplaces(locationID);
            if (fireplaces != null) {
                account.locations.get(i).fireplaces = fireplaces;
            }
        }
        return true;
    }

    public boolean poll(boolean cloudPool) throws IntellifireException, InterruptedException {
        // Retrieve poll data for each fireplace
        for (int i = 0; i < account.locations.size(); i++) {
            for (int j = 0; j < account.locations.get(i).fireplaces.fireplaces.size(); j++) {
                String serialNumber = account.locations.get(i).fireplaces.fireplaces.get(j).serial;

                if (cloudPool) {
                    // Cloud Poll
                    IntellifirePollData cloudPollData = cloudPollFireplace(serialNumber);
                    if (cloudPollData != null) {
                        account.locations.get(i).fireplaces.fireplaces.get(j).pollData = cloudPollData;
                    }
                } else {
                    // Local Poll
                    String ipAddress = account.getIPAddress(serialNumber);
                    IntellifirePollData localPollData = localPollFireplace(ipAddress);
                    if (localPollData != null) {
                        account.locations.get(i).fireplaces.fireplaces.get(j).pollData = localPollData;
                    }
                }
            }
        }

        for (Thing thing : getThing().getThings()) {
            if (thing.getHandler() instanceof IntellifireThingHandler) {
                IntellifireThingHandler handler = (IntellifireThingHandler) thing.getHandler();
                if (handler != null) {
                    String thingSerialNumber = getSerialNumberProperty(thing.getProperties());
                    IntellifirePollData pollData = account.getPollData(thingSerialNumber);
                    if (pollData != null) {
                        handler.poll(pollData);
                    }
                }
            }
        }

        return true;
    }

    public synchronized boolean login() throws InterruptedException {
        // Login to Intellifire cloud server and retrieve cookies
        String httpResponse = httpResponseContent(IntellifireBindingConstants.URL_LOGIN, HttpMethod.POST,
                "username=" + config.username + "&password=" + config.password, 10);
        if ("204".equals(httpResponse)) {
            return true;
        } else if ("422".equals(httpResponse)) {
            logger.warn("Login failed.  Check username and password.");
            return false;
        } else {
            return false;
        }
    }

    public synchronized IntellifireUsername getUsername() throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent(IntellifireBindingConstants.URL_GETUSERNAME, HttpMethod.POST, "", 10);
        IntellifireUsername username = gson.fromJson(httpResponse, IntellifireUsername.class);
        logger.trace("getUsername: {}", username.username);

        return username;
    }

    public synchronized void getAccountLocations() throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent(IntellifireBindingConstants.URL_ENUMLOCATIONS, HttpMethod.POST, "",
                10);
        IntellifireAccount accountData = gson.fromJson(httpResponse, IntellifireAccount.class);

        if (accountData != null) {
            account = accountData;
        }
    }

    public synchronized @Nullable IntellifireLocation getFireplaces(String locationID) throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://iftapi.net/a/enumfireplaces?location_id=" + locationID,
                HttpMethod.POST, "", 10);
        IntellifireLocation fireplaces = gson.fromJson(httpResponse, IntellifireLocation.class);

        return fireplaces;
    }

    public synchronized @Nullable IntellifirePollData cloudPollFireplace(String serialNumber)
            throws IntellifireException, InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://iftapi.net/a/" + serialNumber + "/apppoll", HttpMethod.POST,
                "", 10);
        IntellifirePollData pollData = gson.fromJson(httpResponse, IntellifirePollData.class);

        return pollData;
    }

    public synchronized @Nullable IntellifirePollData localPollFireplace(String IPaddress) throws InterruptedException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://" + IPaddress + "/poll", HttpMethod.GET, "", 10);
        IntellifirePollData pollData = gson.fromJson(httpResponse, IntellifirePollData.class);

        return pollData;
    }

    public synchronized String httpResponseContent(String url, HttpMethod method, String content, int timeout)
            throws InterruptedException {
        for (int retry = 0; retry <= 5; retry++) {
            try {
                Request request = httpRequestBuilder(url, method, timeout);

                cs = httpClient.getCookieStore();

                // load any stored cookies into the request
                if (cs != null) {
                    cs.get(IntellifireBindingConstants.URI_COOKIE).forEach(cookie -> {
                        request.cookie(cookie);
                    });
                }
                ContentResponse httpResponse = httpRequestBuilder(url, method, timeout)
                        .content(new StringContentProvider(content), "text/plain;charset=UTF-8")
                        .header(HttpHeader.CONTENT_LENGTH, Integer.toString(content.length())).send();

                int httpResponseStatusCode = httpResponse.getStatus();
                if (httpResponseStatusCode != 200 && httpResponseStatusCode != 204 && httpResponseStatusCode != 408) {
                    logger.warn("{} failed with http response: {}", getCallingMethod(), httpResponse);
                } else {
                    logger.debug("{} http response: {}", getCallingMethod(), httpResponse);
                }

                logger.trace("Headers:\n{}", request.getHeaders());
                logger.trace("Cookies: {}", httpClient.getCookieStore().getCookies().toString());

                if (!httpResponse.getContentAsString().isEmpty()) {
                    logger.trace("{} httpResponseContent: {}", getCallingMethod(), httpResponse.getContentAsString());
                }

                // store any received cookies
                cs = httpClient.getCookieStore();

                if (getCallingMethod().equals("login") || getCallingMethod().equals("handleCommand")
                        || httpResponseStatusCode == 408) {
                    return Integer.toString(httpResponseStatusCode);
                } else {
                    return httpResponse.getContentAsString();
                }
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

    private Request httpRequestBuilder(String url, HttpMethod method, int timeout) {
        Request request = httpClient.newRequest(url)
                .header(HttpHeader.HOST, IntellifireBindingConstants.HTTP_HEADERS_HOST)
                .header(HttpHeader.CONNECTION, IntellifireBindingConstants.HTTP_HEADERS_CONNECTION)
                .agent("openHAB Intellifire Binding org.openhab.binding.intellifire").method(method)
                .header(HttpHeader.ACCEPT, IntellifireBindingConstants.HTTP_HEADERS_ACCEPT)
                .header(HttpHeader.ACCEPT_LANGUAGE, IntellifireBindingConstants.HTTP_HEADERS_LANGUAGE)
                .header(HttpHeader.ACCEPT_ENCODING, IntellifireBindingConstants.HTTP_HEADERS_ACCEPTENCODING)
                .version(HttpVersion.HTTP_1_1).timeout(timeout, TimeUnit.SECONDS);
        return request;
    }

    public String getSerialNumberProperty(Map<String, String> properties) {
        String serialNumber = properties.get(IntellifireBindingConstants.PROPERTY_SERIALNUMBER);
        if (serialNumber != null) {
            return serialNumber;
        } else {
            return "";
        }
    }

    public String getIPAddressProperty(Map<String, String> properties) {
        String ipAddress = properties.get(IntellifireBindingConstants.PROPERTY_FIREPLACE_IPADDRESS);
        if (ipAddress != null) {
            return ipAddress;
        } else {
            return "";
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    private String getCallingMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];
        return e.getMethodName();
    }

    @Override
    public void dispose() {
        clearPolling();
        logger.trace("Intellifire polling cancelled");
        super.dispose();
    }
}
