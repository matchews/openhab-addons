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
 * The {@link BindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class BindingConstants {

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

    public static final Set<ThingTypeUID> THING_TYPES_UIDS = Set.of(BindingConstants.THING_TYPE_BACKYARD,
            BindingConstants.THING_TYPE_BOW, BindingConstants.THING_TYPE_BRIDGE,
            BindingConstants.THING_TYPE_CHLORINATOR, BindingConstants.THING_TYPE_COLORLOGIC,
            BindingConstants.THING_TYPE_FILTER, BindingConstants.THING_TYPE_HEATER, BindingConstants.THING_TYPE_PUMP,
            BindingConstants.THING_TYPE_RELAY, BindingConstants.THING_TYPE_SENSOR,
            BindingConstants.THING_TYPE_VALVEACTUATOR, BindingConstants.THING_TYPE_VIRTUALHEATER);

    // Bridge
    public static final String PROPERTY_BRIDGE_VSPSPEEDFORMAT = "VSP Speed Format";
    public static final String PROPERTY_BRIDGE_TIMEFORMAT = "Time Format";
    public static final String PROPERTY_BRIDGE_TIMEZONE = "Timezone";
    public static final String PROPERTY_BRIDGE_DST = "DST";
    public static final String PROPERTY_BRIDGE_INTERNETTIME = "Internet Time";
    public static final String PROPERTY_BRIDGE_UNITS = "MSP Units";
    public static final String PROPERTY_BRIDGE_CHLORDISPLAY = "Chlor Display";
    public static final String PROPERTY_BRIDGE_LANGUAGE = "Language";
    public static final String PROPERTY_BRIDGE_UIDISPLAYMODE = "UI Display Mode";
    public static final String PROPERTY_BRIDGE_UIMOODCOLORENABLED = "UI Mood Color Enabled";
    public static final String PROPERTY_BRIDGE_UIHEATERSIMPLEMODE = "UI Heater Simple Mode";
    public static final String PROPERTY_BRIDGE_UIFILTERSIMPLEMODE = "UI Filter Simple Mode";
    public static final String PROPERTY_BRIDGE_UILIGHTSSIMPLEMODE = "UL Lights Simple Mode";

    // Backyard
    public static final String CHANNEL_BACKYARD_AIRTEMP = "backyardAirTemp";
    public static final String CHANNEL_BACKYARD_STATE = "backyardState";

    public static final String PROPERTY_BACKYARDSERVICEMODETIMEOUT = "Service Mode Timeout";

    // Body of Water
    public static final String CHANNEL_BOW_WATERTEMP = "bowWaterTemp";
    public static final String CHANNEL_BOW_FLOW = "bowFlow";

    public static final String PROPERTY_BOW_TYPE = "Type";
    public static final String PROPERTY_BOW_SHAREDTYPE = "Shared Type";
    public static final String PROPERTY_BOW_SHAREDPRIORITY = "Shared Priority";
    public static final String PROPERTY_BOW_SHAREDEQUIPID = "Shared Equipment SystemID";
    public static final String PROPERTY_BOW_SUPPORTSSPILLOVER = "Supports Spillover";
    public static final String PROPERTY_BOW_USESPILLOVERFORFILTEROPERATIONS = "Use Spillover for Filter Operations";
    public static final String PROPERTY_BOW_SIZEINGALLONS = "Size In Gallons";

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

    public static final String PROPERTY_CHLORINATOR_SHAREDTYPE = "Shared Type";
    public static final String PROPERTY_CHLORINATOR_ENABLED = "Enabled";
    public static final String PROPERTY_CHLORINATOR_MODE = "Mode";
    public static final String PROPERTY_CHLORINATOR_TIMEDPERCENT = "Timed Percent";
    public static final String PROPERTY_CHLORINATOR_SUPERCHLORTIMEOUT = "Super Chlor Timeout";
    public static final String PROPERTY_CHLORINATOR_CELLTYPE = "Cell Type";
    public static final String PROPERTY_CHLORINATOR_DISPENSERTYPE = "Dispenser Type";
    public static final String PROPERTY_CHLORINATOR_ORPTIMEOUT = "ORP Timeout";
    public static final String PROPERTY_CHLORINATOR_ORPSENSORID = "ORP Sensor ID";

    // Colorlogic
    public static final String CHANNEL_COLORLOGIC_ENABLE = "enable";
    public static final String CHANNEL_COLORLOGIC_CURRENTSHOW = "currentShow";
    public static final String CHANNEL_COLORLOGIC_BRIGHTNESS = "brightness";
    public static final String CHANNEL_COLORLOGIC_SPEED = "speed";
    public static final String CHANNEL_COLORLOGIC_SPECIALEFFECT = "specialEffect";

    public static final String PROPERTY_COLORLOGIC_TYPE = "Type";
    public static final String PROPERTY_COLORLOGIC_NODEID = "Node ID";
    public static final String PROPERTY_COLORLOGIC_NETWORKED = "Networked";

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

    public static final String PROPERTY_FILTER_SHAREDTYPE = "Shared Type";
    public static final String PROPERTY_FILTER_FILTERTYPE = "Filter Type";
    public static final String PROPERTY_FILTER_MAXSPEED = "Maximum Speed (%)";
    public static final String PROPERTY_FILTER_MINSPEED = "Minimum Speed (%)";
    public static final String PROPERTY_FILTER_MAXRPM = "Maximum Speed (RPM)";
    public static final String PROPERTY_FILTER_MINRPM = "Minimum Speed (RPM)";
    public static final String PROPERTY_FILTER_MINPRIMINGINTERVAL = "Minimum Priming Interval";
    public static final String PROPERTY_FILTER_PRIMINGENABLED = "Priming Enabled";
    public static final String PROPERTY_FILTER_PRIMINGDURATION = "Priming Duration";
    public static final String PROPERTY_FILTER_COOLDOWNDURATION = "Cooldown Duration";
    public static final String PROPERTY_FILTER_SHUTDOWNREQUESTTIMEOUT = "Shutdown Request Timeout";
    public static final String PROPERTY_FILTER_NOWATERFLOWTIMEOUTENABLE = "No Water Flow Timeout Enable";
    public static final String PROPERTY_FILTER_NOWATERFLOWTIMEOUT = "No Water Flow Timeout";
    public static final String PROPERTY_FILTER_VALVECHANGEOFFENABLE = "Valve Change Off Enable";
    public static final String PROPERTY_FILTER_VALVECHANGEOFFDURATION = "Valve Change Off Duration";
    public static final String PROPERTY_FILTER_FREEZEPROTECTENABLE = "Freeze Protect Enable";
    public static final String PROPERTY_FILTER_FREEZEPROTECTTEMP = "Freeze Protect Temp";
    public static final String PROPERTY_FILTER_FREEZEPROTECTSPEED = "Freeze Protect Speed";
    public static final String PROPERTY_FILTER_SHAREDFITLERTIMEOUT = "Shared Filter Timeout";
    public static final String PROPERTY_FILTER_FILTERVALVEPOSITION = "Filter Valve Position";
    public static final String PROPERTY_FILTER_LOWSPEED = "Low Speed %";
    public static final String PROPERTY_FILTER_MEDSPEED = "Medium Speed %";
    public static final String PROPERTY_FILTER_HIGHSPEED = "High Speed %";
    public static final String PROPERTY_FILTER_CUSTOMSPEED = "Custom Speed %";
    public static final String PROPERTY_FILTER_FREEZEPROTECTOVERRIDEINTERVAL = "Freeze Protect Override Interval";

    // Heater
    public static final String CHANNEL_HEATER_STATE = "heaterState";
    public static final String CHANNEL_HEATER_TEMP = "heaterTemp";
    public static final String CHANNEL_HEATER_ENABLE = "heaterEnable";
    public static final String CHANNEL_PRIORITY = "heaterPriority";
    public static final String CHANNEL_HEATER_MAINTAINFOR = "heaterMaintainFor";

    public static final String PROPERTY_HEATER_TYPE = "Type";
    public static final String PROPERTY_HEATER_HEATERTYPE = "Heater Type";
    public static final String PROPERTY_HEATER_ENABLED = "Enabled";
    public static final String PROPERTY_HEATER_PRIORITY = "Priority";
    public static final String PROPERTY_HEATER_RUNFORPRIORITY = "Run For Priority";
    public static final String PROPERTY_HEATER_ALLOWLOWSPEEDOPERATION = "Allow Low Speed Operation";
    public static final String PROPERTY_HEATER_MINSPEEDFOROPERATION = "Minimum Speed for Operation";
    public static final String PROPERTY_HEATER_REQUIRESPRIMING = "Requires Priming";
    public static final String PROPERTY_HEATER_MINPRIMINGINTERVAL = "Minimum Priming Interval";
    public static final String PROPERTY_HEATER_TEMPDIFFINITIAL = "Temp Difference Initial";
    public static final String PROPERTY_HEATER_TEMPDIFFRUNNING = "Tmep Difference Running";
    public static final String PROPERTY_HEATER_SENSORSYSTEMID = "Sensor System ID";
    public static final String PROPERTY_HEATER_SHAREDEQUIPMENTSYSTEMID = "Shared Equipment System ID";

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

    public static final String PROPERTY_RELAY_TYPE = "Type";
    public static final String PROPERTY_RELAY_FUNCTION = "Function";
    public static final String PROPERTY_RELAY_FREEZEPROTECTENABLE = "Freeze Protect Enable";
    public static final String PROPERTY_RELAY_VALVECYCLEENABLE = "Valve Cycle Enable";
    public static final String PROPERTY_RELAY_VALVECYCLETIME = "Valve Cycle Time";

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

    public static final String PROPERTY_VIRTUALHEATER_SHAREDTYPE = "Shared Type";
    public static final String PROPERTY_VIRTUALHEATER_ENABLED = "Enabled";
    public static final String PROPERTY_VIRTUALHEATER_CURRENTSETPOINT = "Current Setpoint";
    public static final String PROPERTY_VIRTUALHEATER_MAXWATERTEMP = "Max Water Temp";
    public static final String PROPERTY_VIRTUALHEATER_MINSETTABLEWATERTEMP = "Min Settable Water Temp";
    public static final String PROPERTY_VIRTUALHEATER_MAXSETTABLEWATERTEMP = "Max Settable Water Temp";
    public static final String PROPERTY_VIRTUALHEATER_COOLDOWNENABLED = "Cooldown Enabled";
    public static final String PROPERTY_VIRTUALHEATER_EXTENDENABLED = "Extend Enabled";
    public static final String PROPERTY_VIRTUALHEATER_BOOSTTIMEINTERVAL = "Boost Time Interval";
    public static final String PROPERTY_VIRTUALHEATER_HEATERBECOMEVALIDTIMEOUT = "Becomes Valid Timeout";

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
