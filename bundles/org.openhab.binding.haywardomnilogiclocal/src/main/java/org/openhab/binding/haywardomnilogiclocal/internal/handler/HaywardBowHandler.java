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

/**
 * The Body of Water Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class HaywardBowHandler extends HaywardThingHandler {

    public HaywardBowHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();
        for (BodyOfWater bow : status.getBodiesOfWater()) {
            if (sysId.equals(bow.getSystemId())) {
                @Nullable String flow = bow.getFlow();
                if (flow != null) {
                    updateData(HaywardBindingConstants.CHANNEL_BOW_FLOW, flow);
                }
                @Nullable String waterTemp = bow.getWaterTemp();
                if (waterTemp != null) {
                    updateData(HaywardBindingConstants.CHANNEL_BOW_WATERTEMP, waterTemp);
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }
}
