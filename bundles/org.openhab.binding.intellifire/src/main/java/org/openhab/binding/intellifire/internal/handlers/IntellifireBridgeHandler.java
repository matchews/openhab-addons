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
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.DatatypeConverter;

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
import org.openhab.binding.intellifire.internal.IntellifireCommand;
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
import com.google.gson.JsonSyntaxException;

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
    private @Nullable ScheduledFuture<?> sendCommandFuture;
    private @Nullable CookieStore cs;
    public IntellifireAccount account = new IntellifireAccount();
    private IntellifireConfiguration config = new IntellifireConfiguration();
    private final LinkedBlockingQueue<IntellifireCommand> commandQueue = new LinkedBlockingQueue<>(20);

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
        // Initializing sequence...
        // Login
        // SetupAccountData
        // getAccountLocations
        // getFireplaces (now we have the apiKey)
        // getUsername
        // cloudPoll
        // initializeApiKeyProperty
        // initPolling

        config = getConfigAs(IntellifireConfiguration.class);

        try {
            if (login() && setupAccountData() && getUsername() && poll(IntellifireBindingConstants.CLOUD_POLLING)) {
                logger.debug("Succesfully opened connection to Intellifire's server: {} Username:{} ",
                        IntellifireBindingConstants.URI_COOKIE, config.username);
                initializeApiKeyProperties();
                initPolling(5);
                logger.trace("Intellifire polling scheduled");
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Unable to Login to Intellifire's server. Retry in 60 seconds");
                clearPolling();
                commFailureCount = 50;
                initPolling(60);
                return;
            }
        } catch (IntellifireException e) {
            logger.error("Intellifire scheduledInitialize exception: {}", e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Intellifire scheduledInitialize exception: {}", e.getMessage());
        } catch (JsonSyntaxException e) {
            logger.error("JsonSyntaxException: {}", e.getMessage());
            return;
        }
    }

    public synchronized boolean login() throws InterruptedException {
        // Login to Intellifire cloud server and retrieve cookies
        String httpResponse = httpResponseContent(IntellifireBindingConstants.URL_LOGIN, HttpMethod.POST,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT,
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

    public synchronized boolean getUsername() throws InterruptedException, JsonSyntaxException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent(IntellifireBindingConstants.URL_GETUSERNAME, HttpMethod.POST,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT, "", 10);
        IntellifireUsername username = gson.fromJson(httpResponse, IntellifireUsername.class);

        if (username != null) {
            account.userName = username.username.toString();
            logger.trace("getUsername: {}", username.username);
            return true;
        } else {
            return false;
        }
    }

    public boolean setupAccountData() throws IntellifireException, InterruptedException {
        if (getAccountLocations()) {
            for (int i = 0; i < account.locations.size(); i++) {
                String locationID = account.locations.get(i).locationId;
                IntellifireLocation fireplaces = getFireplaces(locationID);
                if (fireplaces != null) {
                    account.locations.get(i).fireplaces = fireplaces;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean getAccountLocations() throws InterruptedException, JsonSyntaxException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent(IntellifireBindingConstants.URL_ENUMLOCATIONS, HttpMethod.POST,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT, "", 10);
        IntellifireAccount accountData = gson.fromJson(httpResponse, IntellifireAccount.class);

        if (accountData != null) {
            account.locations = accountData.locations;
            return true;
        } else {
            return false;
        }
    }

    public synchronized @Nullable IntellifireLocation getFireplaces(String locationID)
            throws InterruptedException, JsonSyntaxException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://iftapi.net/a/enumfireplaces?location_id=" + locationID,
                HttpMethod.POST, IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT, "", 10);
        IntellifireLocation fireplaces = gson.fromJson(httpResponse, IntellifireLocation.class);

        return fireplaces;
    }

    public synchronized void initPolling(int initialDelay) {
        logger.debug("Initializing Polling");
        pollTelemetryFuture = scheduler.scheduleWithFixedDelay(() -> {
            try {
                logger.trace("Intellifire initPolling");
                if (commFailureCount >= 5) {
                    commFailureCount = 0;
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    clearPolling();
                    initialize();
                    return;
                }
                if (commFailureCount < 2) {
                    if (!(poll(IntellifireBindingConstants.LOCAL_POLLING))) {
                        commFailureCount++;
                        return;
                    } else {
                        commFailureCount = 0;
                    }
                } else {
                    if (!(poll(IntellifireBindingConstants.CLOUD_POLLING))) {
                        commFailureCount++;
                        return;
                    } else {
                        commFailureCount = 0;
                    }
                }
                if (this.thing.getStatus() != ThingStatus.ONLINE) {
                    commFailureCount = 0;
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (IntellifireException e) {
                logger.error("Intellifire initPolling exception: {}", e.getMessage());
                return;
            } catch (InterruptedException e) {
                logger.error("Intellifire initPolling exception: {}", e.getMessage());
                return;
            } catch (JsonSyntaxException e) {
                logger.error("JsonSyntaxException: {}", e.getMessage());
                return;
            }
        }, initialDelay, config.refreshInterval, TimeUnit.SECONDS);
        return;
    }

    public void clearPolling() {
        logger.debug("Clearing Polling");
        if (pollTelemetryFuture != null) {
            pollTelemetryFuture.cancel(false);
        }
        if (initializeFuture != null) {
            initializeFuture.cancel(false);
        }
    }

    public boolean poll(boolean cloudPoll) throws IntellifireException, InterruptedException, JsonSyntaxException {
        boolean failureFlag = false;

        // Retrieve poll data for each fireplace
        for (int i = 0; i < account.locations.size(); i++) {
            for (int j = 0; j < account.locations.get(i).fireplaces.fireplaces.size(); j++) {
                String serialNumber = account.locations.get(i).fireplaces.fireplaces.get(j).serial;

                if (cloudPoll) {
                    // Cloud Poll
                    IntellifirePollData cloudPollData = cloudPollFireplace(serialNumber);
                    if (cloudPollData != null) {
                        account.locations.get(i).fireplaces.fireplaces.get(j).pollData = cloudPollData;
                    } else {
                        failureFlag = true;
                    }
                } else {
                    // Local Poll
                    String ipAddress = account.getIPAddress(serialNumber);
                    if (!"".equals(ipAddress)) {
                        IntellifirePollData localPollData = localPollFireplace(ipAddress);
                        if (localPollData != null) {
                            account.locations.get(i).fireplaces.fireplaces.get(j).pollData = localPollData;
                            account.locations.get(i).fireplaces.fireplaces.get(j).lastLocalPollSuccesful = true;
                        } else {
                            failureFlag = true;
                        }
                    } else {
                        failureFlag = true;
                        account.locations.get(i).fireplaces.fireplaces.get(j).lastLocalPollSuccesful = false;
                        logger.error("Intellifire local poll failed. Appliance is offline.");
                    }
                }
            }
        }

        boolean lastPollSuccessful = false;

        for (Thing thing : getThing().getThings()) {
            if (thing.getHandler() instanceof IntellifireThingHandler) {
                IntellifireThingHandler handler = (IntellifireThingHandler) thing.getHandler();
                if (handler != null) {
                    String serialNumber = getSerialNumberProperty(thing.getProperties());
                    IntellifirePollData pollData = account.getPollData(serialNumber);
                    lastPollSuccessful = account.getlastLocalPollSuccesful(serialNumber);

                    if (pollData != null && cloudPoll) {
                        handler.poll(pollData);
                    } else if (pollData != null && !cloudPoll && lastPollSuccessful) {
                        handler.updateStatus(ThingStatus.ONLINE);
                        failureFlag = true;
                    } else if (pollData != null && !cloudPoll && !lastPollSuccessful) {
                        handler.updateStatus(ThingStatus.OFFLINE);
                        failureFlag = true;
                    } else {
                        failureFlag = true;
                    }
                }
            }
        }
        return !failureFlag;
    }

    public synchronized @Nullable IntellifirePollData cloudPollFireplace(String serialNumber)
            throws InterruptedException, JsonSyntaxException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://iftapi.net/a/" + serialNumber + "/apppoll", HttpMethod.POST,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT, "", 10);
        IntellifirePollData pollData = gson.fromJson(httpResponse, IntellifirePollData.class);
        return pollData;
    }

    public synchronized @Nullable IntellifirePollData localPollFireplace(String IPaddress)
            throws InterruptedException, JsonSyntaxException {
        final Gson gson = new Gson();

        String httpResponse = httpResponseContent("http://" + IPaddress + "/poll", HttpMethod.GET,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT, "", 10);
        IntellifirePollData pollData = gson.fromJson(httpResponse, IntellifirePollData.class);
        return pollData;
    }

    public void queueCommand(IntellifireCommand command) throws InterruptedException, NoSuchAlgorithmException {
        logger.debug("Queuing {}={} command to serial number: {}", command.cloudCommand, command.value,
                command.serialNumber);

        // Remove any existing/previous commands in the queue
        commandQueue.removeIf(
                i -> i.serialNumber.equals(command.serialNumber) && i.cloudCommand.equals(command.cloudCommand));

        // Add command to the queue
        if (!commandQueue.offer(command)) {
            logger.warn("Maximum command queue size exceeded.");
        }

        if (sendCommandFuture != null) {
            sendCommandFuture.cancel(false);
        }

        sendCommandFuture = scheduler.scheduleWithFixedDelay(() -> {
            try {
                processQueue();
            } catch (InterruptedException e) {
                logger.error("InterruptedException: {}", e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                logger.error("NoSuchAlgorithmException: {}", e.getMessage());
            }
        }, 1000, 500, TimeUnit.MILLISECONDS);
    }

    private void processQueue() throws NoSuchAlgorithmException, InterruptedException {
        IntellifireCommand command = commandQueue.poll();
        if (command != null) {
            logger.debug("Sending {}={} command to serial number: {}", command.cloudCommand, command.value,
                    command.serialNumber);
            sendCommand(command);
        }
        // Cancel futures is queue is empty
        if (commandQueue.isEmpty()) {
            if (sendCommandFuture != null) {
                sendCommandFuture.cancel(false);
            }
        }
    }

    private synchronized void sendCommand(IntellifireCommand command)
            throws InterruptedException, NoSuchAlgorithmException {
        // Pause polling while sending command
        clearPolling();

        // Try local command
        String localResponse = sendLocalCommand(command.ipAddress, command.apiKey, command.localCommand, command.value);

        // If local command fails, try cloud command.
        if (("204").equals(localResponse)) {
            // Success. Restart polling
            initPolling(5);
            return;
        } else {
            logger.warn("Local command {} failed.  Attemping cloud command.", command.localCommand);
            // Cloud Command
            String cloudResponse = sendCloudCommand(command.serialNumber, command.cloudCommand, command.value);

            // Log cloud error
            if (!("204").equals(cloudResponse)) {
                logger.warn("Cloud command {} failed.", command.cloudCommand);
            }
            // Restart polling
            initPolling(5);
            return;
        }
    }

    private String sendCloudCommand(String serialNumber, String cloudCommand, String value)
            throws InterruptedException {
        String cloudCmdURL = "http://iftapi.net/a/" + serialNumber + "/apppost";
        String content = cloudCommand + "=" + value;
        String response = httpResponseContent(cloudCmdURL, HttpMethod.POST,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPETEXT, content, 10);
        return response;
    }

    private String sendLocalCommand(String IPaddress, String apiKeyHexString, String command, String value)
            throws InterruptedException, NoSuchAlgorithmException {
        String localCmdURL = "http://" + IPaddress + "/post";

        // Get challenge string from local fireplace
        String challengeHexStr = getChallengeString(IPaddress);
        logger.trace("Challenge string: {} received.", challengeHexStr);

        // Assemble command string
        String commandStr = "post:command=" + command + "&value=" + value;
        logger.trace("Command string: {}", commandStr);
        logger.trace("API key hex string: {}", apiKeyHexString);

        // Concatenate apiKey, challenge, command
        byte[] apiKeyBytes = decodeHexString(apiKeyHexString);
        byte[] challengeBytes = decodeHexString(challengeHexStr);
        byte[] commandBytes = commandStr.getBytes();

        byte[] apiChallengePayloadBytes = null;
        ByteBuffer buffer1 = ByteBuffer.allocate(apiKeyBytes.length + challengeBytes.length + commandBytes.length);
        buffer1.put(apiKeyBytes);
        buffer1.put(challengeBytes);
        buffer1.put(commandBytes);
        apiChallengePayloadBytes = buffer1.array();

        // Create hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] apiChallengePayloadHash = digest.digest(apiChallengePayloadBytes);

        // Prepend the apiKey (yes, another copy of the apiKey)
        byte[] apiApiChallengePayload = null;
        ByteBuffer buffer = ByteBuffer.allocate(apiKeyBytes.length + apiChallengePayloadHash.length);
        buffer.put(apiKeyBytes);
        buffer.put(apiChallengePayloadHash);
        apiApiChallengePayload = buffer.array();

        // Hash again
        byte[] apiApiChallengePayloadHash = digest.digest(apiApiChallengePayload);

        String responseHexString = encodeHexString(apiApiChallengePayloadHash);

        logger.trace("Username: {}", account.userName);

        // Hash the username and convert to hex string
        byte[] usernameHash = digest.digest(account.userName.getBytes());
        String userNameHexString = encodeHexString(usernameHash);

        // Assemble the url encoded form
        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("command", command);
        dataMap.put("value", value);
        dataMap.put("user", userNameHexString.toString().toUpperCase());
        dataMap.put("response", responseHexString.toString().toLowerCase());

        String data = "";
        for (Map.Entry<String, String> e : dataMap.entrySet()) {
            data = data + (data.isEmpty() ? "" : "&");
            data = data + e.getKey() + "=" + e.getValue();
        }

        String response = httpResponseContent(localCmdURL, HttpMethod.POST,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPEURLENCODED, data, 10);

        return response;
    }

    private synchronized String httpResponseContent(String url, HttpMethod method, String contentType, String content,
            int timeout) throws InterruptedException {
        for (int retry = 1; retry <= 2; retry++) {
            try {
                // Initialize request to load cookies into
                Request request = httpRequestBuilder(url, method, timeout, contentType);

                cs = httpClient.getCookieStore();

                // load any stored cookies into the request
                if (cs != null) {
                    cs.get(IntellifireBindingConstants.URI_COOKIE).forEach(cookie -> {
                        request.cookie(cookie);
                    });
                }
                ContentResponse httpResponse = httpRequestBuilder(url, method, timeout, contentType)
                        .content(new StringContentProvider(content), contentType)
                        .header(HttpHeader.CONTENT_LENGTH, Integer.toString(content.length())).send();

                int httpResponseStatusCode = httpResponse.getStatus();

                // Login returns 204, all others return 200
                if (httpResponseStatusCode != 200 && httpResponseStatusCode != 204) {
                    logger.warn("{} http response: {} {}", getCallingMethod(), httpResponseStatusCode, content);
                } else {
                    logger.debug("{} http response: {} {}", getCallingMethod(), httpResponseStatusCode, content);
                }

                logger.trace("Headers:\n{}", request.getHeaders());
                logger.trace("Cookies: {}", httpClient.getCookieStore().getCookies().toString());

                if (!httpResponse.getContentAsString().isEmpty()) {
                    logger.trace("{} http response: {} {}", getCallingMethod(), httpResponseStatusCode, content);
                }

                // store any received cookies
                cs = httpClient.getCookieStore();

                if (getCallingMethod().equals("login") || getCallingMethod().equals("sendCloudCommand")
                        || getCallingMethod().equals("sendLocalCommand")) {
                    return Integer.toString(httpResponseStatusCode);
                } else {
                    return httpResponse.getContentAsString();
                }
            } catch (ExecutionException | TimeoutException e) {
                logger.warn("Intellifire {} timeout. Attempt #{}", getCallingMethod(),
                        (commFailureCount) * 2 + (retry));

                if (retry >= 2) {
                    return "";
                }
            }
        }
        return "";
    }

    private Request httpRequestBuilder(String url, HttpMethod method, int timeout, String contentType) {
        Request request = httpClient.newRequest(url)
                .header(HttpHeader.HOST, IntellifireBindingConstants.HTTP_HEADERS_HOST)
                .header(HttpHeader.CONNECTION, IntellifireBindingConstants.HTTP_HEADERS_CONNECTION)
                .header(HttpHeader.CONTENT_TYPE, contentType)
                .agent("openHAB Intellifire Binding org.openhab.binding.intellifire").method(method)
                .header(HttpHeader.ACCEPT, IntellifireBindingConstants.HTTP_HEADERS_ACCEPT)
                .header(HttpHeader.ACCEPT_LANGUAGE, IntellifireBindingConstants.HTTP_HEADERS_LANGUAGE)
                .header(HttpHeader.ACCEPT_ENCODING, IntellifireBindingConstants.HTTP_HEADERS_ACCEPTENCODING)
                .version(HttpVersion.HTTP_1_1).timeout(timeout, TimeUnit.SECONDS);
        return request;
    }

    private String getChallengeString(String IPaddress) throws InterruptedException {
        String challengeStr = httpResponseContent("http://" + IPaddress + "/get_challenge", HttpMethod.GET,
                IntellifireBindingConstants.HTTP_HEADERS_CONTENTTYPEURLENCODED, "", 10);
        return challengeStr;
    }

    private String encodeHexString(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    private byte[] decodeHexString(String hexString) {
        return DatatypeConverter.parseHexBinary(hexString);
    }

    public String getApiKeyProperty(Map<String, String> properties) throws InterruptedException, IntellifireException {
        String apiKey = properties.get(IntellifireBindingConstants.PROPERTY_APIKEY);
        if (apiKey != null) {
            return apiKey;
        } else {
            return "";
        }
    }

    public void initializeApiKeyProperties() {
        for (Thing thing : getThing().getThings()) {
            if (thing.getHandler() instanceof IntellifireThingHandler) {
                IntellifireThingHandler handler = (IntellifireThingHandler) thing.getHandler();
                if (handler != null) {
                    String serialNumber = getSerialNumberProperty(thing.getProperties());
                    String locationID = getLocationIDProperty(thing.getProperties());
                    handler.updateApiKey(locationID, serialNumber, account);
                }
            }
        }
    }

    public String getIPAddressProperty(Map<String, String> properties) {
        String ipAddress = properties.get(IntellifireBindingConstants.PROPERTY_IPADDRESS);
        if (ipAddress != null) {
            return ipAddress;
        } else {
            return "";
        }
    }

    public String getLocationIDProperty(Map<String, String> properties) {
        String locationID = properties.get(IntellifireBindingConstants.PROPERTY_LOCATIONID);
        if (locationID != null) {
            return locationID;
        } else {
            return "";
        }
    }

    public String getSerialNumberProperty(Map<String, String> properties) {
        String serialNumber = properties.get(IntellifireBindingConstants.PROPERTY_SERIALNUMBER);
        if (serialNumber != null) {
            return serialNumber;
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
        logger.debug("Dispose: Intellifire polling cancelled");

        if (sendCommandFuture != null) {
            sendCommandFuture.cancel(false);
        }

        super.dispose();
    }
}
