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

import static org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants.THING_TYPES_UIDS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardTypeToRequest;
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
            String xmlResults = thingHandler.getMspConfig();
            mspConfigDiscovery(xmlResults);
        } catch (HaywardException e) {
            logger.warn("Exception during discovery scan: {}", e.getMessage());
        } catch (InterruptedException e) {
            return;
        }
    }

    public synchronized void mspConfigDiscovery(String xmlResponse) {
        MspConfig config = ConfigParser.parse(xmlResponse);
        String name = "";
        String systemID = "";

        // Get bridge properties
        List<org.openhab.binding.haywardomnilogiclocal.internal.config.SystemConfig> systems = config.getSystems();
        Map<String, String> bridgeProps = new HashMap<>();
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_VSPSPEEDFORMAT,
                systems.get(0).getMspVspSpeedFormat());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_TIMEFORMAT,
                systems.get(0).getTimeZone());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_TIMEZONE, systems.get(0).getTimeZone());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_DST, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_INTERNETTIME, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_UNITS, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_CHLORDISPLAY, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_LANGUAGE, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_UIDISPLAYMODE, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_UIMOODCOLORENABLED,
                systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_UIHEATERSIMPLEMODE,
                systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_UIFILTERSIMPLEMODE,
                systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, HaywardBindingConstants.PROPERTY_BRIDGE_UILIGHTSSIMPLEMODE,
                systems.get(0).getDst());

        BridgeHandler bridgehandler = thingHandler;
        if (bridgehandler != null) {
            bridgehandler.getThing().setProperties(bridgeProps);
        }

        List<BackyardConfig> backyards = config.getBackyards();
        if (backyards != null) {
            for (BackyardConfig backyard : backyards) {
                Map<String, Object> backyardProps = new HashMap<>();
                backyardProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BACKYARD);
                putStrObjIfNotNull(backyardProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, backyard.getSystemId());
                name = (backyard.getName() != null) ? backyard.getName() : "Backyard";
                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_BACKYARD, name, backyardProps);

                List<BodyOfWaterConfig> bows = backyard.getBodiesOfWater();
                if (bows != null) {
                    for (BodyOfWaterConfig bow : bows) {
                        Map<String, Object> bowProps = new HashMap<>();
                        bowProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BOW);
                        String bowId = bow.getSystemId();
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, bowId);
                        addBowContext(bowProps, bow);
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_TYPE, bow.getType());
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SHAREDTYPE,
                                bow.getSharedType());
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SHAREDPRIORITY,
                                bow.getSharedPriority());
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SHAREDEQUIPID,
                                bow.getSharedEquipmentSystemId());
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SUPPORTSSPILLOVER,
                                bow.getSupportsSpillover());
                        putStrObjIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SIZEINGALLONS,
                                bow.getSizeInGallons());

                        String bowLabel = bow.getName();
                        if (bowLabel == null) {
                            bowLabel = bowId != null ? bowId : "BodyOfWater";
                        }
                        onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_BOW, bowLabel, bowProps);

                        List<ChlorinatorConfig> chlorinators = bow.getChlorinators();
                        if (chlorinators != null) {
                            for (ChlorinatorConfig chlorinator : chlorinators) {
                                Map<String, Object> chlorProps = new HashMap<>();
                                chlorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.CHLORINATOR);
                                String chlorId = chlorinator.getSystemId();
                                String chlorName = chlorinator.getName();
                                putStrObjIfNotNull(chlorProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, chlorId);
                                addBowContext(chlorProps, bow);
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_CHLORINATOR,
                                        chlorName != null ? chlorName : "Chlorinator", chlorProps);
                            }
                        }

                        List<ColorLogicLightConfig> lights = bow.getColorLogicLights();
                        if (lights != null) {
                            for (ColorLogicLightConfig light : lights) {
                                Map<String, Object> lightProps = new HashMap<>();
                                lightProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.COLORLOGIC);
                                String lightId = light.getSystemId();
                                String lightName = light.getName();
                                putStrObjIfNotNull(lightProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, lightId);
                                addBowContext(lightProps, bow);
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_COLORLOGIC,
                                        lightName != null ? lightName : "ColorLogic", lightProps);
                            }
                        }

                        List<FilterConfig> filters = bow.getFilters();
                        if (filters != null) {
                            for (FilterConfig filter : filters) {
                                Map<String, Object> filterProps = new HashMap<>();
                                filterProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.FILTER);
                                String filterId = filter.getSystemId();
                                String filterName = filter.getName();

                                putStrObjIfNotNull(filterProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, filterId);
                                addBowContext(filterProps, bow);
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_FILTER,
                                        filterName != null ? filterName : "Filter", filterProps);
                            }
                        }

                        List<RelayConfig> relays = bow.getRelays();
                        if (relays != null) {
                            for (RelayConfig relay : relays) {
                                String relayId = relay.getSystemId();
                                String relayType = relay.getType();
                                if ("RLY_VALVE_ACTUATOR".equals(relayType)) {
                                    Map<String, Object> valveProps = new HashMap<>();
                                    valveProps.put(HaywardBindingConstants.PROPERTY_TYPE,
                                            HaywardTypeToRequest.VALVEACTUATOR);
                                    putStrObjIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, relayId);
                                    putStrObjIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_RELAY_TYPE,
                                            relayType);
                                    putStrObjIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_RELAY_FUNCTION,
                                            relay.getFunction());
                                    addBowContext(valveProps, bow);
                                    String relayName = relay.getName();
                                    if (relayName == null) {
                                        relayName = relayId != null ? relayId : "ValveActuator";
                                    }
                                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_VALVEACTUATOR, relayName,
                                            valveProps);
                                    continue;
                                }

                                Map<String, Object> relayProps = new HashMap<>();
                                relayProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.RELAY);
                                putStrObjIfNotNull(relayProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, relayId);
                                addBowContext(relayProps, bow);
                                String name = relay.getName();
                                if (name == null) {
                                    name = relayId != null ? relayId : "Relay";
                                }
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_RELAY, name, relayProps);
                            }
                        }

                        List<SensorConfig> sensors2 = bow.getSensors();
                        if (sensors2 != null) {
                            for (SensorConfig sensor : sensors2) {
                                Map<String, Object> sensorProps = new HashMap<>();
                                sensorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.SENSOR);
                                String sensorId = sensor.getSystemId();
                                putStrObjIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, sensorId);
                                addBowContext(sensorProps, bow);
                                putStrObjIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_TYPE,
                                        sensor.getType());
                                putStrObjIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_UNITS,
                                        sensor.getUnits());
                                String name = sensor.getName();
                                if (name == null) {
                                    name = sensorId != null ? sensorId : "Sensor";
                                }
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_SENSOR, name, sensorProps);
                            }
                        }

                        List<VirtualHeaterConfig> virtualHeaters = bow.getVirtualHeaters();
                        if (virtualHeaters != null) {
                            for (VirtualHeaterConfig virtualHeater : virtualHeaters) {
                                Map<String, Object> virtualHeaterProps = new HashMap<>();
                                virtualHeaterProps.put(HaywardBindingConstants.PROPERTY_TYPE,
                                        HaywardTypeToRequest.HEATER);
                                String virtualHeaterId = virtualHeater.getSystemId();
                                putStrObjIfNotNull(virtualHeaterProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID,
                                        virtualHeaterId);
                                addBowContext(virtualHeaterProps, bow);
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_VIRTUALHEATER, "Virtual Heater",
                                        virtualHeaterProps);

                                List<HeaterEquipConfig> heaters = virtualHeater.getHeaters();
                                if (heaters != null) {
                                    for (HeaterEquipConfig heater : heaters) {
                                        Map<String, Object> heaterProps = new HashMap<>();
                                        heaterProps.put(HaywardBindingConstants.PROPERTY_TYPE,
                                                HaywardTypeToRequest.HEATER);
                                        String heaterId = heater.getSystemId();
                                        putStrObjIfNotNull(heaterProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID,
                                                heaterId);
                                        addBowContext(heaterProps, bow);
                                        onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_HEATER,
                                                "Heater Equipment", heaterProps);
                                    }
                                }
                            }
                        }

                    }

                    List<PumpConfig> pumps = backyard.getPumps();
                    if (pumps != null) {
                        for (PumpConfig pump : pumps) {
                            Map<String, Object> pumpProps = new HashMap<>();
                            pumpProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.PUMP);
                            String pumpId = pump.getSystemId();
                            putStrObjIfNotNull(pumpProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, pumpId);
                            String name = pump.getName() != null ? pump.getName() : pumpId;
                            if (name == null) {
                                name = "Pump";
                            }
                            onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_PUMP, name, pumpProps);
                        }
                    }

                    List<RelayConfig> relays = backyard.getRelays();
                    if (relays != null) {
                        for (RelayConfig relay : relays) {
                            String relayId = relay.getSystemId();
                            String relayType = relay.getType();
                            if ("RLY_VALVE_ACTUATOR".equals(relayType)) {
                                Map<String, Object> valveProps = new HashMap<>();
                                valveProps.put(HaywardBindingConstants.PROPERTY_TYPE,
                                        HaywardTypeToRequest.VALVEACTUATOR);
                                putStrObjIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, relayId);
                                putStrObjIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_RELAY_TYPE, relayType);
                                putStrObjIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_RELAY_FUNCTION,
                                        relay.getFunction());
                                String name = relay.getName();
                                if (name == null) {
                                    name = relayId != null ? relayId : "ValveActuator";
                                }
                                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_VALVEACTUATOR, name, valveProps);
                                continue;
                            }

                            Map<String, Object> relayProps = new HashMap<>();
                            relayProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.RELAY);
                            putStrObjIfNotNull(relayProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, relayId);
                            String name = relay.getName();
                            if (name == null) {
                                name = relayId != null ? relayId : "Relay";
                            }
                            onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_RELAY, name, relayProps);
                        }
                    }

                    List<SensorConfig> sensors = backyard.getSensors();
                    if (sensors != null) {
                        for (SensorConfig sensor : sensors) {
                            Map<String, Object> sensorProps = new HashMap<>();
                            sensorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.SENSOR);
                            String sensorId = sensor.getSystemId();
                            putStrObjIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, sensorId);
                            putStrObjIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_TYPE,
                                    sensor.getType());
                            putStrObjIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_UNITS,
                                    sensor.getUnits());
                            String name = sensor.getName();
                            if (name == null) {
                                name = sensorId != null ? sensorId : "Sensor";
                            }
                            onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_SENSOR, name, sensorProps);
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

    private void putStrStrIfNotNull(Map<String, String> properties, String key, @Nullable String value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    private void addBowContext(Map<String, Object> properties, BodyOfWaterConfig bow) {
        String bowId = bow.getSystemId();
        putStrObjIfNotNull(properties, HaywardBindingConstants.PROPERTY_BOWID, bowId);
        String bowName = bow.getName();
        putStrObjIfNotNull(properties, HaywardBindingConstants.PROPERTY_BOWNAME, bowName);
    }

    public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
        BridgeHandler bridgehandler = thingHandler;
        String systemID = (String) properties.get(HaywardBindingConstants.PROPERTY_SYSTEM_ID);
        if (bridgehandler != null) {
            if (systemID != null) {
                ThingUID thingUID = new ThingUID(thingType, bridgehandler.getThing().getUID(), systemID);
                DiscoveryResult result = DiscoveryResultBuilder.create(thingUID)
                        .withBridge(bridgehandler.getThing().getUID())
                        .withRepresentationProperty(HaywardBindingConstants.PROPERTY_SYSTEM_ID)
                        .withLabel("Hayward " + label).withProperties(properties).build();
                thingDiscovered(result);
            }
        }
    }
}
