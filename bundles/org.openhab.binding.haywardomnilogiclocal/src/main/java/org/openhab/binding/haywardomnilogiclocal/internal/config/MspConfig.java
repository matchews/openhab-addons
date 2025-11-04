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
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the MSP configuration root element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("MSPConfig")
public class MspConfig {
    @XStreamImplicit(itemFieldName = "System")
    private final List<SystemConfig> systems = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Backyard")
    private final List<BackyardConfig> backyards = new ArrayList<>();

    @XStreamAlias("Schedules")
    private final SchedulesConfig schedules = new SchedulesConfig();

    @XStreamAlias("DMT")
    private final DmtConfig dmt = new DmtConfig();

    @XStreamAlias("CHECKSUM")
    private @Nullable String checksum;

    public List<SystemConfig> getSystems() {
        return systems;
    }

    public List<BackyardConfig> getBackyards() {
        return backyards;
    }

    public @Nullable Object getDevice(String sysId) {
        for (BackyardConfig backyard : backyards) {
            if (backyard.getSystemId().equals(sysId)) {
                return backyard;
            }

            List<BodyOfWaterConfig> bows = backyard.getBodiesOfWater();
            if (bows != null) {
                for (BodyOfWaterConfig bow : bows) {
                    if (bow.getSystemId().equals(sysId)) {
                        return bow;
                    }

                    List<ChlorinatorConfig> chlorinators = bow.getChlorinators();
                    if (chlorinators != null) {
                        for (ChlorinatorConfig chlorinator : chlorinators) {
                            if (chlorinator.getSystemId().equals(sysId)) {
                                return chlorinator;
                            }
                        }
                    }

                    List<ColorLogicLightConfig> colorLogicLights = bow.getColorLogicLights();
                    if (colorLogicLights != null) {
                        for (ColorLogicLightConfig colorLogicLight : colorLogicLights) {
                            if (colorLogicLight.getSystemId().equals(sysId)) {
                                return colorLogicLight;
                            }
                        }
                    }

                    List<FilterConfig> filters = bow.getFilters();
                    if (filters != null) {
                        for (FilterConfig filter : filters) {
                            if (filter.getSystemId().equals(sysId)) {
                                return filter;
                            }
                        }
                    }
                    List<RelayConfig> relays = bow.getRelays();
                    if (relays != null) {
                        for (RelayConfig relay : relays) {
                            if (relay.getSystemId().equals(sysId)) {
                                return relay;
                            }
                        }
                    }

                    List<SensorConfig> sensors = bow.getSensors();
                    if (sensors != null) {
                        for (SensorConfig sensor : sensors) {
                            if (sensor.getSystemId().equals(sysId)) {
                                return sensor;
                            }
                        }
                    }

                    List<VirtualHeaterConfig> virtualHeaters = bow.getVirtualHeaters();
                    if (virtualHeaters != null) {
                        for (VirtualHeaterConfig virtualHeater : virtualHeaters) {
                            if (virtualHeater.getSystemId().equals(sysId)) {
                                return virtualHeater;
                            }
                            List<HeaterEquipConfig> heaters = virtualHeater.getHeaters();
                            for (HeaterEquipConfig heater : heaters) {
                                if (heater.getSystemId().equals(sysId)) {
                                    return heater;
                                }
                            }

                        }
                    }
                }
            }

            List<PumpConfig> backyardPumps = backyard.getPumps();
            if (backyardPumps != null) {
                for (PumpConfig backyardPump : backyardPumps) {
                    if (backyardPump.getSystemId().equals(sysId)) {
                        return backyardPump;
                    }
                }
            }

            List<RelayConfig> backyardRelays = backyard.getRelays();
            if (backyardRelays != null) {
                for (RelayConfig backyardRelay : backyardRelays) {
                    if (backyardRelay.getSystemId().equals(sysId)) {
                        return backyardRelay;
                    }
                }
            }

            List<SensorConfig> backyardSensors = backyard.getSensors();
            if (backyardSensors != null) {
                for (SensorConfig backyardSensor : backyardSensors) {

                    if (backyardSensor.getSystemId().equals(sysId)) {
                        return backyardSensor;
                    }
                }
            }
        }
        return null;
    }

    // Todo Needed??
    public @Nullable BodyOfWaterConfig getBodyOfWater(String bowId) {
        for (BackyardConfig backyard : backyards) {
            List<BodyOfWaterConfig> bows = backyard.getBodiesOfWater();
            if (bows != null) {
                for (BodyOfWaterConfig bow : bows) {
                    if (bow.getSystemId().equals(bowId)) {
                        return bow;
                    }
                }
            }
        }
        return null;
    }

    public List<ScheduleConfig> getSchedules() {
        return schedules.getSchedules();
    }

    public DmtConfig getDmt() {
        return dmt;
    }

    public @Nullable String getChecksum() {
        return checksum;
    }
}
