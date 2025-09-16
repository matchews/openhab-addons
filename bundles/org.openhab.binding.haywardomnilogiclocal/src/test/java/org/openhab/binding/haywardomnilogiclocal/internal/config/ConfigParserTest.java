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
                "      <Filter systemId='F1' pumpId='P1'>" +
                "        <Name>Filter Pump</Name>" +
                "        <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>" +
                "        <Filter-Type>FMT_VARIABLE_SPEED_PUMP</Filter-Type>" +
                "        <Max-Pump-Speed>100</Max-Pump-Speed>" +
                "        <Min-Pump-Speed>18</Min-Pump-Speed>" +
                "        <Max-Pump-RPM>3450</Max-Pump-RPM>" +
                "        <Min-Pump-RPM>600</Min-Pump-RPM>" +
                "        <Vsp-Low-Pump-Speed>18</Vsp-Low-Pump-Speed>" +
                "        <Vsp-Medium-Pump-Speed>50</Vsp-Medium-Pump-Speed>" +
                "        <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>" +
                "        <Vsp-Custom-Pump-Speed>80</Vsp-Custom-Pump-Speed>" +
                "        <Operation>PEO_FILTER_SAMPLE" +
                "          <Action>PEA_FILTER_SPEED" +
                "            <Parameter name='Speed' dataType='int'>3200</Parameter>" +
                "          </Action>" +
                "        </Operation>" +
                "      </Filter>" +
                "      <Heater systemId='H1' type='gas' sharedType='BOW_SHARED_EQUIPMENT' enabled='yes' currentSetPoint='80'>" +
                "        <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>" +
                "        <Enabled>yes</Enabled>" +
                "        <Current-Set-Point>82</Current-Set-Point>" +
                "        <Max-Water-Temp>104</Max-Water-Temp>" +
                "        <Min-Settable-Water-Temp>65</Min-Settable-Water-Temp>" +
                "        <Max-Settable-Water-Temp>104</Max-Settable-Water-Temp>" +
                "        <Cooldown-Enabled>no</Cooldown-Enabled>" +
                "        <Operation>PEO_HEATER_EQUIPMENT" +
                "          <Heater-Equipment>" +
                "            <System-Id>HX1</System-Id>" +
                "            <Name>Gas Heater</Name>" +
                "            <Type>PET_HEATER</Type>" +
                "            <Heater-Type>HTR_GAS</Heater-Type>" +
                "            <Enabled>yes</Enabled>" +
                "          </Heater-Equipment>" +
                "        </Operation>" +
                "        <Operation>PEO_HEATER_FLOW" +
                "          <Action>PEA_FLOW" +
                "            <Parameter name='Flow' dataType='int'>40</Parameter>" +
                "          </Action>" +
                "        </Operation>" +
                "      </Heater>" +
                "      <Chlorinator systemId='C1' sharedType='BOW_SHARED_EQUIPMENT' enabled='yes'>" +
                "        <Name>Chlorinator</Name>" +
                "        <Mode>CHLOR_OP_MODE_ORP_AUTO</Mode>" +
                "        <Timed-Percent>50</Timed-Percent>" +
                "        <SuperChlor-Timeout>24</SuperChlor-Timeout>" +
                "        <Cell-Type>CELL_TYPE_T15</Cell-Type>" +
                "        <ORP-Timeout>86400</ORP-Timeout>" +
                "        <Operation>PEO_CHLOR_SAMPLE" +
                "          <Action>PEA_SET_PERCENT" +
                "            <Parameter name='Percent' dataType='int'>60</Parameter>" +
                "          </Action>" +
                "        </Operation>" +
                "      </Chlorinator>" +
                "      <ColorLogic-Light systemId='L1'>" +
                "        <Name>Pool Light</Name>" +
                "        <Type>COLOR_LOGIC_UCL</Type>" +
                "      </ColorLogic-Light>" +
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
                "    <Pump systemId='P1' name='Main'>" +
                "      <Type>PMP_VARIABLE_SPEED_PUMP</Type>" +
                "      <Function>PMP_FILTER</Function>" +
                "      <Max-Pump-RPM>3450</Max-Pump-RPM>" +
                "      <Min-Pump-RPM>600</Min-Pump-RPM>" +
                "      <Min-Pump-Speed>20</Min-Pump-Speed>" +
                "      <Max-Pump-Speed>100</Max-Pump-Speed>" +
                "      <Vsp-Medium-Pump-Speed>60</Vsp-Medium-Pump-Speed>" +
                "      <Vsp-Custom-Pump-Speed>70</Vsp-Custom-Pump-Speed>" +
                "      <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>" +
                "      <Vsp-Low-Pump-Speed>30</Vsp-Low-Pump-Speed>" +
                "      <Operation>PEO_PUMP_SAMPLE" +
                "        <Action>PEA_SET_SPEED" +
                "          <Parameter name='Speed' dataType='int'>3100</Parameter>" +
                "        </Action>" +
                "      </Operation>" +
                "    </Pump>" +
                "    <VirtualHeater systemId='VH1' name='Spa Heat' enable='yes' currentSetPoint='90'>" +
                "      <Enable>yes</Enable>" +
                "      <Current-Set-Point>92</Current-Set-Point>" +
                "    </VirtualHeater>" +
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
        FilterConfig.OperationConfig filterOperation = filter.getOperations().get(0);
        assertEquals("PEO_FILTER_SAMPLE", filterOperation.getType());
        assertEquals(1, filterOperation.getActions().size());
        FilterConfig.ActionConfig filterAction = filterOperation.getActions().get(0);
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
        HeaterConfig.OperationConfig heaterEquipmentOp = heater.getOperations().get(0);
        assertEquals("PEO_HEATER_EQUIPMENT", heaterEquipmentOp.getType());
        assertEquals(0, heaterEquipmentOp.getActions().size());
        assertEquals(1, heaterEquipmentOp.getHeaterEquipment().size());
        HeaterConfig.HeaterEquipmentConfig heaterEquipment = heaterEquipmentOp.getHeaterEquipment().get(0);
        assertEquals("HX1", heaterEquipment.getSystemId());
        assertEquals("Gas Heater", heaterEquipment.getName());
        assertEquals("PET_HEATER", heaterEquipment.getType());
        assertEquals("HTR_GAS", heaterEquipment.getHeaterType());
        assertEquals("yes", heaterEquipment.getEnabled());
        HeaterConfig.OperationConfig heaterFlowOp = heater.getOperations().get(1);
        assertEquals("PEO_HEATER_FLOW", heaterFlowOp.getType());
        assertEquals(1, heaterFlowOp.getActions().size());
        HeaterConfig.ActionConfig heaterFlowAction = heaterFlowOp.getActions().get(0);
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
        ChlorinatorConfig.OperationConfig chlorinatorOp = chlorinator.getOperations().get(0);
        assertEquals("PEO_CHLOR_SAMPLE", chlorinatorOp.getType());
        assertEquals(1, chlorinatorOp.getActions().size());
        ChlorinatorConfig.ActionConfig chlorinatorAction = chlorinatorOp.getActions().get(0);
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
        PumpConfig.OperationConfig pumpOperation = pump.getOperations().get(0);
        assertEquals("PEO_PUMP_SAMPLE", pumpOperation.getType());
        assertEquals(1, pumpOperation.getActions().size());
        PumpConfig.ActionConfig pumpAction = pumpOperation.getActions().get(0);
        assertEquals("PEA_SET_SPEED", pumpAction.getType());
        assertEquals(0, pumpAction.getDevices().size());
        assertEquals(1, pumpAction.getParameters().size());
        assertEquals("Speed", pumpAction.getParameters().get(0).getName());
        assertEquals("3100", pumpAction.getParameters().get(0).getValue());

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

