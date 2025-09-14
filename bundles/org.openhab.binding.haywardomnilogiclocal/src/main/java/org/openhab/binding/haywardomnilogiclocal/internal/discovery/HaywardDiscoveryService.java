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
import org.openhab.binding.haywardomnilogiclocal.internal.config.SystemConfig;
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

        for (SystemConfig system : config.getSystems()) {
            String systemId = system.getSystemId();

            for (BackyardConfig backyard : system.getBackyards()) {
                Map<String, Object> backyardProps = new HashMap<>();
                backyardProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BACKYARD);
                if (systemId != null) {
                    backyardProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, systemId);
                }
                onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_BACKYARD, "Backyard", backyardProps);

                for (BodyOfWaterConfig bow : backyard.getBodiesOfWater()) {
                    Map<String, Object> bowProps = new HashMap<>();
                    bowProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BOW);
                    String bowId = bow.getSystemId();
                    if (bowId != null) {
                        bowProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, bowId);
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_BOW, bowId != null ? bowId : "BodyOfWater",
                            bowProps);
                }

                for (PumpConfig pump : backyard.getPumps()) {
                    Map<String, Object> pumpProps = new HashMap<>();
                    pumpProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.PUMP);
                    String pumpId = pump.getSystemId();
                    if (pumpId != null) {
                        pumpProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, pumpId);
                    }
                    String name = pump.getName() != null ? pump.getName() : pumpId;
                    if (name == null) {
                        name = "Pump";
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_PUMP, name, pumpProps);
                }

                for (FilterConfig filter : backyard.getFilters()) {
                    Map<String, Object> filterProps = new HashMap<>();
                    filterProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.FILTER);
                    String filterId = filter.getSystemId();
                    if (filterId != null) {
                        filterProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, filterId);
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_FILTER,
                            filterId != null ? filterId : "Filter", filterProps);
                }

                for (HeaterConfig heater : backyard.getHeaters()) {
                    Map<String, Object> heaterProps = new HashMap<>();
                    heaterProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.HEATER);
                    String heaterId = heater.getSystemId();
                    if (heaterId != null) {
                        heaterProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, heaterId);
                    }
                    if (heater.getType() != null) {
                        heaterProps.put(HaywardBindingConstants.PROPERTY_HEATER_TYPE, heater.getType());
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_HEATER,
                            heaterId != null ? heaterId : "Heater", heaterProps);
                }

                for (ChlorinatorConfig chlorinator : backyard.getChlorinators()) {
                    Map<String, Object> chlorProps = new HashMap<>();
                    chlorProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.CHLORINATOR);
                    String id = chlorinator.getSystemId();
                    if (id != null) {
                        chlorProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_CHLORINATOR, id != null ? id : "Chlorinator",
                            chlorProps);
                }

                for (ColorLogicLightConfig light : backyard.getColorLogicLights()) {
                    Map<String, Object> lightProps = new HashMap<>();
                    lightProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.COLORLOGIC);
                    String id = light.getSystemId();
                    if (id != null) {
                        lightProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_COLORLOGIC, id != null ? id : "ColorLogic",
                            lightProps);
                }

                for (RelayConfig relay : backyard.getRelays()) {
                    Map<String, Object> relayProps = new HashMap<>();
                    relayProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.RELAY);
                    String id = relay.getSystemId();
                    if (id != null) {
                        relayProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    }
                    String label = relay.getName();
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_RELAY, label != null ? label : id,
                            relayProps);
                }

                for (VirtualHeaterConfig vh : backyard.getVirtualHeaters()) {
                    Map<String, Object> vhProps = new HashMap<>();
                    vhProps.put(HaywardBindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.VIRTUALHEATER);
                    String id = vh.getSystemId();
                    if (id != null) {
                        vhProps.put(HaywardBindingConstants.PROPERTY_SYSTEM_ID, id);
                    }
                    onDeviceDiscovered(HaywardBindingConstants.THING_TYPE_VIRTUALHEATER,
                            id != null ? id : "VirtualHeater", vhProps);
                }
            }
        }
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
