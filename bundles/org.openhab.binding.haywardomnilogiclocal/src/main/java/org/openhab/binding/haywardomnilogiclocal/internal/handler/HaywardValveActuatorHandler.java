package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.ValveActuator;
import org.openhab.core.thing.Thing;

public class HaywardValveActuatorHandler extends HaywardThingHandler {

    public HaywardValveActuatorHandler(Thing thing) {
        super(thing);
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        updateIfPresent(values, "valveActuatorState_" + sysId, "valveActuatorState");
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (ValveActuator valveActuator : status.getValveActuators()) {
            if (sysId.equals(valveActuator.getSystemId())) {
                @Nullable String valveActuatorState = valveActuator.getValveActuatorState();
                if (valveActuatorState != null) {
                    updateData("valveActuatorState", valveActuatorState);
                }
            }
        }
    }
}
