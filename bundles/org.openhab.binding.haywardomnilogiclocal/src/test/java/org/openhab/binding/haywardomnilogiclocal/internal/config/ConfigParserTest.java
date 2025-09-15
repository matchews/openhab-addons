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
                "  <System systemId='SYS'/>" +
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
                "</MSPConfig>";

        MspConfig config = ConfigParser.parse(xml);
        assertEquals(1, config.getSystems().size());

        SystemConfig system = config.getSystems().get(0);
        assertEquals("SYS", system.getSystemId());

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
    }
}

