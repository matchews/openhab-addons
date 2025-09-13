package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Heater;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.core.thing.Thing;

public class HaywardHeaterHandler extends HaywardThingHandler {

    public HaywardHeaterHandler(Thing thing) {
        super(thing);
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        updateIfPresent(values, "heaterEnable_" + sysId, "heaterEnable");
        putIfPresent(values, "heaterState_" + sysId, getThing().getProperties(), "heaterState");
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (Heater h : status.getHeaters()) {
            if (sysId.equals(h.getSystemId())) {
                updateData("heaterState", h.getHeaterState());
                String enable = "yes".equals(h.getEnable()) ? "1" : "0";
                updateData("heaterEnable", enable);
            }
        }
    }
}
