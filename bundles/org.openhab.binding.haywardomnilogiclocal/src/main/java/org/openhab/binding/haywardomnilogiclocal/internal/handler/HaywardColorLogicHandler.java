package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;

public class HaywardColorLogicHandler extends HaywardThingHandler {

    public HaywardColorLogicHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof HaywardBridgeHandler bridgehandler)) {
            return;
        }

        if ("colorMode".equals(channelUID.getId())) {
            sendUdpCommand(CommandBuilder.setColorMode(bridgehandler.getAccount().getToken(),
                    bridgehandler.getAccount().getMspSystemID(), sysId, command.toString()));
        } else if ("brightness".equals(channelUID.getId())) {
            int val = ((Number) command).intValue();
            sendUdpCommand(CommandBuilder.setBrightness(bridgehandler.getAccount().getToken(),
                    bridgehandler.getAccount().getMspSystemID(), sysId, val));
        }
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        putIfPresent(values, "colorMode_" + sysId, getThing().getProperties(), "colorMode");
        updateIfPresent(values, "brightness_" + sysId, "brightness");
    }
}
