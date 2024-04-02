/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.intellifire.internal.discovery;

import static org.openhab.binding.intellifire.internal.intellifireBindingConstants.THING_TYPES_UIDS;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.intellifire.internal.intellifireAccount;
import org.openhab.binding.intellifire.internal.intellifireBindingConstants;
import org.openhab.binding.intellifire.internal.intellifireBridgeHandler;
import org.openhab.binding.intellifire.internal.intellifireLocation;
import org.openhab.binding.intellifire.internal.intellifirePollData;
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
@Component(scope = ServiceScope.PROTOTYPE, service = intellifireDiscoveryService.class)
@NonNullByDefault
public class intellifireDiscoveryService extends AbstractThingHandlerDiscoveryService<intellifireBridgeHandler> {
    private final Logger logger = LoggerFactory.getLogger(intellifireDiscoveryService.class);

    public intellifireDiscoveryService() {
        super(intellifireBridgeHandler.class, THING_TYPES_UIDS, 0, false);
    }

    @Override
    protected void startScan() {

        try {
            intellifireAccount account = thingHandler.getAccountLocations();
            for (int i = 0; i < account.locations.size(); i++) {
                String locationID = account.locations.get(i).location_id.toString();
                intellifireLocation location = thingHandler.getFireplaces(locationID);
                for (int j = 0; j < location.fireplaces.size(); j++) {

                    // Construct thing name
                    String thingName = account.locations.get(i).location_name + " " + location.fireplaces.get(j).name;

                    // Fireplace properties from getFireplaces
                    Map<String, Object> properties = new HashMap<>();
                    properties.put(intellifireBindingConstants.PROPERTY_FIREPLACE_BRAND,
                            location.fireplaces.get(j).brand);
                    properties.put(intellifireBindingConstants.PROPERTY_FIREPLACE_NAME,
                            location.fireplaces.get(j).name);
                    properties.put(intellifireBindingConstants.PROPERTY_FIREPLACE_SERIAL,
                            location.fireplaces.get(j).serial);

                    // Fireplace properties from cloudPoll
                    intellifirePollData pollData = thingHandler.cloudPollFireplace(location.fireplaces.get(j).serial);

                    properties.put(intellifireBindingConstants.PROPERTY_FIREPLACE_FIRMWAREVERSION,
                            pollData.firmware_version_string);
                    properties.put(intellifireBindingConstants.PROPERTY_FIREPLACE_IPADDRESS, pollData.ipv4_address);

                    onDeviceDiscovered(intellifireBindingConstants.THING_TYPE_FIREPLACE, thingName + " Fireplace",
                            properties);

                    // Fan
                    if (pollData.feature_fan == 1) {
                        properties.clear();
                        onDeviceDiscovered(intellifireBindingConstants.THING_TYPE_FAN, thingName + " Fan", properties);
                    }

                    // Light
                    if (pollData.feature_light == 1) {
                        properties.clear();
                        onDeviceDiscovered(intellifireBindingConstants.THING_TYPE_LIGHT, thingName + " Light",
                                properties);
                    }

                    // Remote
                    if (pollData.feature_thermostat == 1) {
                        properties.clear();
                        onDeviceDiscovered(intellifireBindingConstants.THING_TYPE_REMOTE, thingName + "Remote",
                                properties);
                    }

                }
            }
        } catch (InterruptedException e) {
            logger.error("Discovery Error", e);
        }
    }

    public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
        intellifireBridgeHandler bridgehandler = thingHandler;
        String systemID = "1"; // (String) properties.get(intellifireBindingConstants.PROPERTY_SYSTEM_ID);
        if (bridgehandler != null) {
            if (systemID != null) {
                ThingUID thingUID = new ThingUID(thingType, bridgehandler.getThing().getUID(), systemID);
                DiscoveryResult result = DiscoveryResultBuilder.create(thingUID)
                        .withBridge(bridgehandler.getThing().getUID())
                        // .withRepresentationProperty(intellifireBindingConstants.PROPERTY_SYSTEM_ID)
                        .withLabel("Intellifire " + label).withProperties(properties).build();
                thingDiscovered(result);
            }
        }
    }
}
