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
public class IntellifireLightHandler extends IntellifireThingHandler {
    private final Logger logger = LoggerFactory.getLogger(IntellifireLightHandler.class);

    public IntellifireLightHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void poll(IntellifirePollData pollData) throws IntellifireException {
        updateData(IntellifireBindingConstants.CHANNEL_LIGHT, Integer.toString(pollData.light));
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
                    case IntellifireBindingConstants.CHANNEL_LIGHT:
                        content = "light=" + this.cmdToString(command);
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
