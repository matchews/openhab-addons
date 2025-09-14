package org.openhab.binding.haywardomnilogiclocal.internal.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.core.thing.ThingTypeUID;

/**
 * Tests for {@link HaywardDiscoveryService#mspConfigDiscovery(String)}.
 */
@NonNullByDefault
public class HaywardDiscoveryServiceTest {

    private static class TestDiscoveryService extends HaywardDiscoveryService {
        final List<ThingTypeUID> types = new ArrayList<>();

        @Override
        public void onDeviceDiscovered(ThingTypeUID thingType, String label, Map<String, Object> properties) {
            types.add(thingType);
        }
    }

    @Test
    public void mspConfigDiscoveryCreatesResultsForAllDevices() {
        String xml = "" +
                "<MspConfig>" +
                "  <System systemId='SYS'>" +
                "    <Backyard systemId='BY'>" +
                "      <BodyOfWater systemId='BOW1'/>" +
                "      <BodyOfWater systemId='BOW2'/>" +
                "      <Pump systemId='P1' name='Pump1'/>" +
                "      <Pump systemId='P2' name='Pump2'/>" +
                "      <Filter systemId='F1' pumpId='P1'/>" +
                "      <Filter systemId='F2' pumpId='P2'/>" +
                "      <Heater systemId='H1' type='gas'/>" +
                "      <Chlorinator systemId='C1'/>" +
                "      <ColorLogic-Light systemId='L1'/>" +
                "      <Relay systemId='R1' name='Relay1'/>" +
                "      <VirtualHeater systemId='VH1'/>" +
                "    </Backyard>" +
                "  </System>" +
                "</MspConfig>";

        TestDiscoveryService service = new TestDiscoveryService();
        service.mspConfigDiscovery(xml);

        assertEquals(12, service.types.size());
        assertEquals(2, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_PUMP)).count());
        assertEquals(2, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_FILTER)).count());
        assertEquals(2, service.types.stream().filter(t -> t.equals(HaywardBindingConstants.THING_TYPE_BOW)).count());
    }
}
