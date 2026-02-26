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
import org.openhab.binding.haywardomnilogiclocal.internal.config.FilterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Filter;
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
 * The Filter Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class FilterHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(FilterHandler.class);

    public FilterHandler(Thing thing) {
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
                    "Unable to set FilterHandler StateDescriptions");
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
                        if (object instanceof FilterConfig) {
                            FilterConfig filter = (FilterConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_SHAREDTYPE,
                                    filter.getSharedType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_FILTERTYPE,
                                    filter.getFilterType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_MAXSPEED,
                                    filter.getMaxPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_MINSPEED,
                                    filter.getMinPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_MAXRPM, filter.getMaxPumpRpm());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_MINRPM, filter.getMinPumpRpm());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_MINPRIMINGINTERVAL,
                                    filter.getMinPrimingInterval());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_PRIMINGENABLED,
                                    filter.getPrimingEnabled());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_PRIMINGDURATION,
                                    filter.getPrimingDuration());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_COOLDOWNDURATION,
                                    filter.getCooldownDuration());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_SHUTDOWNREQUESTTIMEOUT,
                                    filter.getShutdownRequestTimeout());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_NOWATERFLOWTIMEOUTENABLE,
                                    filter.getNoWaterFlowTimeoutEnable());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_NOWATERFLOWTIMEOUT,
                                    filter.getNoWaterFlowTimeoutTimeout());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_VALVECHANGEOFFENABLE,
                                    filter.getValveChangeOffEnable());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_VALVECHANGEOFFDURATION,
                                    filter.getValveChangeOffDuration());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTENABLE,
                                    filter.getFreezeProtectEnable());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTTEMP,
                                    filter.getFreezeProtectTemp());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTSPEED,
                                    filter.getFreezeProtectSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_SHAREDFITLERTIMEOUT,
                                    filter.getSharedFilterTimeout());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_FILTERVALVEPOSITION,
                                    filter.getFilterValvePosition());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_LOWSPEED,
                                    filter.getVspLowPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_MEDSPEED,
                                    filter.getVspMediumPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_HIGHSPEED,
                                    filter.getVspHighPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_CUSTOMSPEED,
                                    filter.getVspCustomPumpSpeed());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTOVERRIDEINTERVAL,
                                    filter.getFreezeProtectOverrideInterval());
                            updateProperties(props);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setStateDescriptions() throws HaywardException {
        List<StateOption> options = new ArrayList<>();
        String option;

        Bridge bridge = getBridge();
        BridgeHandler bridgehandler = (BridgeHandler) bridge.getHandler();
        if (bridge != null & bridgehandler != null) {
            // Set minimum and maximum percent speeds
            Channel ch = thing.getChannel(BindingConstants.CHANNEL_FILTER_SPEED_PERCENT);
            if (ch != null) {
                StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                        .withMinimum(new BigDecimal(
                                getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MINSPEED)))
                        .withMaximum(new BigDecimal(
                                getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MAXSPEED)))
                        .withPattern("%d %unit%").withStep(new BigDecimal(5)).withReadOnly(false).build();
                bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
            }
            // Set minimum and maximum RPM speeds
            ch = thing.getChannel(BindingConstants.CHANNEL_FILTER_SPEED_RPM);
            if (ch != null) {
                StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                        .withMinimum(
                                new BigDecimal(getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MINRPM)))
                        .withMaximum(
                                new BigDecimal(getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MAXRPM)))
                        .withPattern("%d %unit%").withStep(new BigDecimal(10)).withReadOnly(false).build();
                bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
            }
        }

        // Set Speed States
        Channel ch = thing.getChannel(BindingConstants.CHANNEL_FILTER_SPEEDPRESET);
        if (ch != null) {
            options.add(new StateOption("0", "Off"));
            option = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_LOWSPEED);
            if (option != null) {
                options.add(new StateOption(option, "Low"));
            }
            option = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MEDSPEED);
            if (option != null) {
                options.add(new StateOption(option, "Medium"));
            }
            option = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_HIGHSPEED);
            if (option != null) {
                options.add(new StateOption(option, "High"));
            }
            option = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_CUSTOMSPEED);
            if (option != null) {
                options.add(new StateOption(option, "Custom"));
            }

            StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                    .withOptions(options).build();
            bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
        }
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();
        String bowID = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);

        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            for (Filter f : status.getFilters()) {
                if (sysId.equals(f.getSystemId())) {
                    @Nullable
                    String state = f.getState();
                    if (state != null) {
                        if (Integer.parseInt(state) > 0) {
                            updateData(BindingConstants.CHANNEL_FILTER_ENABLE, "1");
                        } else {
                            updateData(BindingConstants.CHANNEL_FILTER_ENABLE, "0");
                        }
                        updateData(BindingConstants.CHANNEL_FILTER_STATE, state);
                    } else {
                        logger.debug("Filter state missing from Telemtry");
                    }

                    @Nullable
                    String speed = f.getSpeed();
                    if (speed != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_SPEED_PERCENT, speed);
                        updateData(BindingConstants.CHANNEL_FILTER_SPEEDPRESET, speed);
                        String maxRpm = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MAXRPM);
                        if (maxRpm != null) {
                            Integer rpmSpeed = (Integer.parseInt(speed) * (Integer.parseInt(maxRpm)) / 100);
                            updateData(BindingConstants.CHANNEL_FILTER_SPEED_RPM, rpmSpeed.toString());
                        }
                    } else {
                        logger.debug("Filter speed missing from Telemtry");
                    }

                    @Nullable
                    String valvePosition = f.getValvePosition();
                    if (valvePosition != null && bowID != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_VALVEPOSITION, valvePosition);
                        onFilterValvePositionUpdated(bowID, valvePosition);
                    } else {
                        logger.debug("Filter valve position missing from Telemtry");
                    }

                    @Nullable
                    String whyFilterIsOn = f.getWhyFilterIsOn();
                    if (whyFilterIsOn != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_WHYFILTERISON, whyFilterIsOn);
                    } else {
                        logger.debug("Filter why filter is on missing from Telemtry");
                    }

                    @Nullable
                    String fpOverride = f.getFpOverride();
                    if (fpOverride != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_FPOVERRIDE, fpOverride);
                    } else {
                        logger.debug("Filter fpOverride missing from Telemtry");
                    }

                    @Nullable
                    String reportedSpeed = f.getReportedSpeed();
                    if (reportedSpeed != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_REPORTEDSPEED, reportedSpeed);
                    } else {
                        logger.debug("Filter reported speed missing from Telemtry");
                    }

                    @Nullable
                    String power = f.getPower();
                    if (power != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_POWER, power);
                    } else {
                        logger.debug("Filter power missing from Telemtry");
                    }

                    @Nullable
                    String lastSpeed = f.getLastSpeed();
                    if (lastSpeed != null) {
                        updateData(BindingConstants.CHANNEL_FILTER_LASTSPEED, lastSpeed);
                    } else {
                        logger.debug("Filter last speed missing from Telemtry");
                    }

                    // TODO Test/implement Filter Diagnostics
                    if (bowID != null) {
                        String cmdURL = CommandBuilder.buildGetUIFilterDiagnosticInfo(bowID, sysId);
                        // sendUdpCommand(cmdURL, MessageType.DEFAULT);
                    }
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
        String minSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MINSPEED);
        String maxSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MAXSPEED);
        String maxRpmSpeed = getThing().getProperties().get(BindingConstants.PROPERTY_FILTER_MAXRPM);

        Bridge bridge = getBridge();
        if (sysId == null || bowId == null || bridge == null) {
            return;
        }
        String cmdURL;
        String cmdString = "0";

        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_FILTER_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "100";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                break;

            case BindingConstants.CHANNEL_FILTER_SPEED_PERCENT:
                if (command instanceof QuantityType quantityCommand) {
                    if (minSpeed != null && maxSpeed != null) {
                        if (quantityCommand.intValue() < Integer.parseInt(minSpeed)) {
                            cmdString = minSpeed;
                        } else if (quantityCommand.intValue() > Integer.parseInt(maxSpeed)) {
                            cmdString = maxSpeed;
                        } else {
                            cmdString = this.cmdToString(command);
                        }
                        cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, cmdString);
                        sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                    }
                }
                break;

            case BindingConstants.CHANNEL_FILTER_SPEED_RPM:
                if (command instanceof QuantityType quantityCommand) {
                    if (minSpeed != null && maxSpeed != null && maxRpmSpeed != null) {

                        int cmdSpeed = (quantityCommand.intValue() * 100 / Integer.parseInt(maxRpmSpeed));
                        cmdString = Integer.toString(cmdSpeed);
                        if (cmdSpeed < Integer.parseInt(minSpeed)) {
                            cmdString = minSpeed;
                        } else if (cmdSpeed > Integer.parseInt(maxSpeed)) {
                            cmdString = maxSpeed;
                        }
                        cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, cmdString);
                        sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                    }
                }
                break;

            case BindingConstants.CHANNEL_FILTER_SPEEDPRESET:
                cmdURL = CommandBuilder.buildSetEquipmentCmd(bowId, sysId, command.toString());
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                break;
            default:
                break;
        }
    }

    private void onFilterValvePositionUpdated(String bowID, String valvePosition) {
        if (bowID == null) {
            return; // or log debug
        }
        Bridge bridge = getBridge();
        BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
        bridgeHandler.updateFilterValvePositionForBow(bowID, valvePosition);
    }
}
