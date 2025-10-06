package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Heater;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaterHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public HeaterHandler(Thing thing) {
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
                @Nullable
                String heaterState = h.getHeaterState();
                if (heaterState != null) {
                    updateData("heaterState", heaterState);
                }
                @Nullable
                String temp = h.getTemp();
                if (temp != null) {
                    updateData("temp", temp);
                }
                @Nullable
                String enable = h.getEnable();
                if (enable != null) {
                    updateData("enable", enable);
                }
                @Nullable
                String priority = h.getPriority();
                if (priority != null) {
                    updateData("priority", priority);
                }
                @Nullable
                String maintainFor = h.getMaintainFor();
                if (maintainFor != null) {
                    updateData("maintainFor", maintainFor);
                }
            }
        }
    }
}
