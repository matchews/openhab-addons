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
import org.openhab.binding.haywardomnilogiclocal.internal.config.HeaterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.MspConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.PumpConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.RelayConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.SensorConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.config.VirtualHeaterConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardBridgeHandler;
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
public class HaywardDiscoveryService extends AbstractThingHandlerDiscoveryService<HaywardBridgeHandler> {
    private final Logger logger = LoggerFactory.getLogger(HaywardDiscoveryService.class);

    public HaywardDiscoveryService() {
        super(HaywardBridgeHandler.class, THING_TYPES_UIDS, 0, false);
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

        for (BackyardConfig backyard : config.getBackyards()) {
            Map<String, Object> backyardProps = new HashMap<>();
            backyardProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BACKYARD);
            String systemId = backyard.getSystemId();
            putIfNotNull(backyardProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, systemId);
            onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_BACKYARD, "Backyard", backyardProps);

            for (SensorConfig sensor : backyard.getSensors()) {
                Map<String, Object> sensorProps = new HashMap<>();
                sensorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.SENSOR);
                String sensorId = sensor.getSystemId();
                putIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, sensorId);
                putIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_TYPE, sensor.getType());
                putIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_UNITS, sensor.getUnits());
                String name = sensor.getName();
                if (name == null) {
                    name = sensorId != null ? sensorId : "Sensor";
                }
                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_SENSOR, name, sensorProps);
            }

            for (BodyOfWaterConfig bow : backyard.getBodiesOfWater()) {
                Map<String, Object> bowProps = new HashMap<>();
                bowProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BOW);
                String bowId = bow.getSystemId();
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, bowId);
                addBowContext(bowProps, bow);
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_TYPE, bow.getType());
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SHAREDTYPE, bow.getSharedType());
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SHAREDPRIORITY, bow.getSharedPriority());
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SHAREDEQUIPID,
                        bow.getSharedEquipmentSystemId());
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SUPPORTSSPILLOVER,
                        bow.getSupportsSpillover());
                putIfNotNull(bowProps, HaywardBindingConstants.PROPERTY_BOW_SIZEINGALLONS, bow.getSizeInGallons());

                String bowLabel = bow.getName();
                if (bowLabel == null) {
                    bowLabel = bowId != null ? bowId : "BodyOfWater";
                }
                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_BOW, bowLabel, bowProps);

                for (FilterConfig filter : bow.getFilters()) {
                    Map<String, Object> filterProps = new HashMap<>();
                    filterProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.FILTER);
                    String filterId = filter.getSystemId();
                    putIfNotNull(filterProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, filterId);
                    addBowContext(filterProps, bow);
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_FILTER,
                            filterId != null ? filterId : "Filter", filterProps);
                }

                for (HeaterConfig heater : bow.getHeaters()) {
                    Map<String, Object> heaterProps = new HashMap<>();
                    heaterProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.HEATER);
                    String heaterId = heater.getSystemId();
                    putIfNotNull(heaterProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, heaterId);
                    addBowContext(heaterProps, bow);
                    putIfNotNull(heaterProps, HaywardBindingConstants.PROPERTY_HEATER_TYPE, heater.getType());
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_HEATER,
                            heaterId != null ? heaterId : "Heater", heaterProps);
                }

                for (ChlorinatorConfig chlorinator : bow.getChlorinators()) {
                    Map<String, Object> chlorProps = new HashMap<>();
                    chlorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.CHLORINATOR);
                    String id = chlorinator.getSystemId();
                    putIfNotNull(chlorProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    addBowContext(chlorProps, bow);
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_CHLORINATOR,
                            id != null ? id : "Chlorinator", chlorProps);
                }

                for (ColorLogicLightConfig light : bow.getColorLogicLights()) {
                    Map<String, Object> lightProps = new HashMap<>();
                    lightProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.COLORLOGIC);
                    String id = light.getSystemId();
                    putIfNotNull(lightProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    addBowContext(lightProps, bow);
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_COLORLOGIC,
                            id != null ? id : "ColorLogic", lightProps);
                }

                for (RelayConfig relay : bow.getRelays()) {
                    String id = relay.getSystemId();
                    String relayType = relay.getType();
                    if ("RLY_VALVE_ACTUATOR".equals(relayType)) {
                        Map<String, Object> valveProps = new HashMap<>();
                        valveProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.VALVEACTUATOR);
                        putIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                        putIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_RELAY_TYPE, relayType);
                        putIfNotNull(valveProps, HaywardBindingConstants.PROPERTY_RELAY_FUNCTION, relay.getFunction());
                        addBowContext(valveProps, bow);
                        String name = relay.getName();
                        if (name == null) {
                            name = id != null ? id : "ValveActuator";
                        }
                        onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_VALVEACTUATOR, name, valveProps);
                        continue;
                    }

                    Map<String, Object> relayProps = new HashMap<>();
                    relayProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.RELAY);
                    putIfNotNull(relayProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    addBowContext(relayProps, bow);
                    String name = relay.getName();
                    if (name == null) {
                        name = id != null ? id : "Relay";
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_RELAY, name, relayProps);
                }

                for (SensorConfig sensor : bow.getSensors()) {
                    Map<String, Object> sensorProps = new HashMap<>();
                    sensorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.SENSOR);
                    String sensorId = sensor.getSystemId();
                    putIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, sensorId);
                    addBowContext(sensorProps, bow);
                    putIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_TYPE, sensor.getType());
                    putIfNotNull(sensorProps, HaywardBindingConstants.PROPERTY_SENSOR_UNITS, sensor.getUnits());
                    String name = sensor.getName();
                    if (name == null) {
                        name = sensorId != null ? sensorId : "Sensor";
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_SENSOR, name, sensorProps);
                }
            }

            for (PumpConfig pump : backyard.getPumps()) {
                Map<String, Object> pumpProps = new HashMap<>();
                pumpProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.PUMP);
                String pumpId = pump.getSystemId();
                putIfNotNull(pumpProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, pumpId);
                String name = pump.getName() != null ? pump.getName() : pumpId;
                if (name == null) {
                    name = "Pump";
                }
                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_PUMP, name, pumpProps);
            }
            for (VirtualHeaterConfig vh : backyard.getVirtualHeaters()) {
                Map<String, Object> vhProps = new HashMap<>();
                vhProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.VIRTUALHEATER);
                String id = vh.getSystemId();
                putIfNotNull(vhProps, HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_VIRTUALHEATER,
                        id != null ? id : "VirtualHeater", vhProps);
            }
        }
    }

    private void putIfNotNull(Map<String, Object> properties, String key, @Nullable String value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    private void addBowContext(Map<String, Object> properties, BodyOfWaterConfig bow) {
        String bowId = bow.getSystemId();
        putIfNotNull(properties, HaywardBindingConstants.PROPERTY_BOWID, bowId);
        String bowName = bow.getName();
        putIfNotNull(properties, HaywardBindingConstants.PROPERTY_BOWNAME, bowName);
    }

    public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
        HaywardBridgeHandler bridgehandler = thingHandler;
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
