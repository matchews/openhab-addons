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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.config.VirtualHeaterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.VirtualHeater;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.StateDescriptionFragment;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Virtual Heater Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class VirtualHeaterHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(VirtualHeaterHandler.class);

    public VirtualHeaterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        try {
            getProperties();
            setStateDescriptions();
            updateStatus(ThingStatus.ONLINE);
        } catch (HaywardException e) {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void setStateDescriptions() throws HaywardException {
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            // Set Heater min and max water temps
            Channel ch = thing.getChannel(BindingConstants.CHANNEL_VIRTUALHEATER_CURRENTSETPOINT);
            if (ch != null) {
                String minTemp = getThing().getProperties()
                        .get(BindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP);
                String maxTemp = getThing().getProperties()
                        .get(BindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP);
                if (minTemp != null && maxTemp != null) {
                    StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                            .withMinimum(new BigDecimal(getThing().getProperties()
                                    .get(BindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP)))
                            .withMaximum(new BigDecimal(getThing().getProperties()
                                    .get(BindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP)))
                            .build();
                    bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
                    return;
                }
            }
        }
        logger.warn("Could not update {} state descriptions with min and max settable water temps", thing.getLabel());
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
                        if (object instanceof VirtualHeaterConfig) {
                            VirtualHeaterConfig virtualHeater = (VirtualHeaterConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_SHAREDTYPE,
                                    virtualHeater.getSharedType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_ENABLED,
                                    virtualHeater.getEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_CURRENTSETPOINT,
                                    virtualHeater.getCurrentSetPoint());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_MAXWATERTEMP,
                                    virtualHeater.getMaxWaterTemp());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP,
                                    virtualHeater.getMinSettableWaterTemp());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP,
                                    virtualHeater.getMaxSettableWaterTemp());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_COOLDOWNENABLED,
                                    virtualHeater.getCooldownEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_EXTENDENABLED,
                                    virtualHeater.getExtendEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_BOOSTTIMEINTERVAL,
                                    virtualHeater.getBoostTimeInterval());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_VIRTUALHEATER_HEATERBECOMEVALIDTIMEOUT,
                                    virtualHeater.getHeaterBecomeValidTimeout());
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
        for (VirtualHeater vh : status.getVirtualHeaters()) {
            if (sysId.equals(vh.getSystemId())) {
                @Nullable
                String curentSetpoint = vh.getCurrentSetPoint();
                if (curentSetpoint != null) {
                    updateData(BindingConstants.CHANNEL_VIRTUALHEATER_CURRENTSETPOINT, curentSetpoint);
                }
                @Nullable
                String enable = vh.getEnable();
                if (enable != null) {
                    updateData(BindingConstants.CHANNEL_VIRTUALHEATER_ENABLE, enable);
                }
                @Nullable
                String solarSetpoint = vh.getSolarSetPoint();
                if (solarSetpoint != null) {
                    updateData(BindingConstants.CHANNEL_VIRTUALHEATER_SOLARSETPOINT, solarSetpoint);
                }
                @Nullable
                String mode = vh.getMode();
                if (mode != null) {
                    updateData(BindingConstants.CHANNEL_VIRTUALHEATER_MODE, mode);
                }
                @Nullable
                String silentMode = vh.getSilentMode();
                if (silentMode != null) {
                    updateData(BindingConstants.CHANNEL_VIRTUALHEATER_SILENTMODE, silentMode);
                }
                @Nullable
                String whyOn = vh.getWhyOn();
                if (whyOn != null) {
                    updateData(BindingConstants.CHANNEL_VIRTUALHEATER_WHYON, whyOn);
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof RefreshType)) {
            return;
        }
        String sysId = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
        String bowId = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);
        String heaterMinSetTemp = getThing().getProperties()
                .get(BindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP);
        String heaterMaxSetTemp = getThing().getProperties()
                .get(BindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP);

        Bridge bridge = getBridge();
        if (sysId == null || bowId == null || bridge == null
                || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        String cmdURL;
        String cmdString = "0";
        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_VIRTUALHEATER_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetHeaterEnable(bowId, sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_HEATER_ENABLED);
                break;

            case BindingConstants.CHANNEL_VIRTUALHEATER_CURRENTSETPOINT:
                if (command instanceof QuantityType quantityCommand) {
                    if (heaterMinSetTemp != null && heaterMaxSetTemp != null) {
                        if (quantityCommand.intValue() < Integer.parseInt(heaterMinSetTemp)) {
                            cmdString = heaterMinSetTemp;
                        } else if (quantityCommand.intValue() > Integer.parseInt(heaterMaxSetTemp)) {
                            cmdString = heaterMaxSetTemp;
                        } else {
                            cmdString = this.cmdToString(command);
                            ;
                        }
                        cmdURL = CommandBuilder.buildSetHeaterEnable(bowId, sysId, cmdString);
                        sendUdpCommand(cmdURL, MessageType.SET_HEATER_ENABLED);
                    }
                }

                break;

            // TODO does the heater min/max apply to solar?
            case BindingConstants.CHANNEL_VIRTUALHEATER_SOLARSETPOINT:
                if (command instanceof QuantityType quantityCommand) {
                    if (heaterMinSetTemp != null && heaterMaxSetTemp != null) {
                        if (quantityCommand.intValue() < Integer.parseInt(heaterMinSetTemp)) {
                            cmdString = heaterMinSetTemp;
                        } else if (quantityCommand.intValue() > Integer.parseInt(heaterMaxSetTemp)) {
                            cmdString = heaterMaxSetTemp;
                        } else {
                            cmdString = this.cmdToString(command);
                            ;
                        }
                        cmdURL = CommandBuilder.buildSetUISolarSetPointCmd(bowId, sysId, cmdString);
                        sendUdpCommand(cmdURL, MessageType.SET_SOLAR_SET_POINT_COMMAND);
                    }
                }
                break;

            default:
                logger.warn("haywardCommand Unsupported type {}", channelUID);
                return;
        }
    }
}
