package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;

public class HaywardChlorinatorHandler extends HaywardThingHandler {

    public HaywardChlorinatorHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        switch (channelUID.getId()) {
            case "chlorEnable":
                sendUdpCommand(CommandBuilder.setEquipmentEnable(sysId, "ON".equalsIgnoreCase(command.toString())));
                break;
            case "chlorSaltOutput":
                int val = ((Number) command).intValue();
                sendUdpCommand(CommandBuilder.setChlorinatorOutput(sysId, val));
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

        updateIfPresent(values, "chlorSaltOutput_" + sysId, "chlorSaltOutput");
        updateIfPresent(values, "chlorAvgSaltLevel_" + sysId, "chlorAvgSaltLevel");
        updateIfPresent(values, "chlorInstantSaltLevel_" + sysId, "chlorInstantSaltLevel");
        putIfPresent(values, "chlorStatus_" + sysId, getThing().getProperties(), "chlorStatus");
    }
}
