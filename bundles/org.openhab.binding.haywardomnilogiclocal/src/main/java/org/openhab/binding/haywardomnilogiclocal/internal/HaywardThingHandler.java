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
package org.openhab.binding.haywardomnilogiclocal.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.BridgeHandler;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HaywardThingHandler} is a subclass of the BaseThingHandler and a Super
 * Class to each Hayward Thing Handler
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public abstract class HaywardThingHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(HaywardThingHandler.class);

    public HaywardThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        getProperties();
        updateStatus(ThingStatus.ONLINE);
    }

    public void getProperties() {
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public void getTelemetry(String xmlResponse) throws HaywardException {
    }

    public void setStateDescriptions() throws HaywardException {
    }

    public State toState(String type, String channelID, String value) throws NumberFormatException {
        // ---- Read bridge properties once (safe defaults) ----
        String unitsPref = "Standard"; // Standard => °F
        String vspFormat = "Percent"; // Percent => %

        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgeHandler) {
            Map<String, String> bridgeProps = bridgeHandler.getThing().getProperties();
            String u = bridgeProps.get(BindingConstants.PROPERTY_BRIDGE_UNITS);
            if (u != null) {
                unitsPref = u;
            }
            String vs = bridgeProps.get(BindingConstants.PROPERTY_BRIDGE_VSPSPEEDFORMAT);
            if (vs != null) {
                vspFormat = vs;
            }
        }

        // ---- Convert by item-type ----
        switch (type) {
            case "Number":
                return new DecimalType(value);

            case "Switch":
                return OnOffType.from(Integer.parseInt(value) > 0);

            case "Number:Power":
                if (BindingConstants.CHANNEL_FILTER_POWER.equals(channelID)) {
                    return new QuantityType<>(Integer.parseInt(value), Units.WATT);
                }
                return new DecimalType(value);

            case "Number:Temperature": {
                int v = Integer.parseInt(value);
                if ("Metric".equalsIgnoreCase(unitsPref)) {
                    return new QuantityType<>(v, SIUnits.CELSIUS);
                } else {
                    // Default Standard
                    return new QuantityType<>(v, ImperialUnits.FAHRENHEIT);
                }
            }
            case "Number:Dimensionless": {
                // --- Chlorinator salt levels: ppm vs g/L ---
                if (BindingConstants.CHANNEL_CHLORINATOR_AVGSALTLEVEL.equals(channelID)
                        || BindingConstants.CHANNEL_CHLORINATOR_INSTANTSALTLEVEL.equals(channelID)) {
                    // If the controller is displaying PPM, use ppm in OH.
                    // Otherwise treat as g/L (or anything else) and return dimensionless numeric.
                    if ("Metric".equalsIgnoreCase(unitsPref)) {
                        // g/L not available in openHAB units -> dimensionless number
                        return new DecimalType(value);
                    } else {
                        return new QuantityType<>(Integer.parseInt(value), Units.PARTS_PER_MILLION);
                    }
                }

                // --- Timed percent is always percent ---
                if (BindingConstants.CHANNEL_CHLORINATOR_TIMEDPERCENT.equals(channelID)) {
                    return new QuantityType<>(Integer.parseInt(value), Units.PERCENT);
                }

                // --- Speed channels: Percent vs RPM depending on PROPERTY_BRIDGE_VSPSPEEDFORMAT ---
                if (BindingConstants.CHANNEL_FILTER_SPEED.equals(channelID)
                        || BindingConstants.CHANNEL_FILTER_LASTSPEED.equals(channelID)
                        || BindingConstants.CHANNEL_PUMP_SPEED.equals(channelID)) {
                    int v = Integer.parseInt(value);

                    if ("RPM".equalsIgnoreCase(vspFormat)) {
                        return new QuantityType<>(v, Units.RPM);
                    } else {
                        return new QuantityType<>(v, Units.PERCENT);
                    }
                }

                // Default for other Number:Dimensionless channels: keep numeric
                return new DecimalType(value);
            }
            default:
                return StringType.valueOf(value);
        }
    }

    public String cmdToString(Command command) {
        if (command == OnOffType.OFF) {
            return "0";
        } else if (command == OnOffType.ON) {
            return "1";
        } else if (command instanceof DecimalType decimalCommand) {
            return decimalCommand.toString();
        } else if (command instanceof QuantityType quantityCommand) {
            return quantityCommand.format("%1.0f");
        } else {
            return command.toString();
        }
    }

    public Map<String, State> updateData(String channelID, @Nullable String data) {
        Map<String, State> channelStates = new HashMap<>();
        if (data == null) {
            return channelStates;
        }
        Channel chan = getThing().getChannel(channelID);
        if (chan != null) {
            String acceptedItemType = chan.getAcceptedItemType();
            if (acceptedItemType != null) {
                State state = toState(acceptedItemType, channelID, data);
                updateState(chan.getUID(), state);
                channelStates.put(channelID, state);
            }
        }
        return channelStates;
    }

    public void putStrStrIfNotNull(Map<String, String> properties, String key, @Nullable String value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    public void putStrObjIfNotNull(Map<String, Object> properties, String key, @Nullable String value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    protected void sendUdpCommand(String xml, MessageType msgType) {
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            try {
                bridgehandler.sendRequest(xml, msgType);
            } catch (HaywardException e) {
                logger.debug("Error sending UDP command: {}", e.getMessage());
            }
        } else {
            logger.debug("Hayward bridge not available, command not sent");
        }
    }
}
