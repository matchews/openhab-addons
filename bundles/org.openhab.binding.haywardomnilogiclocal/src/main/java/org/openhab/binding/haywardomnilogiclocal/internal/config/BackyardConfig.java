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
package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the Backyard element within the MSP configuration.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Backyard")
public class BackyardConfig {
    @XStreamAsAttribute
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Service-Mode-Timeout")
    private @Nullable String serviceModeTimeout;

    @XStreamImplicit(itemFieldName = "Sensor")
    private final List<SensorConfig> sensors = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Body-of-water")
    private final List<BodyOfWaterConfig> bodiesOfWater = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Pump")
    private final List<PumpConfig> pumps = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Relay")
    private final List<RelayConfig> relays = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getServiceModeTimeout() {
        return serviceModeTimeout;
    }

    public List<SensorConfig> getSensors() {
        return sensors;
    }

    public List<BodyOfWaterConfig> getBodiesOfWater() {
        return bodiesOfWater;
    }

    public @Nullable BodyOfWaterConfig getBodyOfWater(String bowId) {
        for (BodyOfWaterConfig bow : bodiesOfWater) {
            if (bowId.equals(bow.getSystemId())) {
                return bow;
            }
        }
        return null;
    }

    public List<PumpConfig> getPumps() {
        return pumps;
    }

    public List<RelayConfig> getRelays() {
        return relays;
    }
}
