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
import org.openhab.binding.haywardomnilogiclocal.internal.config.ChlorinatorConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Chlorinator;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Chlorinator Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class ChlorinatorHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(ChlorinatorHandler.class);

    public ChlorinatorHandler(Thing thing) {
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
                        if (object instanceof ChlorinatorConfig) {
                            ChlorinatorConfig chlorinator = (ChlorinatorConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_SHAREDTYPE,
                                    chlorinator.getSharedType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_ENABLED,
                                    chlorinator.getEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_MODE,
                                    chlorinator.getMode());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_TIMEDPERCENT,
                                    chlorinator.getTimedPercent());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_SUPERCHLORTIMEOUT,
                                    chlorinator.getSuperChlorTimeout());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_CELLTYPE,
                                    chlorinator.getCellType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_DISPENSERTYPE,
                                    chlorinator.getDispenserType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_ORPTIMEOUT,
                                    chlorinator.getOrpTimeout());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_ORPSENSORID,
                                    chlorinator.getOrpSensorId());
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
        for (Chlorinator c : status.getChlorinators()) {
            @Nullable
            String statusVal = c.getStatus();
            if (statusVal != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_STATUS, statusVal);
            } else {
                logger.debug("Chlorinator status missing from Telemtry");
            }

            @Nullable
            String instantSaltLevel = c.getInstantSaltLevel();
            if (instantSaltLevel != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_INSTANTSALTLEVEL, instantSaltLevel);
            } else {
                logger.debug("Chlorinator instant salt level missing from Telemtry");
            }

            @Nullable
            String avgSaltLevel = c.getAvgSaltLevel();
            if (avgSaltLevel != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_AVGSALTLEVEL, avgSaltLevel);
            } else {
                logger.debug("Chlorinator avgerage salt level missing from Telemtry");
            }

            @Nullable
            String alert = c.getChlorAlert();
            if (alert != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_ALERT, alert);
            } else {
                logger.debug("Chlorinator alert missing from Telemtry");
            }

            @Nullable
            String error = c.getChlorError();
            if (error != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_ERROR, error);
            } else {
                logger.debug("Chlorinator error missing from Telemtry");
            }

            @Nullable
            String scMode = c.getScMode();
            if (scMode != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_SCMODE, scMode);
            } else {
                logger.debug("Chlorinator SC Mode missing from Telemtry");
            }

            @Nullable
            String operatingState = c.getOperatingState();
            if (operatingState != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_OPERATINGSTATE, operatingState);
            } else {
                logger.debug("Chlorinator operating state missing from Telemtry");
            }

            @Nullable
            String timedPercent = c.getTimedPercent();
            if (timedPercent != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_TIMEDPERCENT, timedPercent);
            } else {
                logger.debug("Chlorinator timed percent missing from Telemtry");
            }

            @Nullable
            String operatingMode = c.getOperatingMode();
            if (operatingMode != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_OPERATINGMODE, operatingMode);
            } else {
                logger.debug("Chlorinator operating mode missing from Telemtry");
            }

            @Nullable
            String enable = c.getEnable();
            if (enable != null) {
                updateData(BindingConstants.CHANNEL_CHLORINATOR_ENABLE, enable);
            } else {
                logger.debug("Chlorinator enable missing from Telemtry");
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        switch (channelUID.getId()) {
            case "chlorEnable":
                // sendUdpCommand(CommandBuilder.setEquipmentEnable(bridgehandler.getUnits(),
                // bridgehandler.getU(), sysId, "ON".equalsIgnoreCase(command.toString())),
                // MessageType.SET_CHLOR_ENABLED);
                break;
            case "chlorTimedPercent":
                // int val = ((Number) command).intValue();
                // sendUdpCommand(
                // CommandBuilder.setChlorinatorOutput(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, val),
                // MessageType.SET_CHLOR_PARAMS);
                break;
            default:
                break;
        }
    }
}
