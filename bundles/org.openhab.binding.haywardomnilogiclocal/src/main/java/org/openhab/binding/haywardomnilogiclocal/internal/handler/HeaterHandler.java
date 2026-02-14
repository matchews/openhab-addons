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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.config.HeaterEquipConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Heater;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Heater Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class HeaterHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(HeaterHandler.class);

    public HeaterHandler(Thing thing) {
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
                        if (object instanceof HeaterEquipConfig) {
                            HeaterEquipConfig heater = (HeaterEquipConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_TYPE, heater.getType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_HEATERTYPE,
                                    heater.getHeaterType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_ENABLED, heater.getEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_PRIORITY, heater.getPriority());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_RUNFORPRIORITY,
                                    heater.getRunForPriority());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_ALLOWLOWSPEEDOPERATION,
                                    heater.getAllowLowSpeedOperation());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_MINSPEEDFOROPERATION,
                                    heater.getMinSpeedForOperation());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_REQUIRESPRIMING,
                                    heater.getRequiresPriming());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_MINPRIMINGINTERVAL,
                                    heater.getMinPrimingInterval());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_TEMPDIFFINITIAL,
                                    heater.getTempDifferencetInitial());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_TEMPDIFFRUNNING,
                                    heater.getTempDifferenceRunning());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_SENSORSYSTEMID,
                                    heater.getSensorSystemId());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_HEATER_SHAREDEQUIPMENTSYSTEMID,
                                    heater.getSharedEquipmentSystemID());
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

        for (Heater h : status.getHeaters()) {
            if (sysId.equals(h.getSystemId())) {
                @Nullable
                String state = h.getState();
                if (state != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_STATE, state);
                } else {
                    logger.debug("Heater state missing from Telemtry");
                }

                @Nullable
                String heaterTemp = h.getTemp();
                if (heaterTemp != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_TEMP, heaterTemp);
                } else {
                    logger.debug("Heater temp missing from Telemtry");
                }

                @Nullable
                String heaterEnable = h.getEnable();
                if (heaterEnable != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_ENABLE, heaterEnable);
                } else {
                    logger.debug("Heater enable missing from Telemtry");
                }

                @Nullable
                String heaterPriority = h.getPriority();
                if (heaterPriority != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_PRIORITY, heaterPriority);
                } else {
                    logger.debug("Heater priority missing from Telemtry");
                }

                @Nullable
                String heaterMaintainFor = h.getMaintainFor();
                if (heaterMaintainFor != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_MAINTAINFOR, heaterMaintainFor);
                } else {
                    logger.debug("Heater maintain for missing from Telemtry");
                }
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof RefreshType)) {
            return;
        }
        String sysId = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
        String bowId = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);

        Bridge bridge = getBridge();
        if (sysId == null || bowId == null || bridge == null
                || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        String cmdURL;
        String cmdString = "0";
        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_HEATER_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetHeaterEnableCmd(bowId, sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_HEATER_ENABLED);
                break;
            default:
                logger.warn("haywardCommand Unsupported type {}", channelUID);
                return;
        }
    }
}
