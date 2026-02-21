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
package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.config.GroupConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Group;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Group Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class GroupHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(GroupHandler.class);

    public GroupHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void getProperties() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
            if (bridgeHandler != null && bridgeHandler.getMspConfig() != null) {
                String sysId = getThing().getUID().getId();
                if (bridgeHandler.getMspConfig().getDevice(sysId) != null) {
                    Object object = bridgeHandler.getMspConfig().getDevice(sysId);
                    if (object instanceof GroupConfig) {
                        GroupConfig group = (GroupConfig) object;
                        Map<String, String> props = new HashMap<>();
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_GROUP_NAME, group.getName());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_GROUP_ICON, group.getIconID());
                        updateProperties(props);
                    }
                }
            }
        }
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();

        if (status.getGroups() != null) {
            for (Group group : status.getGroups()) {
                if (sysId.equals(group.getSystemId())) {
                    @Nullable
                    String groupState = group.getGroupState();
                    if (groupState != null) {
                        updateData(BindingConstants.CHANNEL_GROUP_STATE, groupState);
                    } else {
                        logger.debug("Group state missing from Telemtry");
                    }
                }
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof RefreshType)) {
            return;
        }
        String sysId = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);

        Bridge bridge = getBridge();
        if (sysId == null || bridge == null) {
            return;
        }
        String cmdURL;
        String cmdString = "0";

        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_GROUP_STATE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildRunGroupCmd(sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.RUN_GROUP_CMD);
                break;

            default:
                break;
        }
    }
}
