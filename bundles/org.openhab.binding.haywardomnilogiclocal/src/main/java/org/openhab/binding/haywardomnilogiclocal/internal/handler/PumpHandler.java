package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Pump;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PumpHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public PumpHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        switch (channelUID.getId()) {
            case "pumpEnable":
                /// sendUdpCommand(CommandBuilder.setEquipmentEnable(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, "ON".equalsIgnoreCase(command.toString())),
                // MessageType.SET_EQUIPMENT);
                break;
            case "pumpSpeed":
                int speedVal = ((Number) command).intValue();
                // sendUdpCommand(
                // CommandBuilder.setPumpSpeed(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, speedVal),
                // MessageType.SET_EQUIPMENT);
                break;
            default:
                break;
        }
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        updateIfPresent(values, "pumpEnable_" + sysId, "pumpEnable");
        updateIfPresent(values, "pumpSpeed_" + sysId, "pumpSpeed");
        putIfPresent(values, "pumpState_" + sysId, getThing().getProperties(), "pumpState");
        updateIfPresent(values, "pumpLastSpeed_" + sysId, "pumpLastSpeed");
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (Pump p : status.getPumps()) {
            if (sysId.equals(p.getSystemId())) {

                @Nullable
                String pumpSpeed = p.getPumpSpeed();
                if (pumpSpeed != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_SPEED, pumpSpeed);

                    if (Integer.parseInt(pumpSpeed) > 0) {
                        updateData(BindingConstants.CHANNEL_PUMP_ENABLE, "1");
                    } else {
                        updateData(BindingConstants.CHANNEL_PUMP_ENABLE, "0");
                    }

                } else {
                    logger.debug("Pump speed missing from Telemtry");
                }

                @Nullable
                String pumpState = p.getPumpState();
                if (pumpState != null) {
                    updateData(BindingConstants.CHANNEL_PUMP_STATE, pumpState);
                } else {
                    logger.debug("Pump state missing from Telemtry");
                }
            }
        }
    }
}
