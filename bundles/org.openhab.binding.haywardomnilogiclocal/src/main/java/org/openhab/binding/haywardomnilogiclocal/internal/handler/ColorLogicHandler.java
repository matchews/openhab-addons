package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.config.ColorLogicLightConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.ColorLogicLight;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorLogicHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public ColorLogicHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void getProperties() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
            if (bridgeHandler != null && bridgeHandler.getMspConfig() != null) {
                String sysId = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
                if (sysId != null) {
                    if (bridgeHandler.getMspConfig().getDevice(sysId) != null) {
                        Object object = bridgeHandler.getMspConfig().getDevice(sysId);
                        if (object instanceof ColorLogicLightConfig) {
                            ColorLogicLightConfig colorLogicLight = (ColorLogicLightConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_COLORLOGIC_TYPE,
                                    colorLogicLight.getType());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_COLORLOGIC_NODEID,
                                    colorLogicLight.getNodeId());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_COLORLOGIC_NETWORKED,
                                    colorLogicLight.getNetworked());
                            updateProperties(props);
                        }
                    }
                }
            }
        }
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
                    if (lightState.equals("0")) {
                        updateData(BindingConstants.CHANNEL_COLORLOGIC_ENABLE, "0");
                    } else {
                        updateData(BindingConstants.CHANNEL_COLORLOGIC_ENABLE, "1");
                    }
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_STATE, lightState);
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

    /*
     *
     * @Override
     * public void handleCommand(ChannelUID channelUID, Command command) {
     * String sysId = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
     * String bowId = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);
     *
     * Bridge bridge = getBridge();
     * if (sysId == null || bowId == null || bridge == null
     * || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
     * return;
     * }
     *
     *
     * public static final String CHANNEL_COLORLOGIC_ENABLE = "enable";
     * public static final String CHANNEL_COLORLOGIC_CURRENTSHOW = "currentShow";
     * public static final String CHANNEL_COLORLOGIC_BRIGHTNESS = "brightness";
     * public static final String CHANNEL_COLORLOGIC_SPEED = "speed";
     * public static final String CHANNEL_COLORLOGIC_SPECIALEFFECT = "specialEffect";
     *
     *
     * if (BindingConstants.CHANNEL_COLORLOGIC_CURRENTSHOW.equals(channelUID.getId())) {
     * // sendUdpCommand(CommandBuilder.setStandaloneLightShow(bowId, sysId, command.toString()),
     * // MessageType.SET_STANDALONE_LIGHT_SHOW);
     *
     * String xmlStr =
     * "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Name>SetStandAloneLightShow</Name><Parameters><Parameter name=\"PoolID\" dataType=\"int\">30</Parameter><Parameter name=\"LightID\" dataType=\"int\">38</Parameter><Parameter name=\"Show\" dataType=\"int\">2</Parameter><Parameter name=\"IsCountDownTimer\" dataType=\"bool\">false</Parameter><Parameter name=\"StartTimeHours\" dataType=\"int\">0</Parameter><Parameter name=\"StartTimeMinutes\" dataType=\"int\">0</Parameter><Parameter name=\"EndTimeHours\" dataType=\"int\">0</Parameter><Parameter name=\"EndTimeMinutes\" dataType=\"int\">0</Parameter><Parameter name=\"DaysActive\" dataType=\"int\">0</Parameter><Parameter name=\"Recurring\" dataType=\"bool\">false</Parameter></Parameters></Request>"
     * ;
     * sendUdpCommand(xmlStr, MessageType.SET_STANDALONE_LIGHT_SHOW);
     * }
     *
     * if ("colorMode".equals(channelUID.getId())) {
     * // sendUdpCommand(
     * // CommandBuilder.setColorMode(bridgehandler.getAccount().getToken(),
     * // bridgehandler.getAccount().getMspSystemID(), sysId, command.toString()),
     * // MessageType.SET_CHLOR_ENABLED);
     * } else if ("brightness".equals(channelUID.getId())) {
     * int val = ((Number) command).intValue();
     * // sendUdpCommand(
     * // CommandBuilder.setBrightness(bridgehandler.getAccount().getToken(),
     * // bridgehandler.getAccount().getMspSystemID(), sysId, val),
     * // MessageType.SET_CHLOR_ENABLED);
     * }
     * }
     */

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof RefreshType)) {
            return;
        }
        String sysId = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
        String bowId = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);

        Bridge bridge = getBridge();
        if (sysId == null || bowId == null || bridge == null) {
            return;
        }

        String cmdURL;
        String cmdString = "0";
        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_COLORLOGIC_ENABLE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetEquipmentCommand(bowId, sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                break;

            case BindingConstants.CHANNEL_COLORLOGIC_CURRENTSHOW:
                cmdURL = CommandBuilder.buildSetStandaloneLightShow(bowId, sysId, this.cmdToString(command));
                sendUdpCommand(cmdURL, MessageType.SET_STANDALONE_LIGHT_SHOW);
                break;

            // TODO
            case BindingConstants.CHANNEL_COLORLOGIC_BRIGHTNESS:
                break;

            // TODO
            case BindingConstants.CHANNEL_COLORLOGIC_SPEED:
                break;

            // TODO
            case BindingConstants.CHANNEL_COLORLOGIC_SPECIALEFFECT:
                break;

            default:
                logger.warn("haywardCommand Unsupported type {}", channelUID);
                return;
        }
    }
}
