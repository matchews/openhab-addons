package org.openhab.binding.haywardomnilogiclocal.internal.net;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;

/**
 * Helper for building XML command strings sent to the Hayward controller.
 */
@NonNullByDefault
public class CommandBuilder {

    private static final String GET_TELEMETRY = """
            <Name>SetHeaterEnable</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"PoolID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"HeaterID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Enabled\" dataType=\"bool\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_HEATER_ENABLE = """
            <Name>SetHeaterEnable</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"PoolID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"HeaterID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Enabled\" dataType=\"bool\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_UI_HEATER_CMD = """
            <Name>SetUIHeaterCmd</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"PoolID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"HeaterID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Temp\" dataType=\"int\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_EQUIPMENT_ENABLE = """
            <Name>SetEquipmentEnable</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"EquipmentID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Enabled\" dataType=\"bool\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_PUMP_SPEED = """
            <Name>SetPumpSpeed</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"EquipmentID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Speed\" dataType=\"int\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_COLOR_MODE = """
            <Name>SetColorMode</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"EquipmentID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"ColorMode\" dataType=\"String\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_BRIGHTNESS = """
            <Name>SetBrightness</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"EquipmentID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Brightness\" dataType=\"int\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_FILTER_SPEED = """
            <Name>SetFilterSpeed</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"EquipmentID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Speed\" dataType=\"int\">%s</Parameter>
            </Parameters>
            """;

    private static final String SET_CHLORINATOR_OUTPUT = """
            <Name>SetChlorinatorOutput</Name>
            <Parameters>
                <Parameter name=\"Token\" dataType=\"String\">%s</Parameter>
                <Parameter name=\"MspSystemID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"EquipmentID\" dataType=\"int\">%s</Parameter>
                <Parameter name=\"Output\" dataType=\"int\">%s</Parameter>
            </Parameters>
            """;

    private CommandBuilder() {
        // utility class
    }

    // todo
    public static String buildRequestConfiguration(String prefix, String token, int mspSystemID, String poolID,
            String heaterID, String enabled) {
        return prefix + String.format(SET_HEATER_ENABLE, token, mspSystemID, poolID, heaterID, enabled)
                + closingTag(prefix);
    }

    public static String buildSetHeaterEnable(String prefix, String token, int mspSystemID, String poolID,
            String heaterID, String enabled) {
        return prefix + String.format(SET_HEATER_ENABLE, token, mspSystemID, poolID, heaterID, enabled)
                + closingTag(prefix);
    }

    public static String buildSetUIHeaterCmd(String prefix, String token, int mspSystemID, String poolID,
            String heaterID, String temp) {
        return prefix + String.format(SET_UI_HEATER_CMD, token, mspSystemID, poolID, heaterID, temp)
                + closingTag(prefix);
    }

    public static String setEquipmentEnable(String token, String mspSystemID, String equipmentID, boolean enabled) {
        String prefix = BindingConstants.COMMAND_PARAMETERS;
        return prefix + String.format(SET_EQUIPMENT_ENABLE, token, mspSystemID, equipmentID, enabled ? "True" : "False")
                + closingTag(prefix);
    }

    public static String setPumpSpeed(String token, String mspSystemID, String equipmentID, int speed) {
        String prefix = BindingConstants.COMMAND_PARAMETERS;
        return prefix + String.format(SET_PUMP_SPEED, token, mspSystemID, equipmentID, speed) + closingTag(prefix);
    }

    public static String setColorMode(String token, String mspSystemID, String equipmentID, String mode) {
        String prefix = BindingConstants.COMMAND_PARAMETERS;
        return prefix + String.format(SET_COLOR_MODE, token, mspSystemID, equipmentID, mode) + closingTag(prefix);
    }

    public static String setBrightness(String token, String mspSystemID, String equipmentID, int brightness) {
        String prefix = BindingConstants.COMMAND_PARAMETERS;
        return prefix + String.format(SET_BRIGHTNESS, token, mspSystemID, equipmentID, brightness) + closingTag(prefix);
    }

    public static String setFilterSpeed(String token, String mspSystemID, String equipmentID, int speed) {
        String prefix = BindingConstants.COMMAND_PARAMETERS;
        return prefix + String.format(SET_FILTER_SPEED, token, mspSystemID, equipmentID, speed) + closingTag(prefix);
    }

    public static String setChlorinatorOutput(String token, String mspSystemID, String equipmentID, int output) {
        String prefix = BindingConstants.COMMAND_PARAMETERS;
        return prefix + String.format(SET_CHLORINATOR_OUTPUT, token, mspSystemID, equipmentID, output)
                + closingTag(prefix);
    }

    private static String closingTag(String prefix) {
        if (prefix.contains("<Request>")) {
            return "</Request>";
        } else if (prefix.contains("<GetTelemetry>")) {
            return "</GetTelemetry>";
        } else {
            return "";
        }
    }
}
