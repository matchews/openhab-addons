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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.intellifire.internal.IntellifireBindingConstants;
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
public class IntellifireRemoteHandler extends IntellifireThingHandler {
    private final Logger logger = LoggerFactory.getLogger(IntellifireRemoteHandler.class);

    public IntellifireRemoteHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void poll(IntellifirePollData pollData) throws IntellifireException {
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_CONNECTIONQUALITY,
                Integer.toString(pollData.remote_connection_quality));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_DOWNTIME, Integer.toString(pollData.remote_downtime));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_ENABLE, Integer.toString(pollData.thermostat));

        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_SETPOINT, Integer.toString(pollData.setpoint / 100));

        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_TEMPERATURE, Integer.toString(pollData.temperature));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_TIMER, Integer.toString(pollData.timeremaining / 60));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_TIMERENABLE, Integer.toString(pollData.timer));
        updateData(IntellifireBindingConstants.CHANNEL_REMOTE_UPTIME, Integer.toString(pollData.remote_uptime));
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
                String serialNumber = bridgehandler.getSerialNumber(thing.getProperties());
                String cmdURL = "http://iftapi.net/a/" + serialNumber + "/apppost";
                String httpResponse;
                String content;

                // Pause polling while sending command
                bridgehandler.clearPolling();
                bridgehandler.initPolling(5);

                switch (channelUID.getId()) {
                    case IntellifireBindingConstants.CHANNEL_REMOTE_ENABLE:
                        content = "thermostat_setpoint=" + this.cmdToString(command);
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content);
                        break;

                    case IntellifireBindingConstants.CHANNEL_REMOTE_SETPOINT:
                        if (this.cmdToInt(command) >= 1 && this.cmdToInt(command) <= 5) {
                            content = "power=1";
                            httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content);

                        } else {
                            content = "power=0";
                            httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content);
                        }
                        content = "height=" + (this.cmdToInt(command) - 1);
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content);
                        break;

                    case IntellifireBindingConstants.CHANNEL_REMOTE_TIMER:
                        content = "pilot=" + this.cmdToInt(command) * 60;
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content);
                        break;

                    case IntellifireBindingConstants.CHANNEL_REMOTE_TIMERENABLE:
                        content = "prepurge=" + this.cmdToString(command);
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content);
                        break;

                    default:
                        logger.warn("intellifireCommand Unsupported type {}", channelUID);
                        return;
                }

                if (!"204".equals(httpResponse)) {
                    logger.warn("Unable to send command {} to Intellifire's server.", content);
                    this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    return;
                }

            } catch (InterruptedException e) {
                return;
            }
            this.updateStatus(ThingStatus.ONLINE);
        } else {
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
        }
    }
}
