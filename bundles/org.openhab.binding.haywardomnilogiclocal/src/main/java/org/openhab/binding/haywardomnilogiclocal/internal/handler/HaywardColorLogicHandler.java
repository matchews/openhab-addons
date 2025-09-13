package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.ColorLogicLight;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
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
            sendUdpCommand(
                    CommandBuilder.setColorMode(bridgehandler.getAccount().getToken(),
                            bridgehandler.getAccount().getMspSystemID(), sysId, command.toString()),
                    HaywardMessageType.SET_CHLOR_ENABLED);
        } else if ("brightness".equals(channelUID.getId())) {
            int val = ((Number) command).intValue();
            sendUdpCommand(
                    CommandBuilder.setBrightness(bridgehandler.getAccount().getToken(),
                            bridgehandler.getAccount().getMspSystemID(), sysId, val),
                    HaywardMessageType.SET_CHLOR_ENABLED);
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

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (ColorLogicLight cl : status.getColorLogicLights()) {
            if (sysId.equals(cl.getSystemId())) {
                @Nullable String currentShow = cl.getCurrentShow();
                if (currentShow != null) {
                    updateData("colorMode", currentShow);
                }
                @Nullable String brightness = cl.getBrightness();
                if (brightness != null) {
                    updateData("brightness", brightness);
                }
            }
        }
    }
}
