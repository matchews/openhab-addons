package org.openhab.binding.haywardomnilogiclocal.internal.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ActionConfigConverter} behaviour.
 */
public class ActionConfigConverterTest {

    @Test
    public void testParsesActionFunctionAndDataNodes() {
        String xml = """
                <MSPConfig>
                  <Backyard>
                    <Pump systemId=\"P1\" name=\"Main\">
                      <Operation>PEO_SAMPLE
                        <Action>
                          <Action-Function>PEA_SAMPLE</Action-Function>
                          <Action-Data1>Value1</Action-Data1>
                          <Action-Data2>Value2</Action-Data2>
                        </Action>
                      </Operation>
                    </Pump>
                  </Backyard>
                </MSPConfig>
                """;

        MspConfig config = ConfigParser.parse(xml);

        OperationConfig operation = config.getBackyards().get(0).getPumps().get(0).getOperations().get(0);
        ActionConfig action = operation.getActions().get(0);

        assertEquals("PEA_SAMPLE", action.getActionFunction());
        assertEquals(List.of("Value1", "Value2"), action.getActionData());
        assertEquals("Value1", action.getActionDataValue(1));
        assertEquals("Value2", action.getActionDataValue(2));
    }

    @Test
    public void testFallsBackToLegacyTextualType() {
        String xml = """
                <MSPConfig>
                  <Backyard>
                    <Pump systemId=\"P1\" name=\"Main\">
                      <Operation>PEO_SAMPLE
                        <Action>PEA_LEGACY</Action>
                      </Operation>
                    </Pump>
                  </Backyard>
                </MSPConfig>
                """;

        MspConfig config = ConfigParser.parse(xml);

        OperationConfig operation = config.getBackyards().get(0).getPumps().get(0).getOperations().get(0);
        ActionConfig action = operation.getActions().get(0);

        assertEquals("PEA_LEGACY", action.getActionFunction());
        assertEquals("PEA_LEGACY", action.getType());
        assertTrue(action.getActionData().isEmpty());
    }
}
