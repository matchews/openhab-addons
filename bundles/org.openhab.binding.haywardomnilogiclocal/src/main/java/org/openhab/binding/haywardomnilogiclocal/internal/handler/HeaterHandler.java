package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
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
                String state = h.getState();
                if (state != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_STATE, state);
                } else {
                    logger.debug("Heater state missing from Telemtry");
                }

                @Nullable
                String heaterTemp = h.getTemp();
                if (heaterTemp != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_TEMP, heaterTemp);
                } else {
                    logger.debug("Heater temp missing from Telemtry");
                }

                @Nullable
                String heaterEnable = h.getEnable();
                if (heaterEnable != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_ENABLE, heaterEnable);
                } else {
                    logger.debug("Heater temp missing from Telemtry");
                }

                @Nullable
                String heaterPriority = h.getPriority();
                if (heaterPriority != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_PRIORITY, heaterPriority);
                } else {
                    logger.debug("Heater priority missing from Telemtry");
                }

                @Nullable
                String heaterMaintainFor = h.getMaintainFor();
                if (heaterMaintainFor != null) {
                    updateData(BindingConstants.CHANNEL_HEATER_MAINTAINFOR, heaterMaintainFor);
                } else {
                    logger.debug("Heater maintain for missing from Telemtry");
                }
            }
        }
    }
}
