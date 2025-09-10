/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
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
package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardAccount;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardDynamicStateDescriptionProvider;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardTypeToRequest;
import org.openhab.binding.haywardomnilogiclocal.internal.config.HaywardConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.discovery.HaywardDiscoveryService;
import org.openhab.binding.haywardomnilogiclocal.internal.net.UdpClient;
import org.openhab.binding.haywardomnilogiclocal.internal.net.UdpRequest;
import org.openhab.binding.haywardomnilogiclocal.internal.net.UdpResponse;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescriptionFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The {@link HaywardBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public class HaywardBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(HaywardBridgeHandler.class);
    private static final int MSG_TYPE_REQUEST = 1;
    private static final int MSG_TYPE_TELEMETRY = HaywardMessageType.MSP_TELEMETRY_UPDATE.getMsgInt();
    private static final int UDP_PORT = 10444;

    private final HaywardDynamicStateDescriptionProvider stateDescriptionProvider;
    private @Nullable UdpClient udpClient;
    private @Nullable ScheduledFuture<?> initializeFuture;
    private @Nullable ScheduledFuture<?> pollTelemetryFuture;
    private @Nullable ScheduledFuture<?> pollAlarmsFuture;
    private int commFailureCount;
    private HaywardConfig config = getConfig().as(HaywardConfig.class);
    private final HaywardAccount account = getConfig().as(HaywardAccount.class);

    public HaywardConfig getBridgeConfig() {
        return config;
    }

    public HaywardAccount getAccount() {
        return account;
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(HaywardDiscoveryService.class);
    }

    public HaywardBridgeHandler(HaywardDynamicStateDescriptionProvider stateDescriptionProvider, Bridge bridge) {
        super(bridge);
        this.stateDescriptionProvider = stateDescriptionProvider;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void dispose() {
        clearPolling(initializeFuture);
        clearPolling(pollTelemetryFuture);
        clearPolling(pollAlarmsFuture);
        logger.trace("Hayward polling cancelled");
        super.dispose();
    }

    @Override
    public void initialize() {
        initializeFuture = scheduler.schedule(() -> {
            try {
                scheduledInitialize();
            } catch (UnknownHostException e) {
                logger.error("Initialization failed", e);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void scheduledInitialize() throws UnknownHostException {
        config = getConfigAs(HaywardConfig.class);
        udpClient = new UdpClient(config.getEndpointUrl(), UDP_PORT);

        try {
            clearPolling(pollTelemetryFuture);
            clearPolling(pollAlarmsFuture);

            if (!requestConfiguration()) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Unable to complete UDP handshake");
                clearPolling(pollTelemetryFuture);
                clearPolling(pollAlarmsFuture);
                commFailureCount = 50;
                initPolling(60);
                return;
            }

            if (this.thing.getStatus() != ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            }

            logger.debug("Successfully opened connection to Hayward controller: {}", config.getEndpointUrl());

            initPolling(0);
            logger.trace("Hayward Telemetry polling scheduled");

            if (config.getAlarmPollTime() > 0) {
                initAlarmPolling(1);
            }
        } catch (HaywardException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR,
                    "scheduledInitialize exception: " + e.getMessage());
            clearPolling(pollTelemetryFuture);
            clearPolling(pollAlarmsFuture);
            commFailureCount = 50;
            initPolling(60);
        }
    }

    private synchronized boolean requestConfiguration() throws HaywardException {
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request xmlns=\"http://nextgen.hayward.com/api\"><Name>RequestConfiguration</Name></Request>";

        String xmlResponse = udpXmlResponse(xmlRequest, MSG_TYPE_REQUEST);

        if (xmlResponse.isEmpty()) {
            logger.debug("Hayward Connection thing: Handshake XML response was null");
            return false;
        }

        if (!evaluateXPath("/Response/Parameters//Parameter[@name='StatusMessage']/text()", xmlResponse).isEmpty()) {
            logger.debug("Hayward Connection thing: Handshake XML response: {}", xmlResponse);
            return false;
        }

        logger.debug("Hayward Connection thing: Handshake successful");
        return true;
    }

    public synchronized boolean getTelemetryData() throws HaywardException {
        String urlParameters = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Name>GetTelemetryData</Name><Parameters/></Request>";

        String xmlResponse = udpXmlResponse(urlParameters, MSG_TYPE_TELEMETRY);

        if (xmlResponse.isEmpty()) {
            logger.debug("Hayward Connection thing: getTelemetry XML response was null");
            return false;
        }

        if (!evaluateXPath("/Response/Parameters//Parameter[@name='StatusMessage']/text()", xmlResponse).isEmpty()) {
            logger.debug("Hayward Connection thing: getTelemetry XML response: {}", xmlResponse);
            return false;
        }

        for (Thing thing : getThing().getThings()) {
            if (thing.getHandler() instanceof HaywardThingHandler) {
                HaywardThingHandler handler = (HaywardThingHandler) thing.getHandler();
                if (handler != null) {
                    handler.getTelemetry(xmlResponse);
                }
            }
        }
        return true;
    }

    public synchronized boolean getAlarmList() throws HaywardException {
        for (Thing thing : getThing().getThings()) {
            Map<String, String> properties = thing.getProperties();
            if ("BACKYARD".equals(properties.get(HaywardBindingConstants.PROPERTY_TYPE))) {
                HaywardBackyardHandler handler = (HaywardBackyardHandler) thing.getHandler();
                if (handler != null) {
                    String systemID = properties.get(HaywardBindingConstants.PROPERTY_SYSTEM_ID);
                    if (systemID != null) {
                        return handler.getAlarmList(systemID);
                    }
                }
            }
        }
        return false;
    }

    private synchronized void initPolling(int initalDelay) {
        pollTelemetryFuture = scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (commFailureCount >= 5) {
                    commFailureCount = 0;
                    clearPolling(pollTelemetryFuture);
                    clearPolling(pollAlarmsFuture);
                    initialize();
                    return;
                }
                if (!(getTelemetryData())) {
                    commFailureCount++;
                    return;
                }
                updateStatus(ThingStatus.ONLINE);
            } catch (HaywardException e) {
                logger.debug("Hayward Connection thing: Exception during poll: {}", e.getMessage());
            }
        }, initalDelay, config.getTelemetryPollTime(), TimeUnit.SECONDS);
    }

    private synchronized void initAlarmPolling(int initalDelay) {
        pollAlarmsFuture = scheduler.scheduleWithFixedDelay(() -> {
            try {
                getAlarmList();
            } catch (HaywardException e) {
                logger.debug("Hayward Connection thing: Exception during poll: {}", e.getMessage());
            }
        }, initalDelay, config.getAlarmPollTime(), TimeUnit.SECONDS);
    }

    private void clearPolling(@Nullable ScheduledFuture<?> pollJob) {
        if (pollJob != null) {
            pollJob.cancel(false);
        }
    }

    @Nullable
    Thing getThingForType(HaywardTypeToRequest type, int num) {
        for (Thing thing : getThing().getThings()) {
            Map<String, String> properties = thing.getProperties();
            if (Integer.toString(num).equals(properties.get(HaywardBindingConstants.PROPERTY_SYSTEM_ID))) {
                if (type.toString().equals(properties.get(HaywardBindingConstants.PROPERTY_TYPE))) {
                    return thing;
                }
            }
        }
        return null;
    }

    public List<String> evaluateXPath(String xpathExp, String xmlResponse) {
        List<String> values = new ArrayList<>();
        try {
            InputSource inputXML = new InputSource(new StringReader(xmlResponse));
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xPath.evaluate(xpathExp, inputXML, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                values.add(nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            logger.warn("XPathExpression exception: {}", e.getMessage());
        }
        return values;
    }

    public synchronized String udpXmlResponse(String xmlRequest, int msgType) throws HaywardException {
        if (logger.isTraceEnabled()) {
            logger.trace("Hayward Connection thing:  {} Hayward UDP command: {}", getCallingMethod(), xmlRequest);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Hayward Connection thing:  {}", getCallingMethod());
        }

        if (udpClient == null) {
            throw new HaywardException("UDP client not initialised");
        }

        try {
            UdpResponse response = udpClient.send(new UdpRequest(msgType, xmlRequest));
            return response.getXml();
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "UDP communication error: " + e.getMessage());
            return "";
        }
    }

    private String getCallingMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];
        return e.getMethodName();
    }

    void updateChannelStateDescriptionFragment(Channel channel, StateDescriptionFragment descriptionFragment) {
        ChannelUID channelId = channel.getUID();
        stateDescriptionProvider.setStateDescriptionFragment(channelId, descriptionFragment);
    }

    public int convertCommand(Command command) {
        if (command == OnOffType.ON) {
            return 1;
        } else {
            return 0;
        }
    }
}
