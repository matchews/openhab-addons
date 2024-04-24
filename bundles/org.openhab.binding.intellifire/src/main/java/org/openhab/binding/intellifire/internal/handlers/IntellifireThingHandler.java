/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.intellifire.internal.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.intellifire.internal.IntellifireException;
import org.openhab.binding.intellifire.internal.IntellifirePollData;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link IntellifireThingHandler} is a subclass of the BaseThingHandler and a Super
 * Class to each Hayward Thing Handler
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public abstract class IntellifireThingHandler extends BaseThingHandler {

    public IntellifireThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public abstract void poll(IntellifirePollData pollData) throws IntellifireException;

    public State toState(String type, String channelID, String value) throws NumberFormatException {
        switch (type) {
            case "Dimmer":
                return new PercentType(value);
            case "Number":
                return new DecimalType(value);
            case "Number:Time":
                return new QuantityType<>(Integer.parseInt(value), Units.MINUTE);
            case "Number:Temperature":
                return new QuantityType<>(Integer.parseInt(value), SIUnits.CELSIUS);
            case "Switch":
                return OnOffType.from(Integer.parseInt(value) > 0);
            default:
                return StringType.valueOf(value);
        }
    }

    public float cmdToFloat(Command command, @Nullable Unit<?> unit) {
        if (command instanceof QuantityType<?> quantityCommand) {
            if (unit.equals(SIUnits.CELSIUS)) {
                return quantityCommand.toUnit(SIUnits.CELSIUS).floatValue();
            } else {
                return 0;
            }

        } else {
            return 0;
        }
    }

    public int cmdToInt(Command command, @Nullable Unit<?> unit) {
        if (command == OnOffType.OFF) {
            return 0;
        } else if (command == OnOffType.ON) {
            return 1;
        } else if (command instanceof DecimalType decimalCommand) {
            return decimalCommand.intValue();
        } else if (command instanceof QuantityType<?> quantityCommand) {
            if (unit.equals(SIUnits.CELSIUS)) {
                return quantityCommand.toUnit(SIUnits.CELSIUS).intValue();
            } else if (unit.equals(Units.MINUTE)) {
                return quantityCommand.toUnit(Units.MINUTE).intValue();
            } else {
                return 0;
            }

        } else {
            return 0;
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

    public Map<String, State> updateData(String channelID, String data) {
        Map<String, State> channelStates = new HashMap<>();
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
}
