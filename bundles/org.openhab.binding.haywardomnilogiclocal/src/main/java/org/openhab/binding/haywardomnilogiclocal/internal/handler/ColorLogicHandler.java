package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.ColorLogicLight;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorLogicHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public ColorLogicHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        if ("colorMode".equals(channelUID.getId())) {
            // sendUdpCommand(
            // CommandBuilder.setColorMode(bridgehandler.getAccount().getToken(),
            // bridgehandler.getAccount().getMspSystemID(), sysId, command.toString()),
            // MessageType.SET_CHLOR_ENABLED);
        } else if ("brightness".equals(channelUID.getId())) {
            int val = ((Number) command).intValue();
            // sendUdpCommand(
            // CommandBuilder.setBrightness(bridgehandler.getAccount().getToken(),
            // bridgehandler.getAccount().getMspSystemID(), sysId, val),
            // MessageType.SET_CHLOR_ENABLED);
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

                @Nullable
                String lightState = cl.getlightState();
                if (lightState != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_ENABLE, lightState);
                } else {
                    logger.debug("Colorlogic light state missing from Telemtry");
                }

                @Nullable
                String currentShow = cl.getCurrentShow();
                if (currentShow != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_CURRENTSHOW, currentShow);
                } else {
                    logger.debug("Colorlogic light current show missing from Telemtry");
                }

                @Nullable
                String speed = cl.getSpeed();
                if (speed != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_SPEED, speed);
                } else {
                    logger.debug("Colorlogic light speed missing from Telemtry");
                }

                @Nullable
                String brightness = cl.getBrightness();
                if (brightness != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_BRIGHTNESS, brightness);
                } else {
                    logger.debug("Colorlogic light brightness missing from Telemtry");
                }

                @Nullable
                String specialEffect = cl.getSpecialEffect();
                if (speed != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_SPECIALEFFECT, specialEffect);
                } else {
                    logger.debug("Colorlogic light special effect missing from Telemtry");
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }
}
