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
import org.openhab.binding.haywardomnilogiclocal.internal.config.BodyOfWaterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.BodyOfWater;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Body of Water Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class BowHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BowHandler.class);

    public BowHandler(Thing thing) {
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
                    if (object instanceof BodyOfWaterConfig) {
                        BodyOfWaterConfig bow = (BodyOfWaterConfig) object;
                        Map<String, String> props = new HashMap<>();
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_TYPE, bow.getType());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_SHAREDTYPE, bow.getSharedType());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_SHAREDPRIORITY,
                                bow.getSharedPriority());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_SHAREDEQUIPID,
                                bow.getSharedEquipmentSystemId());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_SUPPORTSSPILLOVER,
                                bow.getSupportsSpillover());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_USESPILLOVERFORFILTEROPERATIONS,
                                bow.getUseSpilloverForFilterOperations());
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_BOW_SIZEINGALLONS, bow.getSizeInGallons());
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
        for (BodyOfWater bow : status.getBodiesOfWater()) {
            if (sysId.equals(bow.getSystemId())) {
                @Nullable
                String flow = bow.getFlow();
                if (flow != null) {
                    updateData(BindingConstants.CHANNEL_BOW_FLOW, flow);
                } else {
                    logger.debug("Body of Water flow missing from Telemtry");
                }

                @Nullable
                String waterTemp = bow.getWaterTemp();
                if (waterTemp != null) {
                    updateData(BindingConstants.CHANNEL_BOW_WATERTEMP, waterTemp);
                } else {
                    logger.debug("Body of Water temp missing from Telemtry");
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }
}
