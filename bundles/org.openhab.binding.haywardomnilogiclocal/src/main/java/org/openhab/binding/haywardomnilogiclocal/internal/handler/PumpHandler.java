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
import org.openhab.binding.haywardomnilogiclocal.internal.config.PumpConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Pump;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
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

    public static final String PROPERTY_PUMP_TYPE = "pumpType";
    public static final String PROPERTY_PUMP_FUNCTION = "pumpFunction";
    public static final String PROPERTY_PUMP_PRIMINGENABLED = "pumpPrimingEnabled";
    public static final String PROPERTY_PUMP_MINSPEED = "minPumpPercent";
    public static final String PROPERTY_PUMP_MAXSPEED = "maxPumpPercent";
    public static final String PROPERTY_PUMP_MINRPM = "minPumpRPM";
    public static final String PROPERTY_PUMP_MAXRPM = "maxPumpRPM";
    public static final String PROPERTY_PUMP_LOWSPEED = "lowPumpSpeed";
    public static final String PROPERTY_PUMP_MEDSPEED = "mediumPumpSpeed";
    public static final String PROPERTY_PUMP_HIGHSPEED = "highPumpSpeed";
    public static final String PROPERTY_PUMP_CUSTOMSPEED = "customPumpSpeed";

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
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getUID().getId();
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        switch (channelUID.getId()) {
            case "pumpEnable":
                /// sendUdpCommand(CommandBuilder.setEquipmentEnable(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, "ON".equalsIgnoreCase(command.toString())),
                // MessageType.SET_EQUIPMENT_CMD);
                break;
            case "pumpSpeed":
                int speedVal = ((Number) command).intValue();
                // sendUdpCommand(
                // CommandBuilder.setPumpSpeed(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, speedVal),
                // MessageType.SET_EQUIPMENT_CMD);
                break;
            default:
                break;
        }
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (Pump p : status.getPumps()) {
            if (sysId.equals(p.getSystemId())) {

                @Nullable
                String pumpSpeed = p.getPumpSpeed();
                if (pumpSpeed != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_SPEED, pumpSpeed);

                    if (Integer.parseInt(pumpSpeed) > 0) {
                        updateData(BindingConstants.CHANNEL_PUMP_ENABLE, "1");
                    } else {
                        updateData(BindingConstants.CHANNEL_PUMP_ENABLE, "0");
                    }

                } else {
                    logger.debug("Pump speed missing from Telemtry");
                }

                @Nullable
                String pumpState = p.getPumpState();
                if (pumpState != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_STATE, pumpState);
                } else {
                    logger.debug("Pump state missing from Telemtry");
                }
            }
        }
    }
}
