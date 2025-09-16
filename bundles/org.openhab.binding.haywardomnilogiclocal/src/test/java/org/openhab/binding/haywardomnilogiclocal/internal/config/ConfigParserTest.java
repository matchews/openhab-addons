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
                "  <Backyard>" +
                "    <System-Id>BY</System-Id>" +
                "    <Name>Main Backyard</Name>" +
                "    <Service-Mode-Timeout>15</Service-Mode-Timeout>" +
                "    <BodyOfWater systemId='BOW' type='BOW_POOL' sharedType='BOW_SHARED_EQUIPMENT' supportsSpillover='yes'" +
                "        spilloverMode='manual' spilloverTimedPercent='50' freezeProtectEnabled='yes'" +
                "        freezeProtectSetPoint='38' sizeInLiters='56781'>" +
                "      <Name>Main Pool</Name>" +
                "      <Type>BOW_POOL</Type>" +
                "      <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>" +
                "      <Shared-Priority>SHARED_EQUIPMENT_HIGH_PRIORITY</Shared-Priority>" +
                "      <Shared-Equipment-System-ID>BOW2</Shared-Equipment-System-ID>" +
                "      <Use-Spillover-For-Filter-Operations>yes</Use-Spillover-For-Filter-Operations>" +
                "      <Spillover-Manual-Timeout>10</Spillover-Manual-Timeout>" +
                "      <Spillover-Timed-Timeout>20</Spillover-Timed-Timeout>" +
                "      <Freeze-Protect-Override>no</Freeze-Protect-Override>" +
                "      <Size-In-Gallons>15000</Size-In-Gallons>" +
                "      <Filter systemId='F1' pumpId='P1'/>" +
                "      <Heater systemId='H1' type='gas'/>" +
                "      <Chlorinator systemId='C1'/>" +
                "      <ColorLogic-Light systemId='L1'/>" +
                "      <Relay systemId='R1' name='Aux1'>" +
                "        <Type>RLY_HIGH_VOLTAGE_RELAY</Type>" +
                "        <Function>GENERIC</Function>" +
                "      </Relay>" +
                "      <Sensor>" +
                "        <System-Id>SEN1</System-Id>" +
                "        <Name>Water Sensor</Name>" +
                "        <Type>SENSOR_WATER_TEMP</Type>" +
                "        <Units>UNITS_FAHRENHEIT</Units>" +
                "      </Sensor>" +
                "    </BodyOfWater>" +
                "    <Pump systemId='P1' name='Main'/>" +
                "    <VirtualHeater systemId='VH1'/>" +
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
        assertEquals("Main Backyard", backyard.getName());
        assertEquals("15", backyard.getServiceModeTimeout());
        assertEquals(1, backyard.getBodiesOfWater().size());
        BodyOfWaterConfig bow = backyard.getBodiesOfWater().get(0);
        assertEquals("BOW", bow.getSystemId());
        assertEquals("Main Pool", bow.getName());
        assertEquals("BOW_POOL", bow.getType());
        assertEquals("BOW_SHARED_EQUIPMENT", bow.getSharedType());
        assertEquals("SHARED_EQUIPMENT_HIGH_PRIORITY", bow.getSharedPriority());
        assertEquals("BOW2", bow.getSharedEquipmentSystemId());
        assertEquals("yes", bow.getSupportsSpillover());
        assertEquals("yes", bow.getUseSpilloverForFilterOperations());
        assertEquals("manual", bow.getSpilloverMode());
        assertEquals("10", bow.getSpilloverManualTimeout());
        assertEquals("50", bow.getSpilloverTimedPercent());
        assertEquals("20", bow.getSpilloverTimedTimeout());
        assertEquals("yes", bow.getFreezeProtectEnabled());
        assertEquals("no", bow.getFreezeProtectOverride());
        assertEquals("38", bow.getFreezeProtectSetPoint());
        assertEquals("15000", bow.getSizeInGallons());
        assertEquals("56781", bow.getSizeInLiters());

        assertEquals(1, bow.getFilters().size());
        FilterConfig filter = bow.getFilters().get(0);
        assertEquals("F1", filter.getSystemId());
        assertEquals("P1", filter.getPumpId());

        assertEquals(1, bow.getHeaters().size());
        HeaterConfig heater = bow.getHeaters().get(0);
        assertEquals("H1", heater.getSystemId());
        assertEquals("gas", heater.getType());

        assertEquals(1, bow.getChlorinators().size());
        assertEquals("C1", bow.getChlorinators().get(0).getSystemId());

        assertEquals(1, bow.getColorLogicLights().size());
        assertEquals("L1", bow.getColorLogicLights().get(0).getSystemId());

        assertEquals(1, bow.getRelays().size());
        RelayConfig relay = bow.getRelays().get(0);
        assertEquals("R1", relay.getSystemId());
        assertEquals("Aux1", relay.getName());

        assertEquals(1, bow.getSensors().size());
        SensorConfig sensor = bow.getSensors().get(0);
        assertEquals("SEN1", sensor.getSystemId());
        assertEquals("Water Sensor", sensor.getName());
        assertEquals("SENSOR_WATER_TEMP", sensor.getType());
        assertEquals("UNITS_FAHRENHEIT", sensor.getUnits());

        assertEquals(1, backyard.getPumps().size());
        PumpConfig pump = backyard.getPumps().get(0);
        assertEquals("P1", pump.getSystemId());
        assertEquals("Main", pump.getName());

        assertEquals(1, backyard.getVirtualHeaters().size());
        assertEquals("VH1", backyard.getVirtualHeaters().get(0).getSystemId());

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

