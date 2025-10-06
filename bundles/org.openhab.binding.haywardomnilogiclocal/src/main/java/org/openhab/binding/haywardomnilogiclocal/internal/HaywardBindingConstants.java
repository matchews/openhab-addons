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
package org.openhab.binding.haywardomnilogiclocal.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link HaywardBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class HaywardBindingConstants {

    public static final String BINDING_ID = "haywardomnilogiclocal";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BACKYARD = new ThingTypeUID(BINDING_ID, "backyard");
    public static final ThingTypeUID THING_TYPE_BOW = new ThingTypeUID(BINDING_ID, "bow");
    public static final ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_TYPE_CHLORINATOR = new ThingTypeUID(BINDING_ID, "chlorinator");
    public static final ThingTypeUID THING_TYPE_COLORLOGIC = new ThingTypeUID(BINDING_ID, "colorlogic");
    public static final ThingTypeUID THING_TYPE_FILTER = new ThingTypeUID(BINDING_ID, "filter");
    public static final ThingTypeUID THING_TYPE_HEATER = new ThingTypeUID(BINDING_ID, "heater");
    public static final ThingTypeUID THING_TYPE_PUMP = new ThingTypeUID(BINDING_ID, "pump");
    public static final ThingTypeUID THING_TYPE_RELAY = new ThingTypeUID(BINDING_ID, "relay");
    public static final ThingTypeUID THING_TYPE_SENSOR = new ThingTypeUID(BINDING_ID, "sensor");
    public static final ThingTypeUID THING_TYPE_VALVEACTUATOR = new ThingTypeUID(BINDING_ID, "valveActuator");
    public static final ThingTypeUID THING_TYPE_VIRTUALHEATER = new ThingTypeUID(BINDING_ID, "virtualHeater");
    public static final Set<ThingTypeUID> BRIDGE_THING_TYPES_UIDS = Set.of(THING_TYPE_BRIDGE);

    public static final Set<ThingTypeUID> THING_TYPES_UIDS = Set.of(HaywardBindingConstants.THING_TYPE_BACKYARD,
            HaywardBindingConstants.THING_TYPE_BOW, HaywardBindingConstants.THING_TYPE_BRIDGE,
            HaywardBindingConstants.THING_TYPE_CHLORINATOR, HaywardBindingConstants.THING_TYPE_COLORLOGIC,
            HaywardBindingConstants.THING_TYPE_FILTER, HaywardBindingConstants.THING_TYPE_HEATER,
            HaywardBindingConstants.THING_TYPE_PUMP, HaywardBindingConstants.THING_TYPE_RELAY,
            HaywardBindingConstants.THING_TYPE_SENSOR, HaywardBindingConstants.THING_TYPE_VALVEACTUATOR,
            HaywardBindingConstants.THING_TYPE_VIRTUALHEATER);

    // Bridge

    public static final String PROPERTY_BRIDGE_VSPSPEEDFORMAT = "VSP Speed Format";
    public static final String PROPERTY_BRIDGE_TIMEFORMAT = "Time Format";
    public static final String PROPERTY_BRIDGE_TIMEZONE = "Timezone";
    public static final String PROPERTY_BRIDGE_DST = "DST";
    public static final String PROPERTY_BRIDGE_INTERNETTIME = "Internet Time";
    public static final String PROPERTY_BRIDGE_UNITS = "MSP Units";
    public static final String PROPERTY_BRIDGE_CHLORDISPLAY = "Chlor Display";
    public static final String PROPERTY_BRIDGE_LANGUAGE = "MSP Units";
    public static final String PROPERTY_BRIDGE_UIDISPLAYMODE = "MSP Units";
    public static final String PROPERTY_BRIDGE_UIMOODCOLORENABLED = "MSP Units";
    public static final String PROPERTY_BRIDGE_UIHEATERSIMPLEMODE = "MSP Units";
    public static final String PROPERTY_BRIDGE_UIFILTERSIMPLEMODE = "MSP Units";
    public static final String PROPERTY_BRIDGE_UILIGHTSSIMPLEMODE = "MSP Units";

    // Backyard
    public static final String CHANNEL_BACKYARD_AIRTEMP = "backyardAirTemp";
    public static final String CHANNEL_BACKYARD_STATE = "backyardState";

    // Body of Water
    public static final String CHANNEL_BOW_WATERTEMP = "bowWaterTemp";
    public static final String CHANNEL_BOW_FLOW = "bowFlow";

    public static final String PROPERTY_BOW_TYPE = "type";
    public static final String PROPERTY_BOW_SHAREDTYPE = "sharedType";
    public static final String PROPERTY_BOW_SHAREDPRIORITY = "sharedPriority";
    public static final String PROPERTY_BOW_SHAREDEQUIPID = "sharedEquipmentSystemID";
    public static final String PROPERTY_BOW_SUPPORTSSPILLOVER = "supportsSpillover";
    public static final String PROPERTY_BOW_SIZEINGALLONS = "sizeInGallons";

    // Chlorinator
    public static final String CHANNEL_CHLORINATOR_STATUS = "chlorStatus";
    public static final String CHANNEL_CHLORINATOR_INSTANTSALTLEVEL = "chlorSaltInstantLevel";
    public static final String CHANNEL_CHLORINATOR_AVGSALTLEVEL = "chlorSaltAvgLevel";
    public static final String CHANNEL_CHLORINATOR_ALERT = "chlorAlert";
    public static final String CHANNEL_CHLORINATOR_ERROR = "chlorError";
    public static final String CHANNEL_CHLORINATOR_SCMODE = "chlorScMode";
    public static final String CHANNEL_CHLORINATOR_OPERATINGSTATE = "chlorOperatingState";
    public static final String CHANNEL_CHLORINATOR_TIMEDPERCENT = "chlorTimedPercent";
    public static final String CHANNEL_CHLORINATOR_OPERATINGMODE = "chlorOperatingMode";
    public static final String CHANNEL_CHLORINATOR_ENABLE = "chlorEnable";

    public static final String PROPERTY_CHLORINATOR_SHAREDTYPE = "chlorSharedType";
    public static final String PROPERTY_CHLORINATOR_MODE = "chlorMode";
    public static final String PROPERTY_CHLORINATOR_CELLTYPE = "cellType";
    public static final String PROPERTY_CHLORINATOR_DISPENSERTYPE = "dispenserType";

    // Colorlogic
    public static final String CHANNEL_COLORLOGIC_ENABLE = "enable";
    public static final String CHANNEL_COLORLOGIC_CURRENTSHOW = "currentShow";
    public static final String CHANNEL_COLORLOGIC_BRIGHTNESS = "brightness";
    public static final String CHANNEL_COLORLOGIC_SPEED = "speed";
    public static final String CHANNEL_COLORLOGIC_SPECIALEFFECT = "specialEffect";

    public static final String PROPERTY_COLORLOGIC_TYPE = "colorlogicType";

    // Filter
    public static final String CHANNEL_FILTER_ENABLE = "filterEnable";
    public static final String CHANNEL_FILTER_STATE = "filterState";
    public static final String CHANNEL_FILTER_VALVEPOSITION = "filterValvePosition";
    public static final String CHANNEL_FILTER_SPEED = "filterSpeed";
    public static final String CHANNEL_FILTER_LASTSPEED = "filterLastSpeed";
    public static final String CHANNEL_FILTER_WHYFILTERISON = "filterWhyFilterIsOn";
    public static final String CHANNEL_FILTER_FPOVERRIDE = "filterFpOverride";
    public static final String CHANNEL_FILTER_REPORTEDSPEED = "filterReportedSpeed";
    public static final String CHANNEL_FILTER_POWER = "filterPower";

    public static final String PROPERTY_FILTER_SHAREDTYPE = "filterSharedType";
    public static final String PROPERTY_FILTER_FILTERTYPE = "filterType";
    public static final String PROPERTY_FILTER_PRIMINGENABLED = "primingEnabled";
    public static final String PROPERTY_FILTER_MINSPEED = "filterminPercent";
    public static final String PROPERTY_FILTER_MAXSPEED = "filterMaxPercent";
    public static final String PROPERTY_FILTER_MINRPM = "filterMinRPM";
    public static final String PROPERTY_FILTER_MAXRPM = "filterMaxRPM";
    public static final String PROPERTY_FILTER_LOWSPEED = "filterLowSpeed";
    public static final String PROPERTY_FILTER_MEDSPEED = "filterMediumSpeed";
    public static final String PROPERTY_FILTER_HIGHSPEED = "filterHighSpeed";
    public static final String PROPERTY_FILTER_CUSTOMSPEED = "filterCustomSpeed";
    public static final String PROPERTY_FILTER_FREEZEPROTECTOVERRIDEINTERVAL = "filterFreezeProtectOverrideInterval";

    // Heater
    public static final String CHANNEL_HEATER_STATE = "heaterState";
    public static final String CHANNEL_HEATER_TEMP = "heaterTemp";
    public static final String CHANNEL_HEATER_ENABLE = "heaterEnable";
    public static final String CHANNEL_PRIORITY = "heaterPriority";
    public static final String CHANNEL_HEATER_MAINTAINFOR = "heaterMaintainFor";

    public static final String PROPERTY_HEATER_TYPE = "type";
    public static final String PROPERTY_HEATER_HEATERTYPE = "heaterType";
    public static final String PROPERTY_HEATER_SHAREDEQUIPID = "sharedEquipmentSystemID";

    // Pump
    public static final String CHANNEL_PUMP_ENABLE = "pumpEnable";
    public static final String CHANNEL_PUMP_SPEED = "pumpSpeed";

    public static final String PROPERTY_PUMP_TYPE = "pumpType";
    public static final String PROPERTY_PUMP_FUNCTION = "pumpFunction";
    public static final String PROPERTY_PUMP_PRIMINGENABLED = "pumpPrimingEnabled";
    public static final String PROPERTY_PUMP_MINSPEED = "minPumpPercent";
    public static final String PROPERTY_PUMP_MAXSPEED = "maxPumpPercent";
    public static final String PROPERTY_PUMP_MINRPM = "minPumpRPM";
    public static final String PROPERTY_PUMP_MAXRPM = "maxPumpRPM";
    public static final String PROPERTY_PUMP_LOWSPEED = "lowPumpSpeed";
    public static final String PROPERTY_PUMP_MEDSPEED = "mediumPumpSpeed";
    public static final String PROPERTY_PUMP_HIGHSPEED = "highPumpSpeed";
    public static final String PROPERTY_PUMP_CUSTOMSPEED = "customPumpSpeed";

    // Relay
    public static final String CHANNEL_RELAY_STATE = "relayState";

    public static final String PROPERTY_RELAY_TYPE = "relayType";
    public static final String PROPERTY_RELAY_FUNCTION = "relayFunction";

    // Sensor
    public static final String CHANNEL_SENSOR_DATA = "sensorData";
    public static final String PROPERTY_SENSOR_TYPE = "sensorType";
    public static final String PROPERTY_SENSOR_UNITS = "sensorUnits";

    // Virtual Heater
    public static final String CHANNEL_VIRTUALHEATER_CURRENTSETPOINT = "virtualHeaterCurrentSetpoint";
    public static final String CHANNEL_VIRTUALHEATER_ENABLE = "virtualHeaterEnable";
    public static final String CHANNEL_VIRTUALHEATER_SOLARSETPOINT = "virtualHeaterSolarSetpoint";
    public static final String CHANNEL_VIRTUALHEATER_MODE = "virtualHeaterMode";
    public static final String CHANNEL_VIRTUALHEATER_SILENTMODE = "virtualHeaterSilentMode";
    public static final String CHANNEL_VIRTUALHEATER_WHYON = "virtualHeaterWhyOn";

    public static final String PROPERTY_VIRTUALHEATER_SHAREDTYPE = "sharedType";
    public static final String PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP = "minSettableWaterTemp";
    public static final String PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP = "maxSettableWaterTemp";
    public static final String PROPERTY_VIRTUALHEATER_MAXWATERTEMP = "maxWaterTemp";

    // The properties associated with all things
    public static final String PROPERTY_SYSTEM_ID = "systemID";
    public static final String PROPERTY_TYPE = "thingType";
    public static final String PROPERTY_BOWNAME = "bowName";
    public static final String PROPERTY_BOWID = "bowID";

    // Hayward Command html
    public static final String COMMAND_PARAMETERS = "<?xml version=\"1.0\" encoding=\"utf-8\"?><GetTelemetry>";

    public static final String COMMAND_SCHEDULE = """
            <Parameter name="IsCountDownTimer" dataType="bool">false</Parameter>\
            <Parameter name="StartTimeHours" dataType="int">0</Parameter>\
            <Parameter name="StartTimeMinutes" dataType="int">0</Parameter>\
            <Parameter name="EndTimeHours" dataType="int">0</Parameter>\
            <Parameter name="EndTimeMinutes" dataType="int">0</Parameter>\
            <Parameter name="DaysActive" dataType="int">0</Parameter>\
            <Parameter name="Recurring" dataType="bool">false</Parameter>\
            """;
}
