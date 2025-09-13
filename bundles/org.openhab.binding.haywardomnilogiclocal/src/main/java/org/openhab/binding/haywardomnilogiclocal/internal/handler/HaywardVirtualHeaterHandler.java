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

import java.math.BigDecimal;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.VirtualHeater;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.StateDescriptionFragment;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Virtual Heater Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class HaywardVirtualHeaterHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(HaywardVirtualHeaterHandler.class);

    public HaywardVirtualHeaterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        try {
            setStateDescriptions();
            updateStatus(ThingStatus.ONLINE);
        } catch (HaywardException e) {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void setStateDescriptions() throws HaywardException {
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof HaywardBridgeHandler bridgehandler) {
            // Set heater min and max speeds
            Channel ch = thing.getChannel(HaywardBindingConstants.CHANNEL_VIRTUALHEATER_CURRENTSETPOINT);
            if (ch != null) {
                StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                        .withMinimum(new BigDecimal(getThing().getProperties()
                                .get(HaywardBindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP)))
                        .withMaximum(new BigDecimal(getThing().getProperties()
                                .get(HaywardBindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP)))
                        .build();
                bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
            }
        }
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();
        for (VirtualHeater vh : status.getVirtualHeaters()) {
            if (sysId.equals(vh.getSystemId())) {
                @Nullable String setPoint = vh.getCurrentSetPoint();
                if (setPoint != null) {
                    updateData(HaywardBindingConstants.CHANNEL_VIRTUALHEATER_CURRENTSETPOINT, setPoint);
                } else {
                    logger.debug("Virtual heater set point missing");
                }

                @Nullable String enableStr = vh.getEnable();
                if (enableStr != null) {
                    String enable = "yes".equals(enableStr) ? "1" : "0";
                    updateData(HaywardBindingConstants.CHANNEL_VIRTUALHEATER_ENABLE, enable);
                } else {
                    logger.debug("Virtual heater enable missing");
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof RefreshType)) {
            return;
        }

        String systemID = getThing().getProperties().get(HaywardBindingConstants.PROPERTY_SYSTEM_ID);
        String poolID = getThing().getProperties().get(HaywardBindingConstants.PROPERTY_BOWID);
        String heaterMinSetTemp = getThing().getProperties()
                .get(HaywardBindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP);
        String heaterMaxSetTemp = getThing().getProperties()
                .get(HaywardBindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP);

        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof HaywardBridgeHandler bridgehandler) {
            String cmdString = this.cmdToString(command);
            String cmdURL = null;
            String token = bridgehandler.getAccount().getToken();

            if (poolID == null || systemID == null || token == null) {
                logger.warn("haywardCommand missing configuration (poolID={}, systemID={}, token={})", poolID, systemID,
                        token);
                return;
            }

            int mspId = Integer.parseInt(bridgehandler.getAccount().getMspSystemID());

            if (command == OnOffType.ON) {
                cmdString = "True";
            } else if (command == OnOffType.OFF) {
                cmdString = "False";
            }

            try {
                switch (channelUID.getId()) {
                    case HaywardBindingConstants.CHANNEL_VIRTUALHEATER_ENABLE:
                        cmdURL = CommandBuilder.buildSetHeaterEnable(HaywardBindingConstants.COMMAND_PARAMETERS,
                                Objects.requireNonNull(token), mspId, Objects.requireNonNull(poolID),
                                Objects.requireNonNull(systemID), cmdString);
                        break;

                    case HaywardBindingConstants.CHANNEL_VIRTUALHEATER_CURRENTSETPOINT:
                        if (heaterMinSetTemp != null && heaterMaxSetTemp != null) {
                            if (Integer.parseInt(cmdString) < Integer.parseInt(heaterMinSetTemp)) {
                                cmdString = heaterMinSetTemp;
                            } else if (Integer.parseInt(cmdString) > Integer.parseInt(heaterMaxSetTemp)) {
                                cmdString = heaterMaxSetTemp;
                            }
                        }

                        cmdURL = CommandBuilder.buildSetUIHeaterCmd(HaywardBindingConstants.COMMAND_PARAMETERS,
                                Objects.requireNonNull(token), mspId, Objects.requireNonNull(poolID),
                                Objects.requireNonNull(systemID), cmdString);
                        break;
                    default:
                        logger.warn("haywardCommand Unsupported type {}", channelUID);
                        return;
                }

                // *****Send Command to Hayward server
                String xmlResponse = bridgehandler.sendRequest(cmdURL, HaywardMessageType.SET_HEATER_COMMAND);
                String status = bridgehandler.evaluateXPath("//Parameter[@name='Status']/text()", xmlResponse).get(0);

                if (!("0".equals(status))) {
                    logger.debug("haywardCommand XML response: {}", xmlResponse);
                    return;
                }
            } catch (HaywardException e) {
                logger.debug("Unable to send command to Hayward's server {}:{}",
                        bridgehandler.getBridgeConfig().getEndpointUrl(), e.getMessage());
            }
            this.updateStatus(ThingStatus.ONLINE);
        } else {
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
        }
    }
}
