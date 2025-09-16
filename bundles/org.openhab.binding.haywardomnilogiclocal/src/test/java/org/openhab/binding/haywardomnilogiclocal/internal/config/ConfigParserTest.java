package org.openhab.binding.haywardomnilogiclocal.internal.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ConfigParser}.
 */
public class ConfigParserTest {
    @Test
    public void testParsePopulatesAllListsAndAttributes() {
        String xml = "" +
                "<MSPConfig>" +
                "  <System systemId='SYS'>" +
                "    <Msp-Vsp-Speed-Format>Percent</Msp-Vsp-Speed-Format>" +
                "    <Msp-Time-Format>12 Hour Format</Msp-Time-Format>" +
                "    <Time-Zone>America/New_York</Time-Zone>" +
                "    <DST>Enabled</DST>" +
                "    <Internet-Time>Disabled</Internet-Time>" +
                "    <Units>Metric</Units>" +
                "    <Msp-Chlor-Display>Salt</Msp-Chlor-Display>" +
                "    <Msp-Language>French</Msp-Language>" +
                "    <UI-Show-Backyard>true</UI-Show-Backyard>" +
                "    <UI-Show-Equipment>false</UI-Show-Equipment>" +
                "    <UI-Show-Heaters>true</UI-Show-Heaters>" +
                "    <UI-Show-Lights>true</UI-Show-Lights>" +
                "    <UI-Show-Spillover>false</UI-Show-Spillover>" +
                "    <UI-Show-SuperChlor>true</UI-Show-SuperChlor>" +
                "    <UI-Show-SuperChlorTimeout>false</UI-Show-SuperChlorTimeout>" +
                "  </System>" +
                "  <Backyard systemId='BY'>" +
                "    <BodyOfWater systemId='BOW'/>" +
                "    <Pump systemId='P1' name='Main'/>" +
                "    <Filter systemId='F1' pumpId='P1'/>" +
                "    <Heater systemId='H1' type='gas'/>" +
                "    <VirtualHeater systemId='VH1'/>" +
                "    <Chlorinator systemId='C1'/>" +
                "    <ColorLogic-Light systemId='L1'/>" +
                "    <Relay systemId='R1' name='Aux1'/>" +
                "  </Backyard>" +
                "  <Schedules>" +
                "    <Schedule systemId='SCH1' name='Filter' type='equipment'>" +
                "      <Device systemId='P1' name='Main Pump' type='Pump'/>" +
                "      <Parameter name='StartTimeHours' dataType='int'>6</Parameter>" +
                "      <Action type='on'>" +
                "        <Device systemId='R1' name='Aux1' type='Relay'/>" +
                "      </Action>" +
                "    </Schedule>" +
                "  </Schedules>" +
                "  <DMT>" +
                "    <Device systemId='DEV1' name='Filter Pump' type='Pump'>" +
                "      <Parameter name='Speed' dataType='int'>3450</Parameter>" +
                "    </Device>" +
                "  </DMT>" +
                "  <CHECKSUM>12345</CHECKSUM>" +
                "</MSPConfig>";

        MspConfig config = ConfigParser.parse(xml);
        assertEquals(1, config.getSystems().size());

        SystemConfig system = config.getSystems().get(0);
        assertEquals("SYS", system.getSystemId());
        assertEquals("Percent", system.getMspVspSpeedFormat());
        assertEquals("12 Hour Format", system.getMspTimeFormat());
        assertEquals("America/New_York", system.getTimeZone());
        assertEquals("Enabled", system.getDst());
        assertEquals("Disabled", system.getInternetTime());
        assertEquals("Metric", system.getUnits());
        assertEquals("Salt", system.getMspChlorDisplay());
        assertEquals("French", system.getMspLanguage());
        assertEquals("true", system.getUiShowBackyard());
        assertEquals("false", system.getUiShowEquipment());
        assertEquals("true", system.getUiShowHeaters());
        assertEquals("true", system.getUiShowLights());
        assertEquals("false", system.getUiShowSpillover());
        assertEquals("true", system.getUiShowSuperChlor());
        assertEquals("false", system.getUiShowSuperChlorTimeout());

        assertEquals(1, config.getBackyards().size());

        BackyardConfig backyard = config.getBackyards().get(0);
        assertEquals("BY", backyard.getSystemId());
        assertEquals(1, backyard.getBodiesOfWater().size());
        assertEquals("BOW", backyard.getBodiesOfWater().get(0).getSystemId());

        assertEquals(1, backyard.getPumps().size());
        PumpConfig pump = backyard.getPumps().get(0);
        assertEquals("P1", pump.getSystemId());
        assertEquals("Main", pump.getName());

        assertEquals(1, backyard.getFilters().size());
        FilterConfig filter = backyard.getFilters().get(0);
        assertEquals("F1", filter.getSystemId());
        assertEquals("P1", filter.getPumpId());

        assertEquals(1, backyard.getHeaters().size());
        HeaterConfig heater = backyard.getHeaters().get(0);
        assertEquals("H1", heater.getSystemId());
        assertEquals("gas", heater.getType());

        assertEquals(1, backyard.getVirtualHeaters().size());
        assertEquals("VH1", backyard.getVirtualHeaters().get(0).getSystemId());

        assertEquals(1, backyard.getChlorinators().size());
        assertEquals("C1", backyard.getChlorinators().get(0).getSystemId());

        assertEquals(1, backyard.getColorLogicLights().size());
        assertEquals("L1", backyard.getColorLogicLights().get(0).getSystemId());

        assertEquals(1, backyard.getRelays().size());
        RelayConfig relay = backyard.getRelays().get(0);
        assertEquals("R1", relay.getSystemId());
        assertEquals("Aux1", relay.getName());

        assertEquals(1, config.getSchedules().size());
        ScheduleConfig schedule = config.getSchedules().get(0);
        assertEquals("SCH1", schedule.getSystemId());
        assertEquals("Filter", schedule.getName());
        assertEquals(1, schedule.getDevices().size());
        DeviceConfig scheduleDevice = schedule.getDevices().get(0);
        assertEquals("P1", scheduleDevice.getSystemId());
        assertEquals("Main Pump", scheduleDevice.getName());
        assertEquals(1, schedule.getParameters().size());
        ParameterConfig parameter = schedule.getParameters().get(0);
        assertEquals("StartTimeHours", parameter.getName());
        assertEquals("6", parameter.getValue());
        assertEquals(1, schedule.getActions().size());
        ScheduleActionConfig action = schedule.getActions().get(0);
        assertEquals("on", action.getType());
        assertEquals(1, action.getDevices().size());
        assertEquals("R1", action.getDevices().get(0).getSystemId());

        assertEquals(1, config.getDmt().getDevices().size());
        DeviceConfig device = config.getDmt().getDevices().get(0);
        assertEquals("DEV1", device.getSystemId());
        assertEquals("Filter Pump", device.getName());
        assertEquals("Pump", device.getType());
        assertEquals(1, device.getParameters().size());
        assertEquals("Speed", device.getParameters().get(0).getName());
        assertEquals("3450", device.getParameters().get(0).getValue());

        assertEquals("12345", config.getChecksum());
    }
}

