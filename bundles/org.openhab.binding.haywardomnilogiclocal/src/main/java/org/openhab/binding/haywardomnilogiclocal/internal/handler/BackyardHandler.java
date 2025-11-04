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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.config.BackyardConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Backyard;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Backyard Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class BackyardHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public BackyardHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void getProperties() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
            if (bridgeHandler != null && bridgeHandler.getMspConfig() != null) {
                String sysId = getThing().getUID().getId();
                if (sysId != null) {
                    if (bridgeHandler.getMspConfig().getDevice(sysId) != null) {
                        Object object = bridgeHandler.getMspConfig().getDevice(sysId);
                        if (object instanceof BackyardConfig) {
                            BackyardConfig backyard = (BackyardConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_BACKYARDSERVICEMODETIMEOUT,
                                    backyard.getServiceModeTimeout());
                            updateProperties(props);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();
        if (sysId == null) {
            return;
        }

        for (Backyard by : status.getBackyards()) {
            if (sysId.equals(by.getSystemId())) {
                @Nullable
                String airTemp = by.getAirTemp();
                if (airTemp != null) {
                    updateData(BindingConstants.CHANNEL_BACKYARD_AIRTEMP, airTemp);
                } else {
                    logger.debug("Backyard air temperature missing");
                }

                @Nullable
                String state = by.getState();
                if (state != null) {
                    updateData(BindingConstants.CHANNEL_BACKYARD_STATE, state);
                } else {
                    logger.debug("Backyard state missing");
                }

                @Nullable
                String configChecksum = by.getConfigChksum();
                if (configChecksum != null) {
                    updateData(BindingConstants.CHANNEL_BACKYARD_CONFIGCHKSUM, configChecksum);
                } else {
                    logger.debug("Backyard config checksum missing");
                }

                @Nullable
                String mspVersion = by.getMspVersion();
                if (mspVersion != null) {
                    updateData(BindingConstants.CHANNEL_BACKYARD_MSPVERSION, mspVersion);
                } else {
                    logger.debug("Backyard MSP version missing");
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }

    public boolean getAlarmList(String systemID) {
        List<String> bowID = new ArrayList<>();
        List<String> parameter1 = new ArrayList<>();
        List<String> message = new ArrayList<>();
        String alarmStr;
        // todo
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            String urlParameters = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Name>GetAlarmList</Name><Parameters>"
                    // + "<Parameter name=\"Token\" dataType=\"String\">" + bridgehandler.getAccount().getToken()
                    + "</Parameter><Parameter name=\"MspSystemID\" dataType=\"int\">"
                    // + bridgehandler.getAccount().getMspSystemID()
                    + "</Parameter><Parameter name=\"CultureInfoName\" dataType=\"String\">en-us</Parameter></Parameters></Request>";

            try {
                String xmlResponse = bridgehandler.sendRequest(urlParameters, MessageType.MSP_ALARM_LIST);

                if (xmlResponse.isEmpty()) {
                    logger.debug("Hayward getAlarmList XML response was empty");
                    return false;
                }

                String status = bridgehandler
                        .evaluateXPath("/Response/Parameters//Parameter[@name='Status']/text()", xmlResponse).get(0);

                if (!("0".equals(status))) {
                    logger.trace("Hayward getAlarmList XML response: {}", xmlResponse);
                    return false;
                }

                bowID = bridgehandler.evaluateXPath("//Property[@name='BowID']/text()", xmlResponse);
                parameter1 = bridgehandler.evaluateXPath("//Property[@name='Parameter1']/text()", xmlResponse);
                message = bridgehandler.evaluateXPath("//Property[@name='Message']/text()", xmlResponse);

                for (int i = 0; i < 5; i++) {
                    if (i < bowID.size()) {
                        alarmStr = parameter1.get(i) + ": " + message.get(i);
                    } else {
                        alarmStr = "";
                    }
                    updateData("backyardAlarm" + String.format("%01d", i + 1), alarmStr);
                }
                this.updateStatus(ThingStatus.ONLINE);
                return true;
            } catch (HaywardException e) {
                logger.debug("Unable to get alarm list: {}", e.getMessage());
                return false;
            }
        } else {
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
            return false;
        }
    }
}
