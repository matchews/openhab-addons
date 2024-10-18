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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.intellifire.internal.IntellifireBindingConstants;
import org.openhab.binding.intellifire.internal.IntellifireError;
import org.openhab.binding.intellifire.internal.IntellifireException;
import org.openhab.binding.intellifire.internal.IntellifirePollData;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
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
    public void poll(IntellifirePollData pollData) throws IntellifireException {
        getThing().setProperty(IntellifireBindingConstants.PROPERTY_IPADDRESS, pollData.ipv4Address);
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_POWER, Integer.toString(pollData.power));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_BATTERY, Integer.toString(pollData.battery));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_ECMLATENCY, Integer.toString(pollData.ecmLatency));

        String errors = "";
        for (int i = 0; i < pollData.errors.size(); i++) {
            errors = errors + IntellifireError.fromErrorCode(pollData.errors.get(i)) + " ";
        }

        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_ERRORS, errors);

        if (pollData.power == 0) {
            updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT, "0");
        } else {
            updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT,
                    Integer.toString(pollData.height + 1));
        }
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_HOT, Integer.toString(pollData.hot));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_COLDCLIMATEPILOT, Integer.toString(pollData.pilot));
        updateData(IntellifireBindingConstants.CHANNEL_FIREPLACE_PREPURGE, Integer.toString(pollData.prepurge));
        this.updateStatus(ThingStatus.ONLINE);
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
                String httpResponse;
                String cloudCommand;
                String localCommand;
                String valueString;

                // Pause polling while sending command
                bridgehandler.clearPolling();
                bridgehandler.initPolling(5);

                switch (channelUID.getId()) {
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_POWER:
                        cloudCommand = "power";
                        localCommand = "power";
                        valueString = this.cmdToString(command);
                        httpResponse = bridgehandler.sendCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, valueString);
                        break;
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT:
                        if (this.cmdToInt(command, null) >= 1 && this.cmdToInt(command, null) <= 5) {
                            // Turn on power
                            cloudCommand = "power";
                            localCommand = "power";
                            valueString = "1";
                            httpResponse = bridgehandler.sendCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, valueString);
                            // Set flame height
                            cloudCommand = "height";
                            localCommand = "flame_height";
                            valueString = Integer.toString(this.cmdToInt(command, null) - 1);
                            httpResponse = bridgehandler.sendCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, valueString);

                        } else {
                            // Turn off power
                            cloudCommand = "power";
                            localCommand = "power";
                            valueString = "0";
                            httpResponse = bridgehandler.sendCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, valueString);
                        }

                        break;

                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_COLDCLIMATEPILOT:
                        cloudCommand = "pilot";
                        localCommand = "pilot";
                        valueString = this.cmdToString(command);
                        httpResponse = bridgehandler.sendCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, valueString);
                        break;
                    default:
                        logger.warn("intellifireCommand Unsupported type {}", channelUID);
                        return;
                }

                if (!"204".equals(httpResponse)) {
                    this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    return;
                }

            } catch (InterruptedException e) {
                logger.error("Intellifire handleCommand exception: {}", e.getMessage());
                return;
            } catch (NoSuchAlgorithmException e) {
                logger.error("Intellifire handleCommand exception: {}", e.getMessage());
                return;
            }
            this.updateStatus(ThingStatus.ONLINE);
        } else {
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
        }
    }
}
