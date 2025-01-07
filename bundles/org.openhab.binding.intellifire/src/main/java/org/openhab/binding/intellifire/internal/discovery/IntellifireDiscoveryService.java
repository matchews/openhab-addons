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

import static org.openhab.binding.intellifire.internal.IntellifireBindingConstants.THING_TYPES_UIDS;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.intellifire.internal.IntellifireAccount;
import org.openhab.binding.intellifire.internal.IntellifireBindingConstants;
import org.openhab.binding.intellifire.internal.IntellifireLocation;
import org.openhab.binding.intellifire.internal.IntellifirePollData;
import org.openhab.binding.intellifire.internal.handlers.IntellifireBridgeHandler;
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
@Component(scope = ServiceScope.PROTOTYPE, service = IntellifireDiscoveryService.class)
@NonNullByDefault
public class IntellifireDiscoveryService extends AbstractThingHandlerDiscoveryService<IntellifireBridgeHandler> {
    private final Logger logger = LoggerFactory.getLogger(IntellifireDiscoveryService.class);

    public IntellifireDiscoveryService() {
        super(IntellifireBridgeHandler.class, THING_TYPES_UIDS, 0, false);
    }

    @Override
    protected void startScan() {
        try {
            thingHandler.getAccountLocations();
            IntellifireAccount account = thingHandler.account;

            for (int i = 0; i < account.locations.size(); i++) {
                String locationID = account.locations.get(i).locationId;
                IntellifireLocation fireplaces = thingHandler.getFireplaces(locationID);
                if (fireplaces != null) {
                    account.locations.get(i).fireplaces = fireplaces;
                }

                // for (int j = 0; j < location.fireplaces.size(); j++) {
                for (int j = 0; j < account.locations.get(i).fireplaces.fireplaces.size(); j++) {
                    // Construct thing name
                    String thingName = account.locations.get(i).fireplaces.fireplaces.get(j).name;

                    // Construct representative property
                    String serialNumber = account.locations.get(i).fireplaces.fireplaces.get(j).serial;
                    String uniqueId = String.format("%s-%s-%s", locationID, serialNumber, "fireplace");

                    Map<String, Object> properties = new HashMap<>();
                    properties.put(IntellifireBindingConstants.PROPERTY_LOCATIONID, locationID);
                    properties.put(IntellifireBindingConstants.PROPERTY_SERIALNUMBER, serialNumber);
                    properties.put(IntellifireBindingConstants.PROPERTY_UNIQUEID, uniqueId);

                    // Fireplace properties from getFireplaces
                    properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_BRAND,
                            account.locations.get(i).fireplaces.fireplaces.get(j).brand);
                    properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_NAME,
                            account.locations.get(i).fireplaces.fireplaces.get(j).name);

                    // Fireplace properties from cloudPoll
                    IntellifirePollData cloudPollFireplace = thingHandler.cloudPollFireplace(serialNumber);
                    if (cloudPollFireplace != null) {
                        account.locations.get(i).fireplaces.fireplaces.get(j).pollData = cloudPollFireplace;
                    }
                    properties.put(IntellifireBindingConstants.PROPERTY_APIKEY,
                            account.locations.get(i).fireplaces.fireplaces.get(j).apiKey);
                    properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_FIRMWAREVERSION,
                            account.locations.get(i).fireplaces.fireplaces.get(j).pollData.firmwareVersionString);
                    properties.put(IntellifireBindingConstants.PROPERTY_IPADDRESS,
                            account.locations.get(i).fireplaces.fireplaces.get(j).pollData.ipv4Address);

                    if (account.locations.get(i).fireplaces.fireplaces.get(j).pollData.featureFan == 1) {
                        properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_FEATURE_FAN, 1);
                    } else {
                        properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_FEATURE_FAN, 0);
                    }

                    if (account.locations.get(i).fireplaces.fireplaces.get(j).pollData.featureLight == 1) {
                        properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_FEATURE_LIGHT, 1);
                    } else {
                        properties.put(IntellifireBindingConstants.PROPERTY_FIREPLACE_FEATURE_LIGHT, 0);
                    }

                    // Add device
                    onDeviceDiscovered(IntellifireBindingConstants.THING_TYPE_FIREPLACE, thingName + " Fireplace",
                            properties);

                    // Remote
                    if (account.locations.get(i).fireplaces.fireplaces.get(j).pollData.featureThermostat == 1) {
                        properties.clear();
                        properties.put(IntellifireBindingConstants.PROPERTY_APIKEY,
                                account.locations.get(i).fireplaces.fireplaces.get(j).apiKey);
                        properties.put(IntellifireBindingConstants.PROPERTY_IPADDRESS,
                                account.locations.get(i).fireplaces.fireplaces.get(j).pollData.ipv4Address);
                        properties.put(IntellifireBindingConstants.PROPERTY_LOCATIONID, locationID);
                        properties.put(IntellifireBindingConstants.PROPERTY_SERIALNUMBER, serialNumber);
                        uniqueId = String.format("%s-%s-%s", locationID, serialNumber, "thermostat");
                        properties.put(IntellifireBindingConstants.PROPERTY_UNIQUEID, uniqueId);
                        onDeviceDiscovered(IntellifireBindingConstants.THING_TYPE_REMOTE, thingName + " Remote",
                                properties);
                    }
                }
            }
        } catch (

        InterruptedException e) {
            logger.error("Discovery Error", e);
        }
    }

    public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
        IntellifireBridgeHandler bridgehandler = thingHandler;
        String uniqueId = (String) properties.get(IntellifireBindingConstants.PROPERTY_UNIQUEID);
        if (bridgehandler != null) {
            if (uniqueId != null) {
                ThingUID thingUID = new ThingUID(thingType, bridgehandler.getThing().getUID(), uniqueId);
                DiscoveryResult result = DiscoveryResultBuilder.create(thingUID)
                        .withBridge(bridgehandler.getThing().getUID())
                        .withRepresentationProperty(IntellifireBindingConstants.PROPERTY_UNIQUEID)
                        .withLabel("Intellifire " + label).withProperties(properties).build();
                thingDiscovered(result);
            }
        }
    }
}
