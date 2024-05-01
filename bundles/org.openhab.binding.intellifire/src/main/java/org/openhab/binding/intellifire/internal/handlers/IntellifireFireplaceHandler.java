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
                String serialNumber = bridgehandler.getSerialNumberProperty(thing.getProperties());
                String cmdURL = "http://iftapi.net/a/" + serialNumber + "/apppost";
                String httpResponse;
                String content;

                // Pause polling while sending command
                bridgehandler.clearPolling();
                bridgehandler.initPolling(5);

                switch (channelUID.getId()) {
                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_POWER:
                        content = "power=" + this.cmdToString(command);
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content, 10);
                        break;

                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_FLAMEHEIGHT:
                        if (this.cmdToInt(command, null) >= 1 && this.cmdToInt(command, null) <= 5) {
                            content = "power=1";
                            httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content, 10);

                        } else {
                            content = "power=0";
                            httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content, 10);
                        }
                        content = "height=" + (this.cmdToInt(command, null) - 1);
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content, 10);
                        break;

                    case IntellifireBindingConstants.CHANNEL_FIREPLACE_COLDCLIMATEPILOT:
                        content = "pilot=" + this.cmdToString(command);
                        httpResponse = bridgehandler.httpResponseContent(cmdURL, HttpMethod.POST, content, 10);
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
