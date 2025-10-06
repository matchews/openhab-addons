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
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
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
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public void getTelemetry(String xmlResponse) throws HaywardException {
    }

    public void setStateDescriptions() throws HaywardException {
    }

    public State toState(String type, String channelID, String value) throws NumberFormatException {

        switch (type) {
            case "Number":
                return new DecimalType(value);
            case "Switch":
                return OnOffType.from(Integer.parseInt(value) > 0);
            case "Number:Dimensionless":
                switch (channelID) {
                    case HaywardBindingConstants.CHANNEL_CHLORINATOR_AVGSALTLEVEL:
                        return new QuantityType<>(Integer.parseInt(value), Units.PARTS_PER_MILLION);
                    case HaywardBindingConstants.CHANNEL_CHLORINATOR_INSTANTSALTLEVEL:
                        return new QuantityType<>(Integer.parseInt(value), Units.PARTS_PER_MILLION);
                    case HaywardBindingConstants.CHANNEL_CHLORINATOR_TIMEDPERCENT:
                        return new QuantityType<>(Integer.parseInt(value), Units.PERCENT);
                    case HaywardBindingConstants.CHANNEL_FILTER_SPEED:
                        return new QuantityType<>(Integer.parseInt(value), Units.PERCENT);
                    case HaywardBindingConstants.CHANNEL_FILTER_LASTSPEED:
                        return new QuantityType<>(Integer.parseInt(value), Units.PERCENT);
                    case HaywardBindingConstants.CHANNEL_PUMP_SPEED:
                        return new QuantityType<>(Integer.parseInt(value), Units.PERCENT);
                }
                return StringType.valueOf(value);
            case "Number:Power":
                switch (channelID) {
                    case HaywardBindingConstants.CHANNEL_FILTER_POWER:
                        return new QuantityType<>(Integer.parseInt(value), Units.WATT);
                }
            case "Number:Temperature":
                Bridge bridge = getBridge();
                if (bridge != null) {
                    BridgeHandler bridgehandler = (BridgeHandler) bridge.getHandler();
                    if (bridgehandler != null) {
                        // Get units property from bridge
                        Map<String, String> bridgeProperties = bridgehandler.getThing().getProperties();
                        String units = bridgeProperties.get(HaywardBindingConstants.PROPERTY_BRIDGE_UNITS);

                        if ("Standard".equals(units)) {
                            return new QuantityType<>(Integer.parseInt(value), ImperialUnits.FAHRENHEIT);
                        } else {
                            return new QuantityType<>(Integer.parseInt(value), SIUnits.CELSIUS);
                        }
                    }
                }
                // default to imperial if no bridge
                return new QuantityType<>(Integer.parseInt(value), ImperialUnits.FAHRENHEIT);
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

    protected void updateIfPresent(Map<String, ParameterValue> values, String key, String channelID) {
        @Nullable
        ParameterValue parameter = values.get(key);
        @Nullable
        String value = parameter != null ? parameter.value() : null;
        if (value != null) {
            updateData(channelID, value);
        }
    }

    protected void putIfPresent(Map<String, ParameterValue> values, String key, Map<String, String> properties,
            String propertyName) {
        @Nullable
        ParameterValue parameter = values.get(key);
        @Nullable
        String value = parameter != null ? parameter.value() : null;
        if (value != null) {
            properties.put(propertyName, value);
        }
    }

    protected void sendUdpCommand(String xml, HaywardMessageType msgType) {
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            try {
                String response = bridgehandler.sendRequest(xml, msgType);
                if (logger.isTraceEnabled()) {
                    logger.trace("UDP response: {}", response);
                }
            } catch (HaywardException e) {
                logger.debug("Error sending UDP command: {}", e.getMessage());
            }
        } else {
            logger.debug("Hayward bridge not available, command not sent");
        }
    }
}
