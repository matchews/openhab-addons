package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.ValveActuator;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValveActuatorHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public ValveActuatorHandler(Thing thing) {
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
        for (ValveActuator valve : status.getValveActuators()) {
            if (sysId.equals(valve.getSystemId())) {

                @Nullable
                String valveState = valve.getValveActuatorState();
                if (valveState != null) {
                    updateData(BindingConstants.CHANNEL_VALVEACTUATOR_STATE, valveState);
                } else {
                    logger.debug("Valve actuator state missing from Telemtry");
                }

                @Nullable
                String valveWhyOn = valve.getWhyOn();
                if (valveWhyOn != null) {
                    updateData(BindingConstants.CHANNEL_VALVEACTUATOR_WHYON, valveWhyOn);
                } else {
                    logger.debug("Valve actuator state missing from Telemtry");
                }
            }
        }
    }
}
