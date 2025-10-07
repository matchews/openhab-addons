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

        // Get bridge properties
        List<org.openhab.binding.haywardomnilogiclocal.internal.config.SystemConfig> systems = config.getSystems();
        Map<String, String> bridgeProps = new HashMap<>();
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_VSPSPEEDFORMAT,
                systems.get(0).getMspVspSpeedFormat());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_TIMEFORMAT, systems.get(0).getMspTimeFormat());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_TIMEZONE, systems.get(0).getTimeZone());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_DST, systems.get(0).getDst());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_INTERNETTIME,
                systems.get(0).getInternetTime());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UNITS, systems.get(0).getUnits());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_CHLORDISPLAY,
                systems.get(0).getMspChlorDisplay());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_LANGUAGE, systems.get(0).getMspLanguage());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIDISPLAYMODE,
                systems.get(0).getUiDisplayMode());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIMOODCOLORENABLED,
                systems.get(0).getUiMoodColorEnabled());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIHEATERSIMPLEMODE,
                systems.get(0).getUiHeaterSimpleMode());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UIFILTERSIMPLEMODE,
                systems.get(0).getUiFilterSimpleMode());
        putStrStrIfNotNull(bridgeProps, BindingConstants.PROPERTY_BRIDGE_UILIGHTSSIMPLEMODE,
                systems.get(0).getUiLightsSimpleMode());

        BridgeHandler bridgehandler = thingHandler;
        bridgehandler.getThing().setProperties(bridgeProps);

        List<BackyardConfig> backyards = config.getBackyards();
        if (backyards != null) {
            for (BackyardConfig backyard : backyards) {

                String backyardName = backyard.getName();
                if (backyardName == null) {
                    backyardName = "Backyard";
                }

                Map<String, Object> backyardProps = new HashMap<>();
                backyardProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BACKYARD);
                putStrObjIfNotNull(backyardProps, BindingConstants.PROPERTY_SYSTEM_ID, backyard.getSystemId());
                putStrObjIfNotNull(backyardProps, BindingConstants.PROPERTY_BACKYARDSERVICEMODETIMEOUT,
                        backyard.getServiceModeTimeout());

                onDeviceDiscovered(BindingConstants.THING_TYPE_BACKYARD, backyardName, backyardProps);

                List<BodyOfWaterConfig> bows = backyard.getBodiesOfWater();
                if (bows != null) {
                    for (BodyOfWaterConfig bow : bows) {

                        String bowName = bow.getName();
                        if (bowName == null) {
                            bowName = "Backyard";
                        }

                        Map<String, Object> bowProps = new HashMap<>();
                        bowProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.BOW);
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_SYSTEM_ID, bow.getSystemId());

                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_TYPE, bow.getType());
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_SHAREDTYPE, bow.getSharedType());
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_SHAREDPRIORITY,
                                bow.getSharedPriority());
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_SHAREDEQUIPID,
                                bow.getSharedEquipmentSystemId());
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_SUPPORTSSPILLOVER,
                                bow.getSupportsSpillover());
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_USESPILLOVERFORFILTEROPERATIONS,
                                bow.getUseSpilloverForFilterOperations());
                        putStrObjIfNotNull(bowProps, BindingConstants.PROPERTY_BOW_SIZEINGALLONS,
                                bow.getSizeInGallons());

                        addBowContext(bowProps, bow);
                        onDeviceDiscovered(BindingConstants.THING_TYPE_BOW, bowName, bowProps);

                        List<ChlorinatorConfig> chlorinators = bow.getChlorinators();
                        if (chlorinators != null) {
                            for (ChlorinatorConfig chlorinator : chlorinators) {

                                String chlorinatorName = chlorinator.getName();
                                if (chlorinatorName == null) {
                                    chlorinatorName = "Chlorinator";
                                }

                                Map<String, Object> chlorProps = new HashMap<>();
                                chlorProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.CHLORINATOR);
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        chlorinator.getSystemId());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_SHAREDTYPE,
                                        chlorinator.getSharedType());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_ENABLED,
                                        chlorinator.getEnabled());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_MODE,
                                        chlorinator.getMode());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_TIMEDPERCENT,
                                        chlorinator.getTimedPercent());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_SUPERCHLORTIMEOUT,
                                        chlorinator.getSuperChlorTimeout());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_CELLTYPE,
                                        chlorinator.getCellType());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_DISPENSERTYPE,
                                        chlorinator.getDispenserType());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_ORPTIMEOUT,
                                        chlorinator.getOrpTimeout());
                                putStrObjIfNotNull(chlorProps, BindingConstants.PROPERTY_CHLORINATOR_ORPSENSORID,
                                        chlorinator.getOrpSensorId());

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
                                lightProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.COLORLOGIC);
                                putStrObjIfNotNull(lightProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        light.getSystemId());
                                putStrObjIfNotNull(lightProps, BindingConstants.PROPERTY_COLORLOGIC_TYPE,
                                        light.getType());
                                putStrObjIfNotNull(lightProps, BindingConstants.PROPERTY_COLORLOGIC_NODEID,
                                        light.getNodeId());
                                putStrObjIfNotNull(lightProps, BindingConstants.PROPERTY_COLORLOGIC_NETWORKED,
                                        light.getNetworked());

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
                                filterProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.FILTER);
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        filter.getSystemId());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_SHAREDTYPE,
                                        filter.getSharedType());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_FILTERTYPE,
                                        filter.getFilterType());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_MAXSPEED,
                                        filter.getMaxPumpSpeed());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_MINSPEED,
                                        filter.getMinPumpSpeed());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_MAXRPM,
                                        filter.getMaxPumpRpm());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_MINRPM,
                                        filter.getMinPumpRpm());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_MINPRIMINGINTERVAL,
                                        filter.getMinPrimingInterval());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_PRIMINGENABLED,
                                        filter.getPrimingEnabled());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_PRIMINGDURATION,
                                        filter.getPrimingDuration());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_COOLDOWNDURATION,
                                        filter.getCooldownDuration());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_SHUTDOWNREQUESTTIMEOUT,
                                        filter.getShutdownRequestTimeout());
                                putStrObjIfNotNull(filterProps,
                                        BindingConstants.PROPERTY_FILTER_NOWATERFLOWTIMEOUTENABLE,
                                        filter.getNoWaterFlowTimeoutEnable());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_NOWATERFLOWTIMEOUT,
                                        filter.getNoWaterFlowTimeoutTimeout());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_VALVECHANGEOFFENABLE,
                                        filter.getValveChangeOffEnable());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_VALVECHANGEOFFDURATION,
                                        filter.getValveChangeOffDuration());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTENABLE,
                                        filter.getFreezeProtectEnable());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTTEMP,
                                        filter.getFreezeProtectTemp());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_FREEZEPROTECTSPEED,
                                        filter.getFreezeProtectSpeed());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_SHAREDFITLERTIMEOUT,
                                        filter.getSharedFilterTimeout());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_FILTERVALVEPOSITION,
                                        filter.getFilterValvePosition());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_LOWSPEED,
                                        filter.getVspLowPumpSpeed());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_MEDSPEED,
                                        filter.getVspMediumPumpSpeed());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_HIGHSPEED,
                                        filter.getVspHighPumpSpeed());
                                putStrObjIfNotNull(filterProps, BindingConstants.PROPERTY_FILTER_CUSTOMSPEED,
                                        filter.getVspCustomPumpSpeed());
                                putStrObjIfNotNull(filterProps,
                                        BindingConstants.PROPERTY_FILTER_FREEZEPROTECTOVERRIDEINTERVAL,
                                        filter.getFreezeProtectOverrideInterval());

                                addBowContext(filterProps, bow);
                                onDeviceDiscovered(BindingConstants.THING_TYPE_FILTER, filterName, filterProps);
                            }
                        }

                        List<RelayConfig> relays = bow.getRelays();
                        if (relays != null) {
                            for (RelayConfig relay : relays) {

                                String relayType = relay.getType();
                                if ("RLY_VALVE_ACTUATOR".equals(relayType)) {

                                    String relayName = relay.getName();
                                    if (relayName == null) {
                                        relayName = "Valve Actuator";
                                    }

                                    Map<String, Object> valveProps = new HashMap<>();
                                    valveProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.VALVEACTUATOR);
                                    putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                            relay.getSystemId());
                                    putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_TYPE, relayType);
                                    putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_FUNCTION,
                                            relay.getFunction());
                                    putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_FREEZEPROTECTENABLE,
                                            relay.getFreezeProtectEnable());
                                    putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_VALVECYCLEENABLE,
                                            relay.getValveCycleEnable());
                                    putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_VALVECYCLETIME,
                                            relay.getValveCycleTime());

                                    addBowContext(valveProps, bow);
                                    onDeviceDiscovered(BindingConstants.THING_TYPE_VALVEACTUATOR, relayName,
                                            valveProps);
                                } else {

                                    String relayName = relay.getName();
                                    if (relayName == null) {
                                        relayName = "Relay";
                                    }

                                    Map<String, Object> relayProps = new HashMap<>();
                                    relayProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.RELAY);
                                    putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                            relay.getSystemId());
                                    putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_RELAY_FREEZEPROTECTENABLE,
                                            relay.getFreezeProtectEnable());
                                    putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_RELAY_VALVECYCLEENABLE,
                                            relay.getValveCycleEnable());
                                    putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_RELAY_VALVECYCLETIME,
                                            relay.getValveCycleTime());

                                    addBowContext(relayProps, bow);
                                    onDeviceDiscovered(BindingConstants.THING_TYPE_RELAY, relayName, relayProps);
                                }
                            }
                        }

                        List<SensorConfig> bowSensors = bow.getSensors();
                        if (bowSensors != null) {
                            for (SensorConfig sensor : bowSensors) {

                                String sensorName = sensor.getName();
                                if (sensorName == null) {
                                    sensorName = "Sensor";
                                }

                                Map<String, Object> sensorProps = new HashMap<>();
                                sensorProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.SENSOR);
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
                                virtualHeaterProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.HEATER);
                                putStrObjIfNotNull(virtualHeaterProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        virtualHeater.getSystemId());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_SHAREDTYPE,
                                        virtualHeater.getSharedType());
                                putStrObjIfNotNull(virtualHeaterProps, BindingConstants.PROPERTY_VIRTUALHEATER_ENABLED,
                                        virtualHeater.getEnabled());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_CURRENTSETPOINT,
                                        virtualHeater.getCurrentSetPoint());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_MAXWATERTEMP,
                                        virtualHeater.getMaxWaterTemp());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP,
                                        virtualHeater.getMinSettableWaterTemp());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP,
                                        virtualHeater.getMaxSettableWaterTemp());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_COOLDOWNENABLED,
                                        virtualHeater.getCooldownEnabled());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_EXTENDENABLED,
                                        virtualHeater.getExtendEnabled());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_BOOSTTIMEINTERVAL,
                                        virtualHeater.getBoostTimeInterval());
                                putStrObjIfNotNull(virtualHeaterProps,
                                        BindingConstants.PROPERTY_VIRTUALHEATER_HEATERBECOMEVALIDTIMEOUT,
                                        virtualHeater.getHeaterBecomeValidTimeout());

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
                                    heaterProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.HEATER);
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                            heater.getSystemId());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_TYPE,
                                            heater.getType());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_HEATERTYPE,
                                            heater.getHeaterType());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_ENABLED,
                                            heater.getEnabled());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_PRIORITY,
                                            heater.getPriority());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_RUNFORPRIORITY,
                                            heater.getRunForPriority());
                                    putStrObjIfNotNull(heaterProps,
                                            BindingConstants.PROPERTY_HEATER_ALLOWLOWSPEEDOPERATION,
                                            heater.getAllowLowSpeedOperation());
                                    putStrObjIfNotNull(heaterProps,
                                            BindingConstants.PROPERTY_HEATER_MINSPEEDFOROPERATION,
                                            heater.getMinSpeedForOperation());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_REQUIRESPRIMING,
                                            heater.getRequiresPriming());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_MINPRIMINGINTERVAL,
                                            heater.getMinPrimingInterval());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_TEMPDIFFINITIAL,
                                            heater.getTempDifferencetInitial());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_TEMPDIFFRUNNING,
                                            heater.getTempDifferenceRunning());
                                    putStrObjIfNotNull(heaterProps, BindingConstants.PROPERTY_HEATER_SENSORSYSTEMID,
                                            heater.getSensorSystemId());
                                    putStrObjIfNotNull(heaterProps,
                                            BindingConstants.PROPERTY_HEATER_SHAREDEQUIPMENTSYSTEMID,
                                            heater.getSharedEquipmentSystemID());

                                    addBowContext(heaterProps, bow);
                                    onDeviceDiscovered(BindingConstants.THING_TYPE_HEATER, heaterName, heaterProps);
                                }

                            }
                        }
                    }

                    List<PumpConfig> backyardPumps = backyard.getPumps();
                    if (backyardPumps != null) {
                        for (PumpConfig pump : backyardPumps) {

                            String pumpName = pump.getName();
                            if (pumpName == null) {
                                pumpName = "Pump";
                            }

                            Map<String, Object> pumpProps = new HashMap<>();
                            pumpProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.PUMP);
                            putStrObjIfNotNull(pumpProps, BindingConstants.PROPERTY_SYSTEM_ID, pump.getSystemId());

                            onDeviceDiscovered(BindingConstants.THING_TYPE_PUMP, pumpName, pumpProps);
                        }
                    }

                    List<RelayConfig> backyardRelays = backyard.getRelays();
                    if (backyardRelays != null) {
                        for (RelayConfig relay : backyardRelays) {

                            String relayType = relay.getType();
                            if ("RLY_VALVE_ACTUATOR".equals(relayType)) {

                                String relayName = relay.getName();
                                if (relayName == null) {
                                    relayName = "Valve Actuator";
                                }

                                Map<String, Object> valveProps = new HashMap<>();
                                valveProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.VALVEACTUATOR);
                                putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        relay.getSystemId());
                                putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_TYPE, relayType);
                                putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_FUNCTION,
                                        relay.getFunction());
                                putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_FREEZEPROTECTENABLE,
                                        relay.getFreezeProtectEnable());
                                putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_VALVECYCLEENABLE,
                                        relay.getValveCycleEnable());
                                putStrObjIfNotNull(valveProps, BindingConstants.PROPERTY_RELAY_VALVECYCLETIME,
                                        relay.getValveCycleTime());

                                onDeviceDiscovered(BindingConstants.THING_TYPE_VALVEACTUATOR, relayName, valveProps);
                            } else {

                                String relayName = relay.getName();
                                if (relayName == null) {
                                    relayName = "Relay";
                                }

                                Map<String, Object> relayProps = new HashMap<>();
                                relayProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.RELAY);
                                putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_SYSTEM_ID,
                                        relay.getSystemId());
                                putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_RELAY_FREEZEPROTECTENABLE,
                                        relay.getFreezeProtectEnable());
                                putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_RELAY_VALVECYCLEENABLE,
                                        relay.getValveCycleEnable());
                                putStrObjIfNotNull(relayProps, BindingConstants.PROPERTY_RELAY_VALVECYCLETIME,
                                        relay.getValveCycleTime());

                                onDeviceDiscovered(BindingConstants.THING_TYPE_RELAY, relayName, relayProps);
                            }
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
                            sensorProps.put(BindingConstants.PROPERTY_TYPE, HaywardTypeToRequest.SENSOR);
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

    private void putStrStrIfNotNull(Map<String, String> properties, String key, @Nullable String value) {
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
