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
package org.openhab.binding.haywardomnilogiclocal.internal.net;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Helper for building XML command strings sent to the Hayward controller.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class CommandBuilder {

    public static final String XML_DECLARATION = """
            <?xml version="1.0" encoding="utf-8"?>
            <Request xmlns="http://nextgen.hayward.com/api">
            """;

    public static final String REQUEST_CONFIGURATION = """
                <Name>RequestConfiguration</Name>
            """;

    private static final String GET_ALARMLIST = """
               <Name>GetAllAlarmList</Name>
            """;

    private static final String GET_TELEMETRY = """
                <Name>RequestTelemetryData</Name>
            """;

    private static final String GET_UI_FILTER_DIAGNOSTIC_INFO = """
            <Name>GetUIFilterDiagnosticInfo</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="EquipmentID" dataType="int">%s</Parameter>
            """;

    private static final String RUN_GROUP_CMD = """
            <Name>RunGroupCmd</Name>
            <Parameters>
                <Parameter name="GroupID" dataType="int">%s</Parameter>
                <Parameter name="Data" dataType="int">%s</Parameter>
            """;

    private static final String SET_CHLOR_ENABLE = """
            <Name>SetCHLOREnable</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="Enabled" dataType="bool">%s</Parameter>
            """;

    private static final String SET_CHLOR_PARAMS = """
            <Name>SetCHLORParams</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="ChlorID" dataType="int">%s</Parameter>
                <Parameter name="CfgState" dataType="byte">%s</Parameter>
                <Parameter name="OpMode" dataType="byte">%s</Parameter>
                <Parameter name="BOWType" dataType="byte">%s</Parameter>
                <Parameter name="CellType" dataType="byte">%s</Parameter>
                <Parameter name="TimedPercent" dataType="byte">%s</Parameter>
                <Parameter name="SCTimeout" dataType="byte">%s</Parameter>
                <Parameter name="ORPTimout" dataType="byte">%s</Parameter>
            """;

    private static final String SET_EQUIPMENT_CMD = """
            <Name>SetUIEquipmentCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="EquipmentID" dataType="int">%s</Parameter>
                <Parameter name="IsOn" dataType="bool">%s</Parameter>
            """;

    private static final String SET_HEATER_ENABLE = """
            <Name>SetHeaterEnable</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="HeaterID" dataType="int">%s</Parameter>
                <Parameter name="Enabled" dataType="bool">%s</Parameter>
            """;

    private static final String SET_STANDALONE_LIGHTSHOW = """
            <Name>SetStandAloneLightShow</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="LightID" dataType="int">%s</Parameter>
                <Parameter name="Show" dataType="byte">%s</Parameter>
            """;

    private static final String SET_STANDALONE_LIGHTSHOW_OMNIDIRECT = """
              <Name>SetStandAloneLightShow</Name>
              <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="LightID" dataType="int">%s</Parameter>
                <Parameter name="Show" dataType="byte">%s</Parameter>
                <Parameter name="Speed" dataType="byte">%s</Parameter>
                <Parameter name="Brightness" dataType="byte">%s</Parameter>
                <Parameter name="Reserved" dataType="byte">0</Parameter>
            """;

    private static final String SET_UI_HEATER_CMD = """
            <Name>SetUIHeaterCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="HeaterID" dataType="int">%s</Parameter>
                <Parameter name="Temp" dataType="bool">%s</Parameter>
            """;

    // TODO
    private static final String SET_UI_HEATER_MODE_CMD = """
            <Name>SetUIHeaterModeCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="HeaterID" dataType="int">%s</Parameter>
                <Parameter name="Mode" dataType="int">%s</Parameter>
            """;

    private static final String SET_UI_SOLAR_SETPOINT_CMD = """
            <Name>SetUISolarSetPointCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="HeaterID" dataType="int">%s</Parameter>
                <Parameter name="Temp" dataType="bool">%s</Parameter>
            """;
    // TODO
    private static final String SET_UI_SPILLOVER_CMD = """
            <Name>SetUISpilloverCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="Speed" dataType="int">%s</Parameter>
            """;

    private static final String SET_UI_SUPER_CHLOR_CMD = """
            <Name>SetUISuperCHLORCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="ChlorID" dataType="int">%s</Parameter>
                <Parameter name="IsOn" dataType=\"bool\">%s</Parameter>
            """;

    private static final String SET_UI_SUPER_CHLOR_TIMEOUT_CMD = """
            <Name>SetUISuperCHLORTimeoutCmd</Name>
            <Parameters>
                <Parameter name="PoolID" dataType="int">%s</Parameter>
                <Parameter name="ChlorID" dataType="int">%s</Parameter>
                <Parameter name="Timeout" dataType=\"bool\">%s</Parameter>
            """;

    public static final String COMMAND_SCHEDULE = """
                <Parameter name="IsCountDownTimer" dataType="bool">0</Parameter>
                <Parameter name="StartTimeHours" dataType="int">0</Parameter>
                <Parameter name="StartTimeMinutes" dataType="int">0</Parameter>
                <Parameter name="EndTimeHours" dataType="int">0</Parameter>
                <Parameter name="EndTimeMinutes" dataType="int">0</Parameter>
                <Parameter name="DaysActive" dataType="int">0</Parameter>
                <Parameter name="Recurring" dataType="bool">0</Parameter>
            """;

    public static final String PARAMETERS_SUFFIX = """
            </Parameters>
            """;

    public static final String REQUEST_SUFFIX = """
            </Request>""";

    private CommandBuilder() {
        // utility class
        // ToDo
    }

    public static String buildRequestConfiguration() {
        return XML_DECLARATION + REQUEST_CONFIGURATION + REQUEST_SUFFIX;
    }

    public static String buildGetAlarmList() {
        return XML_DECLARATION + GET_ALARMLIST + REQUEST_SUFFIX;
    }

    public static String buildGetTelemetry() {
        return XML_DECLARATION + GET_TELEMETRY + REQUEST_SUFFIX;
    }

    public static String buildGetUIFilterDiagnosticInfo(String bowID, String equipmentID) {
        return XML_DECLARATION + String.format(GET_UI_FILTER_DIAGNOSTIC_INFO, bowID, equipmentID) + PARAMETERS_SUFFIX
                + REQUEST_SUFFIX;
    }

    public static String buildRunGroupCmd(String groupID, String enable) {
        return XML_DECLARATION + String.format(RUN_GROUP_CMD, groupID, enable) + COMMAND_SCHEDULE + PARAMETERS_SUFFIX
                + REQUEST_SUFFIX;
    }

    public static String buildSetChlorEnableCmd(String bowID, String enable) {
        return XML_DECLARATION + String.format(SET_CHLOR_ENABLE, bowID, enable) + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    public static String buildSetChlorParamsCmd(String bowId, String chlorID, String cfgState, String opMode,
            String bowType, String cellType, String timedPercent, String scTimeout, String orpTimeout) {
        return XML_DECLARATION + String.format(SET_CHLOR_PARAMS, bowId, chlorID, cfgState, opMode, bowType, cellType,
                timedPercent, scTimeout, orpTimeout) + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    public static String buildSetEquipmentCmd(String bowID, String equipmentID, String isOn) {
        return XML_DECLARATION + String.format(SET_EQUIPMENT_CMD, bowID, equipmentID, isOn) + COMMAND_SCHEDULE
                + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    public static String buildSetHeaterEnableCmd(String bowID, String equipmentID, String enable) {
        return XML_DECLARATION + String.format(SET_HEATER_ENABLE, bowID, equipmentID, enable) + PARAMETERS_SUFFIX
                + REQUEST_SUFFIX;
    }

    public static String buildSetUIHeaterCmd(String bowID, String equipmentID, String temp) {
        return XML_DECLARATION + String.format(SET_UI_HEATER_CMD, bowID, equipmentID, temp) + PARAMETERS_SUFFIX
                + REQUEST_SUFFIX;
    }

    public static String buildSetUISolarSetPointCmd(String bowID, String equipmentID, String temp) {
        return XML_DECLARATION + String.format(SET_UI_SOLAR_SETPOINT_CMD, bowID, equipmentID, temp) + PARAMETERS_SUFFIX
                + REQUEST_SUFFIX;
    }

    // todo
    public static String buildSetUISpilloverCmd(String bowID, String equipmentID, String isOn) {
        return XML_DECLARATION + String.format(SET_UI_SPILLOVER_CMD, bowID, equipmentID, isOn) + COMMAND_SCHEDULE
                + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    public static String buildSetUISuperChlorCmd(String bowID, String equipmentID, String isOn) {
        return XML_DECLARATION + String.format(SET_UI_SUPER_CHLOR_CMD, bowID, equipmentID, isOn) + COMMAND_SCHEDULE
                + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    // todo
    public static String buildSetUISuperChlorTimeoutCmd(String bowID, String equipmentID, String isOn) {
        return XML_DECLARATION + String.format(SET_UI_SUPER_CHLOR_TIMEOUT_CMD, bowID, equipmentID, isOn)
                + COMMAND_SCHEDULE + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    public static String buildSetStandaloneLightShow(String bowID, String equipmentID, String show) {
        return XML_DECLARATION + String.format(SET_STANDALONE_LIGHTSHOW, bowID, equipmentID, show) + COMMAND_SCHEDULE
                + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }

    public static String buildSetStandaloneLightShowOmniDirect(String bowID, String equipmentID, String show,
            String speed, String brightness) {
        return XML_DECLARATION
                + String.format(SET_STANDALONE_LIGHTSHOW_OMNIDIRECT, bowID, equipmentID, show, speed, brightness)
                + COMMAND_SCHEDULE + PARAMETERS_SUFFIX + REQUEST_SUFFIX;
    }
}
