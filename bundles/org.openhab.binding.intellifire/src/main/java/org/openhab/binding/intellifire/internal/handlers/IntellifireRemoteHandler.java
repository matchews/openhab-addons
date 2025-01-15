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
import org.openhab.binding.intellifire.internal.IntellifireCommand;
import org.openhab.binding.intellifire.internal.IntellifireException;
import org.openhab.binding.intellifire.internal.IntellifirePollData;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
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
public class IntellifireRemoteHandler extends IntellifireThingHandler {
    private final Logger logger = LoggerFactory.getLogger(IntellifireRemoteHandler.class);

    public IntellifireRemoteHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void poll(IntellifirePollData pollData) throws IntellifireException {
        getThing().setProperty(IntellifireBindingConstants.PROPERTY_IPADDRESS, pollData.ipv4Address);
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_CONNECTIONQUALITY,
                Integer.toString(pollData.remoteConnectionQuality));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_DOWNTIME, Integer.toString(pollData.remoteDowntime));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_ENABLE, Integer.toString(pollData.thermostat));

        // Setpoint is set to zero when disabled. Ignore and do not display zero.
        if (!(pollData.setpoint == 0)) {
            updateData(IntellifireBindingConstants.CHANNEL_REMOTE_SETPOINT, Integer.toString(pollData.setpoint / 100));
        }
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_TEMPERATURE, Integer.toString(pollData.temperature));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_TIMER, Integer.toString(pollData.timeremaining / 60 + 1));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_TIMERENABLE, Integer.toString(pollData.timer));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_UPTIME, Integer.toString(pollData.remoteUptime));
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
                    case IntellifireBindingConstants.CHANNEL_REMOTE_ENABLE:
                        // Set to 72F when enabled (2 implied decimal points)
                        cloudCommand = "setpoint";
                        localCommand = "thermostat_setpoint";
                        if (command == OnOffType.ON) {
                            value = "2220";
                        } else {
                            value = "0";
                        }
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;

                    case IntellifireBindingConstants.CHANNEL_REMOTE_SETPOINT:
                        cloudCommand = "setpoint";
                        localCommand = "thermostat_setpoint";
                        // ToDo losing decimal places here.
                        float celciusCommand = this.cmdToFloat(command, SIUnits.CELSIUS);
                        if (celciusCommand < 7) {
                            value = Integer.toString(7 * 100);
                        } else if (celciusCommand > 37) {
                            value = Integer.toString(37 * 100);
                        } else {
                            value = Integer.toString(Math.round(celciusCommand * 100));
                        }
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;

                    case IntellifireBindingConstants.CHANNEL_REMOTE_TIMER:
                        cloudCommand = "timeremaining";
                        localCommand = "time_remaining";
                        value = Integer.toString(this.cmdToInt(command, Units.MINUTE) * 60);
                        intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                localCommand, value);
                        bridgehandler.queueCommand(intellifireCommand);
                        break;

                    case IntellifireBindingConstants.CHANNEL_REMOTE_TIMERENABLE:
                        cloudCommand = "timeremaining";
                        localCommand = "time_remaining";
                        if (command == OnOffType.OFF) {
                            value = "0";
                            intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, value);
                            bridgehandler.queueCommand(intellifireCommand);
                        } else {
                            value = Integer.toString(60 * 60);
                            intellifireCommand = new IntellifireCommand(serialNumber, ipAddress, apiKey, cloudCommand,
                                    localCommand, value);
                            bridgehandler.queueCommand(intellifireCommand);
                        }
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
