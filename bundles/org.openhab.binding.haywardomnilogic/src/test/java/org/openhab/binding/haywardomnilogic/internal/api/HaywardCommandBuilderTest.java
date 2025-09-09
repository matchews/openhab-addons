package org.openhab.binding.haywardomnilogic.internal.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogic.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogic.internal.api.HaywardCommandBuilder.HaywardCommand;

/**
 * Tests for {@link HaywardCommandBuilder}.
 */
public class HaywardCommandBuilderTest {

    @Test
    public void testSetUiEquipmentCmd() {
        String expected = HaywardBindingConstants.COMMAND_PARAMETERS
                + "<Name>SetUIEquipmentCmd</Name><Parameters>"
                + "<Parameter name=\"Token\" dataType=\"String\">t</Parameter>"
                + "<Parameter name=\"MspSystemID\" dataType=\"int\">1</Parameter>"
                + "<Parameter name=\"PoolID\" dataType=\"int\">2</Parameter>"
                + "<Parameter name=\"EquipmentID\" dataType=\"int\">3</Parameter>"
                + "<Parameter name=\"IsOn\" dataType=\"int\">4</Parameter>"
                + HaywardBindingConstants.COMMAND_SCHEDULE + "</Parameters></Request>";

        String actual = HaywardCommandBuilder.command(HaywardCommand.SET_UI_EQUIPMENT_CMD)
                .withToken("t").withMspSystemId("1").withPoolId("2").withEquipmentId("3")
                .withParameter("IsOn", "int", "4").includeSchedule().build();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetStandAloneLightShow() {
        String expected = HaywardBindingConstants.COMMAND_PARAMETERS
                + "<Name>SetStandAloneLightShow</Name><Parameters>"
                + "<Parameter name=\"Token\" dataType=\"String\">t</Parameter>"
                + "<Parameter name=\"MspSystemID\" dataType=\"int\">1</Parameter>"
                + "<Parameter name=\"PoolID\" dataType=\"int\">2</Parameter>"
                + "<Parameter name=\"LightID\" dataType=\"int\">3</Parameter>"
                + "<Parameter name=\"Show\" dataType=\"int\">4</Parameter>"
                + HaywardBindingConstants.COMMAND_SCHEDULE + "</Parameters></Request>";

        String actual = HaywardCommandBuilder.command(HaywardCommand.SET_STAND_ALONE_LIGHT_SHOW)
                .withToken("t").withMspSystemId("1").withPoolId("2").withLightId("3")
                .withParameter("Show", "int", "4").includeSchedule().build();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetStandAloneLightShowV2() {
        String expected = HaywardBindingConstants.COMMAND_PARAMETERS
                + "<Name>SetStandAloneLightShowV2</Name><Parameters>"
                + "<Parameter name=\"Token\" dataType=\"String\">t</Parameter>"
                + "<Parameter name=\"MspSystemID\" dataType=\"int\">1</Parameter>"
                + "<Parameter name=\"PoolID\" dataType=\"int\">2</Parameter>"
                + "<Parameter name=\"LightID\" dataType=\"int\">3</Parameter>"
                + "<Parameter name=\"Show\" dataType=\"int\">4</Parameter>"
                + "<Parameter name=\"Speed\" dataType=\"byte\">5</Parameter>"
                + "<Parameter name=\"Brightness\" dataType=\"byte\">6</Parameter>"
                + HaywardBindingConstants.COMMAND_SCHEDULE + "</Parameters></Request>";

        String actual = HaywardCommandBuilder.command(HaywardCommand.SET_STAND_ALONE_LIGHT_SHOW_V2)
                .withToken("t").withMspSystemId("1").withPoolId("2").withLightId("3")
                .withParameter("Show", "int", "4").withParameter("Speed", "byte", "5")
                .withParameter("Brightness", "byte", "6").includeSchedule().build();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetChlorParams() {
        String expected = HaywardBindingConstants.COMMAND_PARAMETERS
                + "<Name>SetCHLORParams</Name><Parameters>"
                + "<Parameter name=\"Token\" dataType=\"String\">t</Parameter>"
                + "<Parameter name=\"MspSystemID\" dataType=\"int\">1</Parameter>"
                + "<Parameter name=\"PoolID\" dataType=\"int\">2</Parameter>"
                + "<Parameter name=\"ChlorID\" dataType=\"int\" alias=\"EquipmentID\">3</Parameter>"
                + "<Parameter name=\"CfgState\" dataType=\"byte\" alias=\"Data1\">4</Parameter>"
                + "<Parameter name=\"OpMode\" dataType=\"byte\" alias=\"Data2\">1</Parameter>"
                + "<Parameter name=\"BOWType\" dataType=\"byte\" alias=\"Data3\">1</Parameter>"
                + "<Parameter name=\"CellType\" dataType=\"byte\" alias=\"Data4\">4</Parameter>"
                + "<Parameter name=\"TimedPercent\" dataType=\"byte\" alias=\"Data5\">5</Parameter>"
                + "<Parameter name=\"SCTimeout\" dataType=\"byte\" unit=\"hour\" alias=\"Data6\">24</Parameter>"
                + "<Parameter name=\"ORPTimout\" dataType=\"byte\" unit=\"hour\" alias=\"Data7\">24</Parameter>"
                + "</Parameters></Request>";

        String actual = HaywardCommandBuilder.command(HaywardCommand.SET_CHLOR_PARAMS)
                .withToken("t").withMspSystemId("1").withPoolId("2").withChlorId("3")
                .withParameter("CfgState", "byte", "4", "Data1")
                .withParameter("OpMode", "byte", "1", "Data2")
                .withParameter("BOWType", "byte", "1", "Data3")
                .withParameter("CellType", "byte", "4", "Data4")
                .withParameter("TimedPercent", "byte", "5", "Data5")
                .withParameter("SCTimeout", "byte", "24", "Data6", "hour")
                .withParameter("ORPTimout", "byte", "24", "Data7", "hour")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetHeaterEnable() {
        String expected = HaywardBindingConstants.COMMAND_PARAMETERS
                + "<Name>SetHeaterEnable</Name><Parameters>"
                + "<Parameter name=\"Token\" dataType=\"String\">t</Parameter>"
                + "<Parameter name=\"MspSystemID\" dataType=\"int\">1</Parameter>"
                + "<Parameter name=\"PoolID\" dataType=\"int\">2</Parameter>"
                + "<Parameter name=\"HeaterID\" dataType=\"int\">3</Parameter>"
                + "<Parameter name=\"Enabled\" dataType=\"bool\">true</Parameter>"
                + "</Parameters></Request>";

        String actual = HaywardCommandBuilder.command(HaywardCommand.SET_HEATER_ENABLE)
                .withToken("t").withMspSystemId("1").withPoolId("2").withHeaterId("3")
                .withParameter("Enabled", "bool", "true").build();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetUiHeaterCmd() {
        String expected = HaywardBindingConstants.COMMAND_PARAMETERS
                + "<Name>SetUIHeaterCmd</Name><Parameters>"
                + "<Parameter name=\"Token\" dataType=\"String\">t</Parameter>"
                + "<Parameter name=\"MspSystemID\" dataType=\"int\">1</Parameter>"
                + "<Parameter name=\"PoolID\" dataType=\"int\">2</Parameter>"
                + "<Parameter name=\"HeaterID\" dataType=\"int\">3</Parameter>"
                + "<Parameter name=\"Temp\" dataType=\"int\">40</Parameter>"
                + "</Parameters></Request>";

        String actual = HaywardCommandBuilder.command(HaywardCommand.SET_UI_HEATER_CMD)
                .withToken("t").withMspSystemId("1").withPoolId("2").withHeaterId("3")
                .withParameter("Temp", "int", "40").build();

        assertEquals(expected, actual);
    }
}

