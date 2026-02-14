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
import org.openhab.binding.haywardomnilogiclocal.internal.config.ChlorinatorConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Chlorinator;
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
 * The Chlorinator Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class ChlorinatorHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(ChlorinatorHandler.class);

    // Cache a few channel states (pattern copied from ColorLogicHandler)
    private final Object aggLock = new Object();
    private final ChlorAggregate agg = new ChlorAggregate();

    private static String orDefault(@Nullable String v, String def) {
        return v != null ? v : def;
    }

    // Defaults used when we don't yet have telemetry
    private static final String DEFAULT_CHLOR_ENABLE = "1";
    private static final String DEFAULT_SC_ENABLE = "0";
    private static final String DEFAULT_OPERATINGMODE = "1"; // 1 = Timed (matches your existing comment)

    private static final class ChlorAggregate {
        @org.eclipse.jdt.annotation.Nullable
        String chlorEnable;
        @org.eclipse.jdt.annotation.Nullable
        String scEnable;
        @org.eclipse.jdt.annotation.Nullable
        String operatingMode;

        ChlorAggregate copy() {
            ChlorAggregate c = new ChlorAggregate();
            c.chlorEnable = this.chlorEnable;
            c.scEnable = this.scEnable;
            c.operatingMode = this.operatingMode;
            return c;
        }
    }

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
                if (bridgeHandler.getMspConfig().getDevice(sysId) != null) {
                    Object object = bridgeHandler.getMspConfig().getDevice(sysId);
                    if (object instanceof ChlorinatorConfig) {
                        ChlorinatorConfig chlorinator = (ChlorinatorConfig) object;
                        Map<String, String> props = new HashMap<>();
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_SHAREDTYPE,
                                chlorinator.getSharedType());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_ENABLED,
                                chlorinator.getEnabled());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_CHLORINATOR_MODE, chlorinator.getMode());
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

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();

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
                updateData(BindingConstants.CHANNEL_CHLORINATOR_SC_MODE, scMode);
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
                // cache latest operating mode
                synchronized (aggLock) {
                    agg.operatingMode = operatingMode;
                }
                updateData(BindingConstants.CHANNEL_CHLORINATOR_OPERATINGMODE, operatingMode);
            } else {
                logger.debug("Chlorinator operating mode missing from Telemtry");
            }

            @Nullable
            String enable = c.getEnable();
            if (enable != null) {
                synchronized (aggLock) {
                    agg.chlorEnable = enable;
                }
                updateData(BindingConstants.CHANNEL_CHLORINATOR_ENABLE, enable);
            } else {
                logger.debug("Chlorinator enable missing from Telemtry");
            }

            if ("0".equals(scMode)) {
                synchronized (aggLock) {
                    agg.scEnable = "0";
                }
                updateData(BindingConstants.CHANNEL_CHLORINATOR_SC_ENABLE, "0");
                updateData(BindingConstants.CHANNEL_CHLORINATOR_SC_REMAINING, "0");
            } else {
                // Check to see if superchlorinate just started. If so, capture the duration
                // String scEnable = thing.getChannel(BindingConstants.CHANNEL_CHLORINATOR_SC_ENABLE).get
                // if ("0".equals(scEnable)) {
                // updateData(BindingConstants.CHANNEL_CHLORINATOR_SC_DURATION, "0");
                // }

                synchronized (aggLock) {
                    agg.scEnable = "1";
                }
                updateData(BindingConstants.CHANNEL_CHLORINATOR_SC_ENABLE, "1");
                // SC Time Remaining only exists in the requestConfiguration xml
                Bridge bridge = getBridge();
                if (bridge != null) {
                    BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
                    if (bridgeHandler != null && bridgeHandler.getMspConfig() != null) {
                        bridgeHandler.requestConfiguration();
                        Object object = bridgeHandler.getMspConfig().getDevice(sysId);
                        if (object instanceof ChlorinatorConfig) {
                            ChlorinatorConfig chlorinator = (ChlorinatorConfig) object;
                            String scRemaining = chlorinator.getSuperChlorTimeout();
                            if (scRemaining != null) {
                                updateData(BindingConstants.CHANNEL_CHLORINATOR_SC_REMAINING, scRemaining);
                            } else {
                                logger.debug("Chlorinator super chlorinate timeout missing from Configuration");
                            }
                        }
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

        // Snapshot cached states (pattern copied from ColorLogicHandler)
        final ChlorAggregate snap;
        synchronized (aggLock) {
            snap = agg.copy();
        }

        String sysID = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
        String bowID = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);
        String cellType = getThing().getProperties().get(BindingConstants.PROPERTY_CHLORINATOR_CELLTYPE);

        Bridge bridge = getBridge();
        if (sysID == null || bowID == null || cellType == null || bridge == null
                || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }
        String cmdURL;
        String cmdString = "0";

        switch (channelUID.getId()) {
            // TODO working!
            case BindingConstants.CHANNEL_CHLORINATOR_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetChlorEnableCmd(bowID, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_CHLOR_ENABLED);
                break;

            // TODO somewhat working - need to run pump to change timed percent OR have t-cell plugged in. it won't
            // change on the app either
            case BindingConstants.CHANNEL_CHLORINATOR_TIMEDPERCENT:
                String cholorEnabled = orDefault(snap.chlorEnable, DEFAULT_CHLOR_ENABLE);
                String scEnabled = orDefault(snap.scEnable, DEFAULT_SC_ENABLE);
                // Ensure SC is not running
                if ("0".equals(scEnabled)) {
                    BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
                    String valvePos = bridgeHandler.getFilterValvePositionForBow(bowID);
                    String bowType;
                    // If filterValve is set to SPA (2)
                    if ("2".equals(valvePos)) {
                        // Spa
                        bowType = "1";
                    } else {
                        // Pool
                        bowType = "0";
                    }

                    String cfgState;
                    if ("1".equals(cholorEnabled)) {
                        // Enable Chlorinator
                        cfgState = "3";
                    } else {
                        // Disable Chlorinator
                        cfgState = "2";
                    }
                    String opMode = orDefault(snap.operatingMode, DEFAULT_OPERATINGMODE);
                    String scTimeout = "0";
                    String orpTimeout = "0";
                    cmdURL = CommandBuilder.buildSetChlorParamsCmd(bowID, sysID, cfgState, opMode, bowType, cellType,
                            this.cmdToString(command), scTimeout, orpTimeout);
                    sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                }
                break;

            // TODO not working likely because the tcell is not plugged in
            // I think this was working before. need to verify.
            case BindingConstants.CHANNEL_CHLORINATOR_SC_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetUISuperChlorCmd(bowID, sysID, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_UI_SUPERCHLORINATE);

                // optimistic cache update (so subsequent commands can read it immediately)
                synchronized (aggLock) {
                    agg.scEnable = cmdString;
                }

                cmdURL = CommandBuilder.buildSetUISuperChlorTimeoutCmd(bowID, sysID, "5");
                sendUdpCommand(cmdURL, MessageType.SET_UI_SUPERCHLORINATE);
                break;

            default:
                break;
        }
    }
}
