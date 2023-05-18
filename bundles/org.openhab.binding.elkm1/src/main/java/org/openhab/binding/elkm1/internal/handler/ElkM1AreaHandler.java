/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

package org.openhab.binding.elkm1.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.elkm1.internal.ElkM1BindingConstants;
import org.openhab.binding.elkm1.internal.elk.ElkAlarmAreaState;
import org.openhab.binding.elkm1.internal.elk.ElkAlarmArmUpState;
import org.openhab.binding.elkm1.internal.elk.ElkAlarmArmedState;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The elk area handler to handle the area updates. Areas are the pieces which
 * can be updated and control the current alarm status.
 *
 * @author David Bennett - Initial Contribution
 */
@NonNullByDefault
public class ElkM1AreaHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(ElkM1AreaHandler.class);

    public ElkM1AreaHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    /**
     * Called to change the channel values and cause the elk to update.
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (ElkM1BindingConstants.CHANNEL_AREA_ARMED.equals(channelUID.getId())) {
            if (command instanceof StringType) {
                StringType str = (StringType) command;
                ElkAlarmArmedState armed = ElkAlarmArmedState.valueOf(str.toString());
                @SuppressWarnings("null")
                ElkM1BridgeHandler bridgeHandler = (ElkM1BridgeHandler) getBridge().getHandler();

                String zoneNo = getThing().getProperties().get(ElkM1BindingConstants.PROPERTY_ZONE_NUM);
                if (zoneNo != null) {
                    int zone = Integer.valueOf(zoneNo);
                    if (bridgeHandler != null) {
                        bridgeHandler.updateArmedState(zone, armed);
                    }
                }
            }
        }

        else if (ElkM1BindingConstants.CHANNEL_AREA_COMMAND.equals(channelUID.getId())) {
            if (command instanceof StringType) {
                StringType str = (StringType) command;
                // Execute the command
                try {
                    Bridge bridge = getBridge();
                    if (bridge != null) {
                        ElkM1BridgeHandler bridgeHandler = (ElkM1BridgeHandler) bridge.getHandler();
                        if (bridgeHandler != null) {
                            bridgeHandler.sendELKCommand(str.toString());
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        if (command instanceof RefreshType) {
            getElkM1BridgeHandler().refreshArea();
        }
    }

    /**
     * Called by the bridge to update the details on this area.
     *
     * @param state the new state of the area
     * @param armed the armed state of the area
     * @param armUp the armup state of the area
     */
    public void updateArea(ElkAlarmAreaState state, ElkAlarmArmedState armed, ElkAlarmArmUpState armUp) {
        logger.debug("Updated Elk area config to: {}, {}, {}", state, armed, armUp);
        Channel chan = getThing().getChannel(ElkM1BindingConstants.CHANNEL_AREA_STATE);
        if (chan != null) {
            updateState(chan.getUID(), new StringType(state.toString()));
        }
        chan = getThing().getChannel(ElkM1BindingConstants.CHANNEL_AREA_ARMED);
        if (chan != null) {
            updateState(chan.getUID(), new StringType(armed.toString()));
        }
        chan = getThing().getChannel(ElkM1BindingConstants.CHANNEL_AREA_ARMUP);
        if (chan != null) {
            updateState(chan.getUID(), new StringType(armUp.toString()));
        }
    }

    public void updateUserCode(String userNumber) {
        logger.debug("Valid code entered for user number: {}", userNumber);
        Channel chan = getThing().getChannel(ElkM1BindingConstants.CHANNEL_AREA_USERCODE);
        if (chan != null) {
            updateState(chan.getUID(), new StringType(userNumber.toString()));
        }
    }

    private @Nullable ElkM1BridgeHandler getElkM1BridgeHandler() {
        return (ElkM1BridgeHandler) getBridge().getHandler();
    }
}
