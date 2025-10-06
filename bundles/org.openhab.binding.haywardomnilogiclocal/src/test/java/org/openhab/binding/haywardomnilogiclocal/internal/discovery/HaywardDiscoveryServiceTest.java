package org.openhab.binding.haywardomnilogiclocal.internal.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardTypeToRequest;
import org.openhab.core.thing.ThingTypeUID;

/**
 * Tests for {@link HaywardDiscoveryService#mspConfigDiscovery(String)}.
 */
@NonNullByDefault
public class HaywardDiscoveryServiceTest {

    private static class TestDiscoveryService extends HaywardDiscoveryService {
        final List<ThingTypeUID> types = new ArrayList<>();
        final List<Map<String, Object>> propertyMaps = new ArrayList<>();

        @Override
        public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
            types.add(thingType);
            propertyMaps.add(new HashMap<>(properties));
        }
    }

    @Test
    public void mspConfigDiscoveryCreatesResultsForAllDevices() {
        String xml = "" + "<MSPConfig>" + "  <System systemId='SYS'/>" + "  <Backyard systemId='BY'>"
                + "    <Sensor systemId='S2' name='Air' type='SENSOR_AIR_TEMP' units='UNITS_FAHRENHEIT'/>"
                + "    <BodyOfWater systemId='BOW1' name='Pool' type='BOW_POOL' sharedType='BOW_SHARED_EQUIPMENT'"
                + "        sharedPriority='SHARED_EQUIPMENT_HIGH_PRIORITY' sharedEquipmentSystemId='BOW2'"
                + "        supportsSpillover='yes' sizeInGallons='15000'>" + "      <Filter systemId='F1' pumpId='P1'/>"
                + "      <Heater systemId='H1' type='gas'/>" + "      <Chlorinator systemId='C1'/>"
                + "      <ColorLogic-Light systemId='L1'/>" + "      <Relay systemId='R1' name='Relay1'>"
                + "        <Type>RLY_HIGH_VOLTAGE_RELAY</Type>" + "      </Relay>"
                + "      <Sensor systemId='S1' name='Water' type='SENSOR_WATER_TEMP' units='UNITS_FAHRENHEIT'/>"
                + "    </BodyOfWater>" + "    <BodyOfWater systemId='BOW2'>"
                + "      <Filter systemId='F2' pumpId='P2'/>" + "    </BodyOfWater>"
                + "    <Pump systemId='P1' name='Pump1'/>" + "    <Pump systemId='P2' name='Pump2'/>"
                + "    <VirtualHeater systemId='VH1'/>" + "  </Backyard>" + "</MSPConfig>";

        TestDiscoveryService service = new TestDiscoveryService();
        service.mspConfigDiscovery(xml);

        assertEquals(14, service.types.size());
        assertEquals(2, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_PUMP)).count());
        assertEquals(2,
                service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_FILTER)).count());
        assertEquals(2, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_BOW)).count());
        assertEquals(2,
                service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_SENSOR)).count());

        Map<String, Object> bowProps = null;
        Map<String, Object> backyardSensorProps = null;
        for (int i = 0; i < service.types.size(); i++) {
            if (bowProps != null && backyardSensorProps != null) {
                break;
            }
            if (service.types.get(i).equals(HaywardBindingConstants.THING_TYPE_BOW)) {
                Map<String, Object> properties = service.propertyMaps.get(i);
                if ("BOW1".equals(properties.get(HaywardBindingConstants.PROPERTY_SYSTEM_ID))) {
                    bowProps = properties;
                }
            }
            if (service.types.get(i).equals(HaywardBindingConstants.THING_TYPE_SENSOR)) {
                Map<String, Object> properties = service.propertyMaps.get(i);
                if ("S2".equals(properties.get(HaywardBindingConstants.PROPERTY_SYSTEM_ID))) {
                    backyardSensorProps = properties;
                }
            }
        }

        assertNotNull(bowProps);
        assertEquals("Pool", bowProps.get(HaywardBindingConstants.PROPERTY_BOWNAME));
        assertEquals("BOW1", bowProps.get(HaywardBindingConstants.PROPERTY_BOWID));
        assertEquals("BOW_POOL", bowProps.get(HaywardBindingConstants.PROPERTY_BOW_TYPE));
        assertEquals("BOW_SHARED_EQUIPMENT", bowProps.get(HaywardBindingConstants.PROPERTY_BOW_SHAREDTYPE));
        assertEquals("SHARED_EQUIPMENT_HIGH_PRIORITY",
                bowProps.get(HaywardBindingConstants.PROPERTY_BOW_SHAREDPRIORITY));
        assertEquals("BOW2", bowProps.get(HaywardBindingConstants.PROPERTY_BOW_SHAREDEQUIPID));
        assertEquals("yes", bowProps.get(HaywardBindingConstants.PROPERTY_BOW_SUPPORTSSPILLOVER));
        assertEquals("15000", bowProps.get(HaywardBindingConstants.PROPERTY_BOW_SIZEINGALLONS));

        assertNotNull(backyardSensorProps);
        assertEquals(HaywardTypeToRequest.SENSOR, backyardSensorProps.get(HaywardBindingConstants.PROPERTY_TYPE));
        assertEquals("SENSOR_AIR_TEMP", backyardSensorProps.get(HaywardBindingConstants.PROPERTY_SENSOR_TYPE));
        assertEquals("UNITS_FAHRENHEIT", backyardSensorProps.get(HaywardBindingConstants.PROPERTY_SENSOR_UNITS));
        assertFalse(backyardSensorProps.containsKey(HaywardBindingConstants.PROPERTY_BOWID));
    }

    @Test
    public void mspConfigDiscoverySkipsMissingPumps() {
        String xml = "" + "<MSPConfig>" + "  <System systemId='SYS'/>" + "  <Backyard systemId='BY'>"
                + "    <Sensor systemId='S1' name='Air' type='SENSOR_AIR_TEMP' units='UNITS_FAHRENHEIT'/>"
                + "    <BodyOfWater systemId='BOW1' name='Pool'>" + "      <Filter systemId='F1' pumpId='P1'/>"
                + "    </BodyOfWater>" + "  </Backyard>" + "</MSPConfig>";

        TestDiscoveryService service = new TestDiscoveryService();
        service.mspConfigDiscovery(xml);

        assertEquals(0, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_PUMP)).count());
        assertEquals(1,
                service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_BACKYARD)).count());
        assertEquals(1, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_BOW)).count());
        assertEquals(1,
                service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_FILTER)).count());
        assertEquals(1,
                service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_SENSOR)).count());
    }

    @Test
    public void mspConfigDiscoveryIdentifiesValveActuatorRelays() {
        String xml = "" + "<MSPConfig>" + "  <System systemId='SYS'/>" + "  <Backyard systemId='BY'>"
                + "    <BodyOfWater systemId='BOW1'>" + "      <Relay systemId='VA1' name='Valve'>"
                + "        <Type>RLY_VALVE_ACTUATOR</Type>" + "        <Function>POOL_RETURN</Function>"
                + "      </Relay>" + "    </BodyOfWater>" + "  </Backyard>" + "</MSPConfig>";

        TestDiscoveryService service = new TestDiscoveryService();
        service.mspConfigDiscovery(xml);

        Map<String, Object> valveProps = null;
        for (int i = 0; i < service.types.size(); i++) {
            if (service.types.get(i).equals(HaywardBindingConstants.THING_TYPE_VALVEACTUATOR)) {
                valveProps = service.propertyMaps.get(i);
                break;
            }
        }

        assertNotNull(valveProps);
        assertEquals(1,
                service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_VALVEACTUATOR)).count());
        assertEquals(0, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_RELAY)).count());
        assertEquals(HaywardTypeToRequest.VALVEACTUATOR, valveProps.get(HaywardBindingConstants.PROPERTY_TYPE));
        assertEquals("RLY_VALVE_ACTUATOR", valveProps.get(HaywardBindingConstants.PROPERTY_RELAY_TYPE));
        assertEquals("POOL_RETURN", valveProps.get(HaywardBindingConstants.PROPERTY_RELAY_FUNCTION));
        assertEquals("VA1", valveProps.get(HaywardBindingConstants.PROPERTY_SYSTEM_ID));
        assertEquals("BOW1", valveProps.get(HaywardBindingConstants.PROPERTY_BOWID));
    }
}
