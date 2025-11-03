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
import java.util.HashMap;
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
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.Config;
import org.openhab.binding.haywardomnilogiclocal.internal.DynamicStateDescriptionProvider;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.TypeToRequest;
import org.openhab.binding.haywardomnilogiclocal.internal.config.ConfigParser;
import org.openhab.binding.haywardomnilogiclocal.internal.config.MspConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.discovery.HaywardDiscoveryService;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.net.UdpClient;
import org.openhab.binding.haywardomnilogiclocal.internal.net.UdpMessage;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescriptionFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The {@link BridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public class BridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(BridgeHandler.class);
    private static final int UDP_PORT = 10444;

    private final DynamicStateDescriptionProvider stateDescriptionProvider;
    private @Nullable UdpClient udpClient;
    private @Nullable ScheduledFuture<?> initializeFuture;
    private @Nullable ScheduledFuture<?> pollTelemetryFuture;
    private @Nullable ScheduledFuture<?> pollAlarmsFuture;
    private int commFailureCount;
    private Config config = getConfig().as(Config.class);
    @Nullable
    private MspConfig mspConfig;
    public String units = "Standard";

    public void updatePropertiez(Map<String, String> bridgeProps) {
        BridgeBuilder thingBuilder = editThing();
        thingBuilder.withProperties(bridgeProps);
        updateThing(thingBuilder.build());
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(HaywardDiscoveryService.class);
    }

    public BridgeHandler(DynamicStateDescriptionProvider stateDescriptionProvider, Bridge bridge) {
        super(bridge);
        this.stateDescriptionProvider = stateDescriptionProvider;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
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

    @Override
    public void dispose() {
        clearPolling(initializeFuture);
        clearPolling(pollTelemetryFuture);
        clearPolling(pollAlarmsFuture);
        logger.trace("Hayward polling cancelled");
        super.dispose();
    }

    public void scheduledInitialize() throws UnknownHostException {
        config = getConfigAs(Config.class);
        udpClient = new UdpClient(config.getEndpointUrl(), UDP_PORT);

        try {
            clearPolling(pollTelemetryFuture);
            clearPolling(pollAlarmsFuture);

            if (requestConfiguration().isEmpty() || !requestTelemetryData()) {
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

    public synchronized String requestConfiguration() throws HaywardException {
        String xmlRequest = CommandBuilder.buildRequestConfiguration();
        String xmlResponse = sendRequest(xmlRequest, MessageType.REQUEST_CONFIGURATION);

        if (xmlResponse.isEmpty()) {
            logger.debug("Hayward Connection thing: RequestConfiguration XML response was null");
            throw new HaywardException("MSP configuration response empty");
        }

        setMspConfig(ConfigParser.parse(xmlResponse));
        logger.debug("Hayward Connection thing: RequestConfiguration successful");
        return xmlResponse;
    }

    public synchronized boolean requestTelemetryData() throws HaywardException {
        String xmlRequest = CommandBuilder.buildGetTelemetry();
        String xmlResponse = sendRequest(xmlRequest, MessageType.GET_TELEMETRY);

        if (xmlResponse.isEmpty()) {
            logger.debug("Hayward Connection thing: RequestTelemetryData XML response was null");
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

        return true;

        /*
         * String xmlRequest =
         * "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Name>GetAllAlarmList</Name><Parameters/></Request>";
         * String xmlResponse = sendRequest(xmlRequest, MessageType.GET_ALARM_LIST);
         *
         * if (xmlResponse.isEmpty()) {
         * logger.debug("Hayward Connection thing: GetAllAlarmList XML response was null");
         * return false;
         * }
         *
         * if (!evaluateXPath("/Response/Parameters//Parameter[@name='StatusMessage']/text()", xmlResponse).isEmpty()) {
         * logger.debug("Hayward Connection thing: GetAllAlarmList XML response: {}", xmlResponse);
         * return false;
         * }
         *
         * // TODO
         * for (Thing thing : getThing().getThings()) {
         * Map<String, String> properties = thing.getProperties();
         * if ("BACKYARD".equals(properties.get(BindingConstants.PROPERTY_TYPE))) {
         * BackyardHandler handler = (BackyardHandler) thing.getHandler();
         * if (handler != null) {
         * String systemID = properties.get(BindingConstants.PROPERTY_SYSTEM_ID);
         * if (systemID != null) {
         * return handler.getAlarmList(systemID);
         * }
         * }
         * }
         * }
         * return false;
         */
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
                if (!(requestTelemetryData())) {
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
    Thing getThingForType(TypeToRequest type, int num) {
        for (Thing thing : getThing().getThings()) {
            Map<String, String> properties = thing.getProperties();
            if (Integer.toString(num).equals(properties.get(BindingConstants.PROPERTY_SYSTEM_ID))) {
                if (type.toString().equals(properties.get(BindingConstants.PROPERTY_TYPE))) {
                    return thing;
                }
            }
        }
        return null;
    }

    public void getProperties() {
        if (getMspConfig() != null) {
            List<org.openhab.binding.haywardomnilogiclocal.internal.config.SystemConfig> systems = getMspConfig()
                    .getSystems();
            Map<String, String> bridgeProps = new HashMap<>();
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_VSPSPEEDFORMAT,
                    systems.get(0).getMspVspSpeedFormat());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_TIMEFORMAT,
                    systems.get(0).getMspTimeFormat());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_TIMEZONE, systems.get(0).getTimeZone());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_DST, systems.get(0).getDst());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_INTERNETTIME,
                    systems.get(0).getInternetTime());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UNITS, systems.get(0).getUnits());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_CHLORDISPLAY,
                    systems.get(0).getMspChlorDisplay());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_LANGUAGE, systems.get(0).getMspLanguage());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIDISPLAYMODE,
                    systems.get(0).getUiDisplayMode());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIMOODCOLORENABLED,
                    systems.get(0).getUiMoodColorEnabled());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIHEATERSIMPLEMODE,
                    systems.get(0).getUiHeaterSimpleMode());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIFILTERSIMPLEMODE,
                    systems.get(0).getUiFilterSimpleMode());
            putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UILIGHTSSIMPLEMODE,
                    systems.get(0).getUiLightsSimpleMode());

            BridgeBuilder bridgeBuilder = editThing();
            bridgeBuilder.withProperties(bridgeProps);
            updateThing(bridgeBuilder.build());
        }
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

    public synchronized String sendRequest(String xmlRequest, MessageType msgType) throws HaywardException {
        if (logger.isTraceEnabled()) {
            logger.trace("Hayward Connection thing:  {} Hayward UDP command:\r{}", getCallingMethod(), xmlRequest);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Hayward Connection thing:  {}", getCallingMethod());
        }

        if (udpClient == null) {
            throw new HaywardException("UDP client not initialised");
        }

        try {
            UdpMessage response = udpClient.send(msgType, xmlRequest);
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

    public void putStrStrIfNotNull(Map<String, String> properties, String key, @Nullable String value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    public int convertCommand(Command command) {
        if (command == OnOffType.ON) {
            return 1;
        } else {
            return 0;
        }
    }

    public @Nullable MspConfig getMspConfig() {
        return mspConfig;
    }

    public void setMspConfig(@Nullable MspConfig mspConfig) {
        this.mspConfig = mspConfig;
    }

    public Config getBridgeConfig() {
        return config;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
