package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.Thing;

public class OmniLogicLocalRelayHandler extends OmniLogicLocalThingHandler {

    public OmniLogicLocalRelayHandler(Thing thing) {
        super(thing);
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        updateIfPresent(values, "relayState_" + sysId, "relayState");
    }
}
