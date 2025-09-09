package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;

public class HaywardPumpHandler extends HaywardThingHandler {

    public HaywardPumpHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        switch (channelUID.getId()) {
            case "pumpEnable":
                sendUdpCommand(CommandBuilder.setEquipmentEnable(sysId, "ON".equalsIgnoreCase(command.toString())));
                break;
            case "pumpSpeed":
                int speedVal = ((Number) command).intValue();
                sendUdpCommand(CommandBuilder.setPumpSpeed(sysId, speedVal));
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
}
