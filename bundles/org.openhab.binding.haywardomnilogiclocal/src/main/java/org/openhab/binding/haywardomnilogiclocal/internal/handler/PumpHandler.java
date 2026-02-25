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
import org.openhab.binding.haywardomnilogiclocal.internal.config.PumpConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Pump;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.StateDescriptionFragment;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.StateOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Pump Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class PumpHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(PumpHandler.class);

    public PumpHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        try {
            getProperties();
            setStateDescriptions();

            updateStatus(ThingStatus.ONLINE);
        } catch (HaywardException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Unable to set PumpHandler StateDescriptions");
        }
    }

    @Override
    public void setStateDescriptions() throws HaywardException {
        List<StateOption> options = new ArrayList<>();
        String option;

        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            // Set minimum and maximum percent speeds
            Channel ch = thing.getChannel(BindingConstants.CHANNEL_PUMP_SPEED_PERCENT);
            if (ch != null) {
                StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                        .withMinimum(
                                new BigDecimal(getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MINSPEED)))
                        .withMaximum(
                                new BigDecimal(getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MAXSPEED)))
                        .withPattern("%d %unit%").withStep(new BigDecimal(5)).withReadOnly(false).build();
                bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
            }
            // Set minimum and maximum RPM speeds
            ch = thing.getChannel(BindingConstants.CHANNEL_PUMP_SPEED_RPM);
            if (ch != null) {
                StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                        .withMinimum(
                                new BigDecimal(getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MINRPM)))
                        .withMaximum(
                                new BigDecimal(getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MAXRPM)))
                        .withPattern("%d %unit%").withStep(new BigDecimal(10)).withReadOnly(false).build();
                bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
            }

            // Set Speed States
            ch = thing.getChannel(BindingConstants.CHANNEL_PUMP_SPEEDPRESET);
            if (ch != null) {
                options.add(new StateOption("0", "Off"));
                option = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_LOWSPEED);
                if (option != null) {
                    options.add(new StateOption(option, "Low"));
                }
                option = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MEDSPEED);
                if (option != null) {
                    options.add(new StateOption(option, "Medium"));
                }
                option = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_HIGHSPEED);
                if (option != null) {
                    options.add(new StateOption(option, "High"));
                }
                option = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_CUSTOMSPEED);
                if (option != null) {
                    options.add(new StateOption(option, "Custom"));
                }

                StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                        .withOptions(options).build();
                bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
            }
        }
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
                        if (object instanceof PumpConfig) {
                            PumpConfig backyardPump = (PumpConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_TYPE, backyardPump.getType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_FUNCTION,
                                    backyardPump.getFunction());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_FREEZEPROTECTENABLE,
                                    backyardPump.getFreezeProtectEnable());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_FREEZEPROTECTSPEED,
                                    backyardPump.getFreezeProtectSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_VALVECYCLEENABLE,
                                    backyardPump.getValveCycleEnable());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_VALVECYCLETIME,
                                    backyardPump.getValveCycleTime());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_PRIMINGENABLED,
                                    backyardPump.getPrimingEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_PRIMINGDURATION,
                                    backyardPump.getPrimingDuration());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_MINSPEED,
                                    backyardPump.getMinPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_MAXSPEED,
                                    backyardPump.getMaxPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_MINRPM,
                                    backyardPump.getMinPumpRpm());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_MAXRPM,
                                    backyardPump.getMaxPumpRpm());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_LOWSPEED,
                                    backyardPump.getVspLowPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_MEDSPEED,
                                    backyardPump.getVspMediumPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_HIGHSPEED,
                                    backyardPump.getVspHighPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_PUMP_CUSTOMSPEED,
                                    backyardPump.getVspCustomPumpSpeed());
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

        for (Pump p : status.getPumps()) {
            if (sysId.equals(p.getSystemId())) {
                @Nullable
                String state = p.getState();
                if (state != null) {
                    if (Integer.parseInt(state) > 0) {
                        updateData(BindingConstants.CHANNEL_PUMP_ENABLE, "1");
                    } else {
                        updateData(BindingConstants.CHANNEL_PUMP_ENABLE, "0");
                    }
                    updateData(BindingConstants.CHANNEL_PUMP_STATE, state);
                } else {
                    logger.debug("Pump state missing from Telemtry");
                }

                @Nullable
                String speed = p.getSpeed();
                if (speed != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_SPEED_PERCENT, speed);
                    updateData(BindingConstants.CHANNEL_PUMP_SPEEDPRESET, speed);
                    String maxRpmSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MAXRPM);
                    if (maxRpmSpeed != null) {
                        Integer rpmSpeed = (Integer.parseInt(speed) * (Integer.parseInt(maxRpmSpeed)) / 100);
                        updateData(BindingConstants.CHANNEL_PUMP_SPEED_RPM, rpmSpeed.toString());
                    }
                } else {
                    logger.debug("Pump speed missing from Telemtry");
                }

                @Nullable
                String lastSpeed = p.getLastSpeed();
                if (lastSpeed != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_LASTSPEED, lastSpeed);
                } else {
                    logger.debug("Pump last speed missing from Telemtry");
                }

                @Nullable
                String whyPumpIsOn = p.getWhyOn();
                if (whyPumpIsOn != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_WHYPUMPISON, whyPumpIsOn);
                } else {
                    logger.debug("Pump why pump is on missing from Telemtry");
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
        String minSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MINSPEED);
        String maxSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MAXSPEED);
        String minRpmSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MINRPM);
        String maxRpmSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_PUMP_MAXRPM);

        Bridge bridge = getBridge();
        if (sysId == null || bowId == null || bridge == null
                || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }
        String cmdURL;
        String cmdString = "0";

        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_PUMP_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "100";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                break;

            case BindingConstants.CHANNEL_PUMP_SPEED_PERCENT:
                if (command instanceof QuantityType quantityCommand) {
                    if (minSpeed != null && maxSpeed != null) {
                        if (quantityCommand.intValue() < Integer.parseInt(minSpeed)) {
                            cmdString = minSpeed;
                        } else if (quantityCommand.intValue() > Integer.parseInt(maxSpeed)) {
                            cmdString = maxSpeed;
                        } else {
                            cmdString = this.cmdToString(command);
                            ;
                        }
                        cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, cmdString);
                        sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                    }
                }
                break;

            case BindingConstants.CHANNEL_PUMP_SPEED_RPM:
                if (minSpeed != null && maxSpeed != null && maxRpmSpeed != null) {
                    cmdString = Integer.toString((Integer.parseInt(cmdString) * 100 / Integer.parseInt(maxRpmSpeed)));
                    if (Integer.parseInt(cmdString) > 0 && Integer.parseInt(cmdString) < Integer.parseInt(minSpeed)) {
                        cmdString = minSpeed;
                    } else if (Integer.parseInt(cmdString) > Integer.parseInt(maxSpeed)) {
                        cmdString = maxSpeed;
                    }
                }
                break;

            case BindingConstants.CHANNEL_PUMP_SPEEDPRESET:
                cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, command.toString());
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                break;
            default:
                break;
        }
    }
}
