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
package org.openhab.binding.haywardomnilogiclocal.internal.discovery;

import static org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants.THING_TYPES_UIDS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.TypeToRequest;
import org.openhab.binding.haywardomnilogiclocal.internal.config.BackyardConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.BodyOfWaterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.ChlorinatorConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.ColorLogicLightConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.ConfigParser;
import org.openhab.binding.haywardomnilogiclocal.internal.config.FilterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.HeaterEquipConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.MspConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.PumpConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.RelayConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.SensorConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.VirtualHeaterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.BridgeHandler;
import org.openhab.core.config.discovery.AbstractThingHandlerDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets up the discovery results and details
 *
 * @author Matt Myers - Initial contribution
 */
@Component(scope = ServiceScope.PROTOTYPE, service = HaywardDiscoveryService.class)
@NonNullByDefault
public class HaywardDiscoveryService extends AbstractThingHandlerDiscoveryService<BridgeHandler> {
    private final Logger logger = LoggerFactory.getLogger(HaywardDiscoveryService.class);

    public HaywardDiscoveryService() {
        super(BridgeHandler.class, THING_TYPES_UIDS, 0, false);
    }

    @Override
    protected void startScan() {
        try {
            String xmlResults = thingHandler.requestConfiguration();
            mspConfigDiscovery(xmlResults);
        } catch (HaywardException e) {
            logger.warn("Exception during discovery scan: {}", e.getMessage());
        }
    }

    public synchronized void mspConfigDiscovery(String xmlResponse) {
        MspConfig config = ConfigParser.parse(xmlResponse);

        List<BackyardConfig> backyards = config.getBackyards();
        if (backyards != null) {
            for (BackyardConfig backyard : backyards) {

                String backyardName = backyard.getName();
                if (backyardName == null) {
                    backyardName = "Backyard";
                }

                Map<String, Object> backyardProps = new HashMap<>();
                backyardProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.BACKYARD);
                putStrObjIfNotNull(backyardProps, BindingConstants.PROPERTY_SYSTEM_ID, backyard.getSystemId());

                onDeviceDiscovered(BindingConstants.THING_TYPE_BACKYARD, backyardName, backyardProps);

                List<BodyOfWaterConfig> bows = backyard.getBodiesOfWater();
                if (bows != null) {
                    for (BodyOfWaterConfig bow : bows) {

                        String bowName = bow.getName();
                        if (bowName == null) {
                            bowName = "Backyard";
                        }

                        Map<String, Object> bowProps = new HashMap<>();
                        bowProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.BOW);
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_SYSTEM_ID, bow.getSystemId());
                        // TOdo don't think I need this.
                        // addBowContext(bowProps, bow);
                        onDeviceDiscovered(BindingConstants.THING_TYPE_BOW, bowName, bowProps);

                        List<ChlorinatorConfig> chlorinators = bow.getChlorinators();
                        if (chlorinators != null) {
                            for (ChlorinatorConfig chlorinator : chlorinators) {

                                String chlorinatorName = chlorinator.getName();
                                if (chlorinatorName == null) {
                                    chlorinatorName = "Chlorinator";
                                }

                                Map<String, Object> chlorProps = new HashMap<>();
                                chlorProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.CHLORINATOR);
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        chlorinator.getSystemId());
                                addBowContext(chlorProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_CHLORINATOR, chlorinatorName,
                                        chlorProps);
                            }
                        }

                        List<ColorLogicLightConfig> lights = bow.getColorLogicLights();
                        if (lights != null) {
                            for (ColorLogicLightConfig light : lights) {

                                String lightName = light.getName();
                                if (lightName == null) {
                                    lightName = "Light";
                                }
                                Map<String, Object> lightProps = new HashMap<>();
                                lightProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.COLORLOGIC);
                                putStrObjIfNotNull(lightProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        light.getSystemId());
                                addBowContext(lightProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_COLORLOGIC, lightName, lightProps);
                            }
                        }

                        List<FilterConfig> filters = bow.getFilters();
                        if (filters != null) {
                            for (FilterConfig filter : filters) {

                                String filterName = filter.getName();
                                if (filterName == null) {
                                    filterName = "Filter";
                                }

                                Map<String, Object> filterProps = new HashMap<>();
                                filterProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.FILTER);
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        filter.getSystemId());

                                addBowContext(filterProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_FILTER, filterName, filterProps);
                            }
                        }

                        List<RelayConfig> relays = bow.getRelays();
                        if (relays != null) {
                            for (RelayConfig relay : relays) {
                                String relayName = relay.getName();
                                if (relayName == null) {
                                    relayName = "Relay";
                                }
                                Map<String, Object> relayProps = new HashMap<>();
                                relayProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.RELAY);
                                putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        relay.getSystemId());
                                addBowContext(relayProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_RELAY, relayName, relayProps);
                            }
                        }

                        List<SensorConfig> sensors = bow.getSensors();
                        if (sensors != null) {
                            for (SensorConfig sensor : sensors) {
                                String sensorName = sensor.getName();
                                if (sensorName == null) {
                                    sensorName = "Sensor";
                                }
                                Map<String, Object> sensorProps = new HashMap<>();
                                sensorProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.SENSOR);
                                putStrObjIfNotNull(sensorProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        sensor.getSystemId());
                                putStrObjIfNotNull(sensorProps, BindingConstants.PROPERTY_SENSOR_TYPE,
                                        sensor.getType());
                                putStrObjIfNotNull(sensorProps, BindingConstants.PROPERTY_SENSOR_UNITS,
                                        sensor.getUnits());

                                addBowContext(sensorProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_SENSOR, sensorName, sensorProps);
                            }
                        }

                        List<VirtualHeaterConfig> virtualHeaters = bow.getVirtualHeaters();
                        if (virtualHeaters != null) {
                            for (VirtualHeaterConfig virtualHeater : virtualHeaters) {
                                Map<String, Object> virtualHeaterProps = new HashMap<>();
                                virtualHeaterProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.HEATER);
                                putStrObjIfNotNull(virtualHeaterProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        virtualHeater.getSystemId());
                                addBowContext(virtualHeaterProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_VIRTUALHEATER, "Heater",
                                        virtualHeaterProps);

                                List<HeaterEquipConfig> heaters = virtualHeater.getHeaters();
                                for (HeaterEquipConfig heater : heaters) {
                                    String heaterName = heater.getName();
                                    if (heaterName == null) {
                                        heaterName = "Heater Equipment";
                                    }
                                    Map<String, Object> heaterProps = new HashMap<>();
                                    heaterProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.HEATER);
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                            heater.getSystemId());
                                    addBowContext(heaterProps, bow);
                                    onDeviceDiscovered(BindingConstants.THING_TYPE_HEATER, heaterName + " Equipment",
                                            heaterProps);
                                }
                            }
                        }
                    }
                    List<PumpConfig> backyardPumps = backyard.getPumps();
                    if (backyardPumps != null) {
                        for (PumpConfig backyardPump : backyardPumps) {
                            String pumpName = backyardPump.getName();
                            if (pumpName == null) {
                                pumpName = "Pump";
                            }
                            Map<String, Object> pumpProps = new HashMap<>();
                            pumpProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.PUMP);
                            putStrObjIfNotNull(pumpProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                    backyardPump.getSystemId());
                            onDeviceDiscovered(BindingConstants.THING_TYPE_PUMP, pumpName, pumpProps);
                        }
                    }
                    List<RelayConfig> backyardRelays = backyard.getRelays();
                    if (backyardRelays != null) {
                        for (RelayConfig relay : backyardRelays) {
                            String relayType = relay.getType();

                            String relayName = relay.getName();
                            if (relayName == null) {
                                relayName = "Relay";
                            }
                            Map<String, Object> relayProps = new HashMap<>();
                            relayProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.RELAY);
                            putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_SYSTEM_ID, relay.getSystemId());
                            onDeviceDiscovered(BindingConstants.THING_TYPE_RELAY, relayName, relayProps);
                        }
                    }
                    List<SensorConfig> backyardSensors = backyard.getSensors();
                    if (backyardSensors != null) {
                        for (SensorConfig sensor : backyardSensors) {

                            String sensorName = sensor.getName();
                            if (sensorName == null) {
                                sensorName = "Sensor";
                            }
                            Map<String, Object> sensorProps = new HashMap<>();
                            sensorProps.put(BindingConstants.PROPERTY_TYPE, TypeToRequest.SENSOR);
                            putStrObjIfNotNull(sensorProps, BindingConstants.PROPERTY_SYSTEM_ID, sensor.getSystemId());
                            putStrObjIfNotNull(sensorProps, BindingConstants.PROPERTY_SENSOR_TYPE, sensor.getType());
                            putStrObjIfNotNull(sensorProps, BindingConstants.PROPERTY_SENSOR_UNITS, sensor.getUnits());
                            onDeviceDiscovered(BindingConstants.THING_TYPE_SENSOR, sensorName, sensorProps);
                        }
                    }
                }
            }

        }

    }

    private void putStrObjIfNotNull(Map<String, Object> properties, String key, @Nullable String value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    private void addBowContext(Map<String, Object> properties, BodyOfWaterConfig bow) {
        String bowId = bow.getSystemId();
        putStrObjIfNotNull(properties, BindingConstants.PROPERTY_BOWID, bowId);
        String bowName = bow.getName();
        putStrObjIfNotNull(properties, BindingConstants.PROPERTY_BOWNAME, bowName);
    }

    public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
        BridgeHandler bridgehandler = thingHandler;
        String systemID = (String) properties.get(BindingConstants.PROPERTY_SYSTEM_ID);
        if (systemID != null) {
            ThingUID thingUID = new ThingUID(thingType, bridgehandler.getThing().getUID(), systemID);
            DiscoveryResult result = DiscoveryResultBuilder.create(thingUID)
                    .withBridge(bridgehandler.getThing().getUID())
                    .withRepresentationProperty(BindingConstants.PROPERTY_SYSTEM_ID).withLabel("Hayward " + label)
                    .withProperties(properties).build();
            thingDiscovered(result);
        }
    }
}
