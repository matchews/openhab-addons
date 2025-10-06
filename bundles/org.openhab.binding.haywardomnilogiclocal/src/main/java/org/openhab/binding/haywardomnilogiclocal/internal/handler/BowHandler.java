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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.BodyOfWater;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
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
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public BowHandler(Thing thing) {
        super(thing);
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
                    updateData(HaywardBindingConstants.CHANNEL_BOW_FLOW, flow);
                } else {
                    logger.debug("Body of Water flow missing from Telemtry");
                }

                @Nullable
                String waterTemp = bow.getWaterTemp();
                if (waterTemp != null) {
                    updateData(HaywardBindingConstants.CHANNEL_BOW_WATERTEMP, waterTemp);
                } else {
                    logger.debug("Body of Water temp missing from Telemtry");
                }

            }
        }
        updateStatus(ThingStatus.ONLINE);
    }
}
