package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.Thing;

public class OmniLogicLocalBackyardHandler extends OmniLogicLocalThingHandler {

    public OmniLogicLocalBackyardHandler(Thing thing) {
        super(thing);
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        updateIfPresent(values, "backyardAirTemp_" + sysId, "backyardAirTemp");
        putIfPresent(values, "backyardStatus_" + sysId, getThing().getProperties(), "backyardStatus");
        putIfPresent(values, "backyardState_" + sysId, getThing().getProperties(), "backyardState");
        updateIfPresent(values, "backyardAlarm1_" + sysId, "backyardAlarm1");
    }
}
