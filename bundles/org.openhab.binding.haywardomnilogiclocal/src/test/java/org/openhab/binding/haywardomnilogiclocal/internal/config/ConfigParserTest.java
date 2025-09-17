package org.openhab.binding.haywardomnilogiclocal.internal.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ConfigParser}.
 */
public class ConfigParserTest {
    @Test
    public void testParsePopulatesAllListsAndAttributes() throws IOException {
        MspConfig config = parseSampleConfiguration();
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
        assertEquals(1, backyard.getSensors().size());
        SensorConfig backyardSensor = backyard.getSensors().get(0);
        assertEquals("SENBY1", backyardSensor.getSystemId());
        assertEquals("Air Sensor", backyardSensor.getName());
        assertEquals("SENSOR_AIR_TEMP", backyardSensor.getType());
        assertEquals("UNITS_FAHRENHEIT", backyardSensor.getUnits());
        assertEquals(0, backyardSensor.getOperations().size());
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
        assertEquals("Filter Pump", filter.getName());
        assertEquals("BOW_SHARED_EQUIPMENT", filter.getSharedType());
        assertEquals("FMT_VARIABLE_SPEED_PUMP", filter.getFilterType());
        assertEquals("100", filter.getMaxPumpSpeed());
        assertEquals("18", filter.getMinPumpSpeed());
        assertEquals("3450", filter.getMaxPumpRpm());
        assertEquals("600", filter.getMinPumpRpm());
        assertEquals("18", filter.getVspLowPumpSpeed());
        assertEquals("50", filter.getVspMediumPumpSpeed());
        assertEquals("100", filter.getVspHighPumpSpeed());
        assertEquals("80", filter.getVspCustomPumpSpeed());
        assertEquals(1, filter.getOperations().size());
        OperationConfig filterOperation = filter.getOperations().get(0);
        assertEquals("PEO_FILTER_SAMPLE", filterOperation.getType());
        assertEquals(1, filterOperation.getActions().size());
        ActionConfig filterAction = filterOperation.getActions().get(0);
        assertEquals("PEA_FILTER_SPEED", filterAction.getType());
        assertEquals(0, filterAction.getDevices().size());
        assertEquals(1, filterAction.getParameters().size());
        assertEquals("Speed", filterAction.getParameters().get(0).getName());
        assertEquals("3200", filterAction.getParameters().get(0).getValue());

        assertEquals(1, bow.getHeaters().size());
        HeaterConfig heater = bow.getHeaters().get(0);
        assertEquals("H1", heater.getSystemId());
        assertEquals("gas", heater.getType());
        assertEquals("BOW_SHARED_EQUIPMENT", heater.getSharedType());
        assertEquals("yes", heater.getEnabled());
        assertEquals("80", heater.getCurrentSetPoint());
        assertEquals("104", heater.getMaxWaterTemp());
        assertEquals("65", heater.getMinSettableWaterTemp());
        assertEquals("104", heater.getMaxSettableWaterTemp());
        assertEquals("no", heater.getCooldownEnabled());
        assertEquals(2, heater.getOperations().size());
        OperationConfig heaterEquipmentOp = heater.getOperations().get(0);
        assertEquals("PEO_HEATER_EQUIPMENT", heaterEquipmentOp.getType());
        assertEquals(0, heaterEquipmentOp.getActions().size());
        assertEquals(1, heaterEquipmentOp.getHeaterEquipment().size());
        HeaterConfig.HeaterEquipmentConfig heaterEquipment = heaterEquipmentOp.getHeaterEquipment().get(0);
        assertEquals("HX1", heaterEquipment.getSystemId());
        assertEquals("Gas Heater", heaterEquipment.getName());
        assertEquals("PET_HEATER", heaterEquipment.getType());
        assertEquals("HTR_GAS", heaterEquipment.getHeaterType());
        assertEquals("yes", heaterEquipment.getEnabled());
        OperationConfig heaterFlowOp = heater.getOperations().get(1);
        assertEquals("PEO_HEATER_FLOW", heaterFlowOp.getType());
        assertEquals(1, heaterFlowOp.getActions().size());
        ActionConfig heaterFlowAction = heaterFlowOp.getActions().get(0);
        assertEquals("PEA_FLOW", heaterFlowAction.getType());
        assertEquals(0, heaterFlowAction.getDevices().size());
        assertEquals(1, heaterFlowAction.getParameters().size());
        assertEquals("Flow", heaterFlowAction.getParameters().get(0).getName());
        assertEquals("40", heaterFlowAction.getParameters().get(0).getValue());

        assertEquals(1, bow.getChlorinators().size());
        ChlorinatorConfig chlorinator = bow.getChlorinators().get(0);
        assertEquals("C1", chlorinator.getSystemId());
        assertEquals("Chlorinator", chlorinator.getName());
        assertEquals("BOW_SHARED_EQUIPMENT", chlorinator.getSharedType());
        assertEquals("yes", chlorinator.getEnabled());
        assertEquals("CHLOR_OP_MODE_ORP_AUTO", chlorinator.getMode());
        assertEquals("50", chlorinator.getTimedPercent());
        assertEquals("24", chlorinator.getSuperChlorTimeout());
        assertEquals("CELL_TYPE_T15", chlorinator.getCellType());
        assertEquals("86400", chlorinator.getOrpTimeout());
        assertEquals(1, chlorinator.getOperations().size());
        OperationConfig chlorinatorOp = chlorinator.getOperations().get(0);
        assertEquals("PEO_CHLOR_SAMPLE", chlorinatorOp.getType());
        assertEquals(1, chlorinatorOp.getActions().size());
        ActionConfig chlorinatorAction = chlorinatorOp.getActions().get(0);
        assertEquals("PEA_SET_PERCENT", chlorinatorAction.getType());
        assertEquals(0, chlorinatorAction.getDevices().size());
        assertEquals(1, chlorinatorAction.getParameters().size());
        assertEquals("Percent", chlorinatorAction.getParameters().get(0).getName());
        assertEquals("60", chlorinatorAction.getParameters().get(0).getValue());

        assertEquals(1, bow.getColorLogicLights().size());
        ColorLogicLightConfig light = bow.getColorLogicLights().get(0);
        assertEquals("L1", light.getSystemId());
        assertEquals("Pool Light", light.getName());
        assertEquals("COLOR_LOGIC_UCL", light.getType());

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
        assertEquals(1, sensor.getOperations().size());
        OperationConfig sensorOperation = sensor.getOperations().get(0);
        assertEquals("PEO_SENSOR_SAMPLE", sensorOperation.getType());
        assertEquals(1, sensorOperation.getParameters().size());
        ParameterConfig sensorOperationParameter = sensorOperation.getParameters().get(0);
        assertEquals("SampleRate", sensorOperationParameter.getName());
        assertEquals("5", sensorOperationParameter.getValue());
        assertEquals(1, sensorOperation.getActions().size());
        ActionConfig sensorAction = sensorOperation.getActions().get(0);
        assertEquals("PEA_SENSOR_REPORT", sensorAction.getType());
        assertEquals(0, sensorAction.getDevices().size());
        assertEquals(1, sensorAction.getParameters().size());
        assertEquals("Interval", sensorAction.getParameters().get(0).getName());
        assertEquals("15", sensorAction.getParameters().get(0).getValue());

        assertEquals(1, backyard.getPumps().size());
        PumpConfig pump = backyard.getPumps().get(0);
        assertEquals("P1", pump.getSystemId());
        assertEquals("Main", pump.getName());
        assertEquals("PMP_VARIABLE_SPEED_PUMP", pump.getType());
        assertEquals("PMP_FILTER", pump.getFunction());
        assertEquals("3450", pump.getMaxPumpRpm());
        assertEquals("600", pump.getMinPumpRpm());
        assertEquals("20", pump.getMinPumpSpeed());
        assertEquals("100", pump.getMaxPumpSpeed());
        assertEquals("60", pump.getVspMediumPumpSpeed());
        assertEquals("70", pump.getVspCustomPumpSpeed());
        assertEquals("100", pump.getVspHighPumpSpeed());
        assertEquals("30", pump.getVspLowPumpSpeed());
        assertEquals(1, pump.getOperations().size());
        OperationConfig pumpOperation = pump.getOperations().get(0);
        assertEquals("PEO_PUMP_SAMPLE", pumpOperation.getType());
        assertEquals(1, pumpOperation.getActions().size());
        ActionConfig pumpAction = pumpOperation.getActions().get(0);
        assertEquals("PEA_SET_SPEED", pumpAction.getType());
        assertEquals(0, pumpAction.getDevices().size());
        assertEquals(1, pumpAction.getParameters().size());
        assertEquals("Speed", pumpAction.getParameters().get(0).getName());
        assertEquals("3100", pumpAction.getParameters().get(0).getValue());
        assertEquals(1, pumpAction.getOperations().size());
        OperationConfig pumpActionNestedOperation = pumpAction.getOperations().get(0);
        assertEquals("PEO_ACTION_CHILD", pumpActionNestedOperation.getType());
        assertEquals(1, pumpActionNestedOperation.getParameters().size());
        assertEquals("ChildParam", pumpActionNestedOperation.getParameters().get(0).getName());
        assertEquals("1", pumpActionNestedOperation.getParameters().get(0).getValue());
        assertEquals(1, pumpOperation.getOperations().size());
        OperationConfig nestedPumpOperation = pumpOperation.getOperations().get(0);
        assertEquals("PEO_PUMP_CHILD", nestedPumpOperation.getType());
        assertEquals(1, nestedPumpOperation.getActions().size());
        ActionConfig nestedPumpAction = nestedPumpOperation.getActions().get(0);
        assertEquals("PEA_CHILD_SPEED", nestedPumpAction.getType());
        assertEquals(1, nestedPumpAction.getParameters().size());
        assertEquals("Speed", nestedPumpAction.getParameters().get(0).getName());
        assertEquals("2800", nestedPumpAction.getParameters().get(0).getValue());

        assertEquals(1, backyard.getVirtualHeaters().size());
        VirtualHeaterConfig virtualHeater = backyard.getVirtualHeaters().get(0);
        assertEquals("VH1", virtualHeater.getSystemId());
        assertEquals("Spa Heat", virtualHeater.getName());
        assertEquals("yes", virtualHeater.getEnable());
        assertEquals("90", virtualHeater.getCurrentSetPoint());

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
        assertEquals(1, action.getParameters().size());
        ParameterConfig scheduleActionParameter = action.getParameters().get(0);
        assertEquals("Duration", scheduleActionParameter.getName());
        assertEquals("30", scheduleActionParameter.getValue());

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

    @Test
    public void testParseSampleConfigurationNestedActionOperations() throws IOException {
        MspConfig config = parseSampleConfiguration();

        PumpConfig pump = config.getBackyards().get(0).getPumps().get(0);
        OperationConfig pumpOperation = pump.getOperations().get(0);
        ActionConfig pumpAction = pumpOperation.getActions().get(0);

        assertEquals(1, pumpAction.getOperations().size());
        OperationConfig nestedOperation = pumpAction.getOperations().get(0);
        assertEquals("PEO_ACTION_CHILD", nestedOperation.getType());
        assertEquals("ChildParam", nestedOperation.getParameters().get(0).getName());
        assertEquals("1", nestedOperation.getParameters().get(0).getValue());
    }

    @Test
    public void testParseSchedulesWithScheElements() throws IOException {
        MspConfig config = parseConfigurationResource("request-configuration-msp-sample.xml");

        assertEquals(2, config.getSchedules().size());

        ScheduleConfig firstSchedule = config.getSchedules().get(0);
        assertEquals("SCH1", firstSchedule.getSystemId());
        assertEquals("Filter Schedule", firstSchedule.getName());
        assertEquals("equipment", firstSchedule.getType());
        assertEquals("pump", firstSchedule.getSubType());
        assertEquals("yes", firstSchedule.getEnabled());
        assertEquals(1, firstSchedule.getDevices().size());
        assertEquals("P1", firstSchedule.getDevices().get(0).getSystemId());
        assertEquals(1, firstSchedule.getParameters().size());
        assertEquals("StartTimeHours", firstSchedule.getParameters().get(0).getName());
        assertEquals("6", firstSchedule.getParameters().get(0).getValue());
        assertEquals(1, firstSchedule.getActions().size());
        ScheduleActionConfig firstAction = firstSchedule.getActions().get(0);
        assertEquals("on", firstAction.getType());
        assertEquals(1, firstAction.getParameters().size());
        assertEquals("Duration", firstAction.getParameters().get(0).getName());
        assertEquals("30", firstAction.getParameters().get(0).getValue());

        ScheduleConfig secondSchedule = config.getSchedules().get(1);
        assertEquals("SCH2", secondSchedule.getSystemId());
        assertEquals("Chlorinator", secondSchedule.getName());
        assertEquals("equipment", secondSchedule.getType());
        assertEquals("chlorinator", secondSchedule.getSubType());
        assertEquals("no", secondSchedule.getEnabled());
        assertEquals(0, secondSchedule.getDevices().size());
        assertEquals(1, secondSchedule.getParameters().size());
        assertEquals("StartTimeHours", secondSchedule.getParameters().get(0).getName());
        assertEquals("2", secondSchedule.getParameters().get(0).getValue());
        assertEquals(0, secondSchedule.getActions().size());
    }

    private static MspConfig parseSampleConfiguration() throws IOException {
        return parseConfigurationResource("request-configuration-sample.xml");
    }

    private static MspConfig parseConfigurationResource(String resourceName) throws IOException {
        try (InputStream stream = ConfigParserTest.class.getResourceAsStream(resourceName)) {
            assertNotNull(stream, "Sample RequestConfiguration XML is missing: " + resourceName);
            String xml = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return ConfigParser.parse(xml);
        }
    }
}

