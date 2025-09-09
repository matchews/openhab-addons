package org.openhab.binding.haywardomnilogiclocal.internal.net;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Helper for building XML command strings sent to the Hayward controller.
 */
@NonNullByDefault
public class CommandBuilder {

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

    private CommandBuilder() {
        // utility class
    }

    public static String buildSetHeaterEnable(String prefix, String token, int mspSystemID, String poolID,
            String heaterID, String enabled) {
        return prefix
                + String.format(SET_HEATER_ENABLE, token, mspSystemID, poolID, heaterID, enabled)
                + closingTag(prefix);
    }

    public static String buildSetUIHeaterCmd(String prefix, String token, int mspSystemID, String poolID,
            String heaterID, String temp) {
        return prefix + String.format(SET_UI_HEATER_CMD, token, mspSystemID, poolID, heaterID, temp)
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

