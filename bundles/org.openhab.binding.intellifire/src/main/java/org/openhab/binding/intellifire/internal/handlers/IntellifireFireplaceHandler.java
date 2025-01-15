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

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.intellifire.internal.IntellifireBindingConstants;
import org.openhab.binding.intellifire.internal.IntellifireCommand;
import org.openhab.binding.intellifire.internal.IntellifireError;
import org.openhab.binding.intellifire.internal.IntellifireException;
import org.openhab.binding.intellifire.internal.IntellifirePollData;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Light Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class IntellifireFireplaceHandler extends IntellifireThingHandler {
    private final Logger logger = LoggerFactory.getLogger(IntellifireFireplaceHandler.class);

    public IntellifireFireplaceHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        Map<String, String> properties = thing.getProperties();
        String fanFeature = properties.get(IntellifireBindingConstants.PROPERTY_FIREPLACE_FEATURE_FAN);
        String lightFeature = properties.get(IntellifireBindingConstants.PROPERTY_FIREPLACE_FEATURE_LIGHT);

        if ("1".equals(fanFeature)) {
            addChannel(IntellifireBindingConstants.CHANNEL_FIREPLACE_FAN, IntellifireBindingConstants.CHANNEL_TYPE_FAN,
                    "Dimmer");
        }
        if ("1".equals(lightFeature)) {
            addChannel(IntellifireBindingConstants.CHANNEL_FIREPLACE_LIGHT,
                    IntellifireBindingConstants.CHANNEL_TYPE_LIGHT, "Dimmer");
        }
        updateStatus(ThingStatus.ONLINE);
    }

    protected void addChannel(String channelUID, String channelType, String itemType) {
        if (getThing().getChannel(channelUID) == null) {
            final ChannelTypeUID channelTypeUID = new ChannelTypeUID(channelType);
            ThingBuilder thingBuilder = editThing();
            Channel channel = ChannelBuilder.create(new ChannelUID(getThing().getUID(), channelUID), itemType)
                    .withType(channelTypeUID).build();
            thingBuilder.withChannel(channel);
            updateThing(thingBuilder.build());
        }
    }

    @Override
    public void poll(IntellifirePollData pollData) throws IntellifireException {
        getThing().setProperty(IntellifireBindingConstants.PROPERTY_IPADDRESS, pollData.ipv4Address);
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_BATTERY, Integer.toString(pollData.battery));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_ECMLATENCY, Integer.toString(pollData.ecmLatency));

        // If fan exists
        if (pollData.featureFan == 1) {
            updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_FAN, Integer.toString(pollData.fanspeed * 25));
        }

        // If light exists
        if (pollData.featureLight == 1) {
            updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_LIGHT,
                    String.valueOf(Math.round(pollData.light * 33.3)));
        }

        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_POWER, Integer.toString(pollData.power));

        String errors = "";
        for (int i = 0; i < pollData.errors.size(); i++) {
            errors = errors + IntellifireError.fromErrorCode(pollData.errors.get(i)) + " ";
        }

        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_ERRORS, errors);

        if (pollData.power == 0) {
            updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT, "0");
        } else {
            updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT,
                    Integer.toString((pollData.height + 1) * 20));
        }
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_HOT, Integer.toString(pollData.hot));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_COLDCLIMATEPILOT, Integer.toString(pollData.pilot));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_PREPURGE, Integer.toString(pollData.prepurge));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof RefreshType)) {
            return;
        }
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof IntellifireBridgeHandler bridgehandler) {
            try {
                String apiKey = bridgehandler.getApiKeyProperty(thing.getProperties());
                String serialNumber = bridgehandler.getSerialNumberProperty(thing.getProperties());
                String ipAddress = bridgehandler.getIPAddressProperty(thing.getProperties());
                String cloudCommand;
                String localCommand;
                String value;
                IntellifireCommand intellifireCommand;

                switch (channelUID.getId()) {
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_COLDCLIMATEPILOT:
                        cloudCommand = "pilot";
                        localCommand = "pilot";
                        value = this.cmdToString(command);
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_FAN:
                        cloudCommand = "fanspeed";
                        localCommand = "fan_speed";
                        value = Long.toString(Math.round(this.cmdToInt(command, null) / 25.0));
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT:
                        if (this.cmdToInt(command, null) >= 10 && this.cmdToInt(command, null) <= 100) {
                            // Turn on power
                            cloudCommand = "power";
                            localCommand = "power";
                            value = "1";
                            intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, value);
                            bridgehandler.queueCommand(intellifireCommand);
                            // Set flame height
                            cloudCommand = "height";
                            localCommand = "flame_height";
                            value = Long.toString(Math.round(this.cmdToInt(command, null) / 20.0) - 1);
                            intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, value);
                            bridgehandler.queueCommand(intellifireCommand);
                        } else {
                            // Turn off power
                            cloudCommand = "power";
                            localCommand = "power";
                            value = "0";
                            intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, value);
                            bridgehandler.queueCommand(intellifireCommand);
                        }
                        break;
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_LIGHT:
                        cloudCommand = "light";
                        localCommand = "light";
                        value = Long.toString(Math.round(this.cmdToInt(command, null) / 33.0));
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_POWER:
                        cloudCommand = "power";
                        localCommand = "power";
                        value = this.cmdToString(command);
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;
                    default:
                        logger.warn("intellifireCommand Unsupported type {}", channelUID);
                        return;
                }
            } catch (IntellifireException e) {
                logger.error("Intellifire handleCommand exception: {}", e.getMessage());
                return;
            } catch (InterruptedException e) {
                logger.error("Intellifire handleCommand exception: {}", e.getMessage());
                return;
            } catch (NoSuchAlgorithmException e) {
                logger.error("Intellifire handleCommand exception: {}", e.getMessage());
                return;
            }
        }
    }
}
