package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;

public class OmniLogicLocalFilterHandler extends OmniLogicLocalThingHandler {

    public OmniLogicLocalFilterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        switch (channelUID.getId()) {
            case "filterEnable":
                sendUdpCommand(CommandBuilder.setEquipmentEnable(sysId, "ON".equalsIgnoreCase(command.toString())));
                break;
            case "filterSpeed":
                int speedVal = ((Number) command).intValue();
                sendUdpCommand(CommandBuilder.setFilterSpeed(sysId, speedVal));
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

        updateIfPresent(values, "filterEnable_" + sysId, "filterEnable");
        updateIfPresent(values, "filterSpeed_" + sysId, "filterSpeed");
        putIfPresent(values, "filterState_" + sysId, getThing().getProperties(), "filterState");
        updateIfPresent(values, "filterLastSpeed_" + sysId, "filterLastSpeed");
    }
}
