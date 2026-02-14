/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
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
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.StateDescriptionFragment;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.StateOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Colorlogic Light Handler
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class ColorLogicHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(ColorLogicHandler.class);
    private final Object aggLock = new Object();
    private final LightAggregate agg = new LightAggregate();

    private static String orDefault(@Nullable String v, String def) {
        return v != null ? v : def;
    }

    private static final String DEFAULT_SHOW = "0";
    private static final String DEFAULT_SPEED = "4";
    private static final String DEFAULT_BRIGHTNESS = "4";

    public ColorLogicHandler(Thing thing) {
        super(thing);
    }

    private static final class LightAggregate {
        @org.eclipse.jdt.annotation.Nullable
        String show;
        @org.eclipse.jdt.annotation.Nullable
        String speed;
        @org.eclipse.jdt.annotation.Nullable
        String brightness;
        @org.eclipse.jdt.annotation.Nullable
        String enabled;
        @org.eclipse.jdt.annotation.Nullable
        String specialEffect;

        LightAggregate copy() {
            LightAggregate c = new LightAggregate();
            c.show = this.show;
            c.speed = this.speed;
            c.brightness = this.brightness;
            c.enabled = this.enabled;
            c.specialEffect = this.specialEffect;
            return c;
        }
    }

    @Override
    public void initialize() {
        try {
            getProperties();
            setStateDescriptions();
            // Add brightness, speed and special effect channels for advanced lights
            String lightType = getThing().getProperties().get(BindingConstants.PROPERTY_COLORLOGIC_TYPE);
            if (lightType != null) {
                if ("COLOR_LOGIC_UCL".equals(lightType) || "COLOR_LOGIC_UCL_V2".equals(lightType)) {
                    addV2Channels();
                }
            }
            updateStatus(ThingStatus.ONLINE);
        } catch (HaywardException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Unable to set ColorLogixHandler StateDescriptions");
        }
    }

    protected void addV2Channels() {
        if (thing.getChannel(BindingConstants.CHANNEL_COLORLOGIC_BRIGHTNESS) == null) {
            ThingBuilder thingBuilder = editThing();
            ChannelUID uid = new ChannelUID(thing.getUID(), BindingConstants.CHANNEL_COLORLOGIC_BRIGHTNESS);
            ChannelBuilder chnBuilder = ChannelBuilder.create(uid, "String");
            chnBuilder.withType(
                    new ChannelTypeUID(BindingConstants.BINDING_ID, BindingConstants.TYPE_COLORLOGIC_LIGHTBRIGHTNESS));
            chnBuilder.withLabel("Brightness");
            chnBuilder.withDescription("Brightness");
            Channel channel = chnBuilder.build();
            thingBuilder.withChannel(channel);
            updateThing(thingBuilder.build());
        }

        if (thing.getChannel(BindingConstants.CHANNEL_COLORLOGIC_SPEED) == null) {
            ThingBuilder thingBuilder = editThing();
            ChannelUID uid = new ChannelUID(thing.getUID(), BindingConstants.CHANNEL_COLORLOGIC_SPEED);
            ChannelBuilder chnBuilder = ChannelBuilder.create(uid, "String");
            chnBuilder.withType(
                    new ChannelTypeUID(BindingConstants.BINDING_ID, BindingConstants.TYPE_COLORLOGIC_LIGHTSPEED));
            chnBuilder.withLabel("Speed");
            chnBuilder.withDescription("Speed");
            Channel channel = chnBuilder.build();
            thingBuilder.withChannel(channel);
            updateThing(thingBuilder.build());
        }

        if (thing.getChannel(BindingConstants.CHANNEL_COLORLOGIC_SPECIALEFFECT) == null) {
            ThingBuilder thingBuilder = editThing();
            ChannelUID uid = new ChannelUID(thing.getUID(), BindingConstants.CHANNEL_COLORLOGIC_SPECIALEFFECT);
            ChannelBuilder chnBuilder = ChannelBuilder.create(uid, "String");
            chnBuilder.withType(
                    new ChannelTypeUID(BindingConstants.BINDING_ID, BindingConstants.TYPE_COLORLOGIC_SPECIALEFFECT));
            chnBuilder.withLabel("Special Effect");
            chnBuilder.withDescription("Special Effect");
            Channel channel = chnBuilder.build();
            thingBuilder.withChannel(channel);
            updateThing(thingBuilder.build());
        }
    }

    @Override
    public void setStateDescriptions() throws HaywardException {
        List<StateOption> options = new ArrayList<>();
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            // Set Light Shows based on light type
            Channel ch = thing.getChannel(BindingConstants.CHANNEL_COLORLOGIC_CURRENTSHOW);
            if (ch != null) {
                String lightType = getThing().getProperties().get(BindingConstants.PROPERTY_COLORLOGIC_TYPE);
                if (lightType != null) {
                    if ("COLOR_LOGIC_2_5".equals(lightType) || "COLOR_LOGIC_4_0".equals(lightType)) {
                        options.add(new StateOption("0", "Voodoo Lounge"));
                        options.add(new StateOption("1", "Deep Blue Sea"));
                        options.add(new StateOption("2", "Afternoon Sky"));
                        options.add(new StateOption("3", "Emerald"));
                        options.add(new StateOption("4", "Sangria"));
                        options.add(new StateOption("5", "Cloud White"));
                        options.add(new StateOption("6", "Twilight"));
                        options.add(new StateOption("7", "Tranquility"));
                        options.add(new StateOption("8", "Gemstone"));
                        options.add(new StateOption("9", "USA"));
                        options.add(new StateOption("10", "Mardi Gras"));
                        options.add(new StateOption("11", "Cool Cabaret"));
                    } else if (lightType.contains("COLOR_LOGIC_UCL")) {
                        options.add(new StateOption("0", "Voodoo Lounge"));
                        options.add(new StateOption("1", "Deep Blue Sea"));
                        options.add(new StateOption("2", "Royal Blue"));
                        options.add(new StateOption("3", "Afternoon Sky"));
                        options.add(new StateOption("4", "Aqua Green"));
                        options.add(new StateOption("5", "Emerald"));
                        options.add(new StateOption("6", "Cloud White"));
                        options.add(new StateOption("7", "Warm Red"));
                        options.add(new StateOption("8", "Flamingo"));
                        options.add(new StateOption("9", "Vivid Violet"));
                        options.add(new StateOption("10", "Sangria"));
                        options.add(new StateOption("11", "Twilight"));
                        options.add(new StateOption("12", "Tranquility"));
                        options.add(new StateOption("13", "Gemstone"));
                        options.add(new StateOption("14", "USA"));
                        options.add(new StateOption("15", "Mardi Gras"));
                        options.add(new StateOption("16", "Cool Cabaret"));
                        if ("COLOR_LOGIC_UCL_V2".equals(lightType)) {
                            options.add(new StateOption("17", "Yellow"));
                            options.add(new StateOption("18", "Orange"));
                            options.add(new StateOption("19", "Gold"));
                            options.add(new StateOption("20", "Mint"));
                            options.add(new StateOption("21", "Teal"));
                            options.add(new StateOption("22", "Burnt Orange"));
                            options.add(new StateOption("23", "Pure White"));
                            options.add(new StateOption("24", "Crisp White"));
                            options.add(new StateOption("25", "Warm White"));
                            options.add(new StateOption("26", "Bright Yellow"));
                        }
                    } else if ("CL_P_COLOR".equals(lightType)) {
                        options.add(new StateOption("0", "Sam Show"));
                        options.add(new StateOption("1", "Party Show"));
                        options.add(new StateOption("2", "Romance Show"));
                        options.add(new StateOption("3", "Caribbean Show"));
                        options.add(new StateOption("4", "American Show"));
                        options.add(new StateOption("5", "Sunset Show"));
                        options.add(new StateOption("6", "Royal Show"));
                        options.add(new StateOption("7", "Blue"));
                        options.add(new StateOption("8", "Green"));
                        options.add(new StateOption("9", "Red"));
                        options.add(new StateOption("10", "White"));
                        options.add(new StateOption("11", "Magenta"));
                    } else if ("CL_Z_COLOR".equals(lightType)) {
                        options.add(new StateOption("0", "Alpine White"));
                        options.add(new StateOption("1", "Sky Blue"));
                        options.add(new StateOption("2", "Cobalt Blue"));
                        options.add(new StateOption("3", "Caribbean Blue"));
                        options.add(new StateOption("4", "Spring Green"));
                        options.add(new StateOption("5", "Emerald Green"));
                        options.add(new StateOption("6", "Emerald Rose"));
                        options.add(new StateOption("7", "Magenta"));
                        options.add(new StateOption("8", "Violet"));
                        options.add(new StateOption("9", "Slow Color Splash"));
                        options.add(new StateOption("10", "Fast Color Splash"));
                        options.add(new StateOption("11", "America The Beautiful"));
                        options.add(new StateOption("12", "Fat Tuesday"));
                        options.add(new StateOption("13", "Disco Tech"));
                    }
                    StateDescriptionFragment stateDescriptionFragment = StateDescriptionFragmentBuilder.create()
                            .withOptions(options).build();
                    bridgehandler.updateChannelStateDescriptionFragment(ch, stateDescriptionFragment);
                }
            }
        }
    }

    @Override
    public void getProperties() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler bridgeHandler = (BridgeHandler) bridge.getHandler();
            if (bridgeHandler != null && bridgeHandler.getMspConfig() != null) {
                String sysId = getThing().getUID().getId();
                if (bridgeHandler.getMspConfig().getDevice(sysId) != null) {
                    Object object = bridgeHandler.getMspConfig().getDevice(sysId);
                    if (object instanceof ColorLogicLightConfig) {
                        ColorLogicLightConfig colorLogicLight = (ColorLogicLightConfig) object;
                        Map<String, String> props = new HashMap<>();
                        putStrStrIfNotNull(props, BindingConstants.PROPERTY_COLORLOGIC_TYPE, colorLogicLight.getType());
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

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getUID().getId();

        for (ColorLogicLight cl : status.getColorLogicLights()) {
            if (sysId.equals(cl.getSystemId())) {
                @Nullable
                String lightState = cl.getlightState();
                @Nullable
                String currentShow = cl.getCurrentShow();
                @Nullable
                String speed = cl.getSpeed();
                @Nullable
                String brightness = cl.getBrightness();
                @Nullable
                String specialEffect = cl.getSpecialEffect();

                synchronized (aggLock) {
                    agg.enabled = lightState == null ? agg.enabled : ("0".equals(lightState) ? "0" : "1");
                    agg.show = currentShow != null ? currentShow : agg.show;
                    agg.speed = speed != null ? speed : agg.speed;
                    agg.brightness = brightness != null ? brightness : agg.brightness;
                    agg.specialEffect = specialEffect != null ? specialEffect : agg.specialEffect;
                }

                // Push channel states to Items (UI)
                if (lightState != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_ENABLE, "0".equals(lightState) ? "0" : "1");
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_STATE, lightState);
                } else {
                    logger.debug("Colorlogic light state missing from Telemtry");
                }
                if (currentShow != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_CURRENTSHOW, currentShow);
                } else {
                    logger.debug("Colorlogic light current show missing from Telemtry");
                }
                if (speed != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_SPEED, speed);
                } else {
                    logger.debug("Colorlogic light speed missing from Telemtry");
                }
                if (brightness != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_BRIGHTNESS, brightness);
                } else {
                    logger.debug("Colorlogic light brightness missing from Telemtry");
                }
                if (specialEffect != null) {
                    updateData(BindingConstants.CHANNEL_COLORLOGIC_SPECIALEFFECT, specialEffect);
                } else {
                    logger.debug("Colorlogic light special effect missing from Telemtry");
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }

        String sysID = getThing().getProperties().get(BindingConstants.PROPERTY_SYSTEM_ID);
        String bowID = getThing().getProperties().get(BindingConstants.PROPERTY_BOWID);
        Bridge bridge = getBridge();
        if (sysID == null || bowID == null || bridge == null) {
            return;
        }

        // Take a snapshot of current aggregate
        final LightAggregate snap;
        synchronized (aggLock) {
            snap = agg.copy();
        }

        String cmdURL;
        switch (channelUID.getId()) {
            case BindingConstants.CHANNEL_COLORLOGIC_ENABLE: {
                String on = (command == OnOffType.ON) ? "1" : "0";
                cmdURL = CommandBuilder.buildSetEquipmentCmd(bowID, sysID, on);
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                // optimistic cache update
                synchronized (aggLock) {
                    agg.enabled = on;
                }
                break;
            }
            case BindingConstants.CHANNEL_COLORLOGIC_CURRENTSHOW: {
                String newShow = cmdToString(command);
                String speed = orDefault(snap.speed, DEFAULT_SPEED);
                String brightness = orDefault(snap.brightness, DEFAULT_BRIGHTNESS);

                String lightType = getThing().getProperties().get(BindingConstants.PROPERTY_COLORLOGIC_TYPE);
                if (lightType != null && lightType.contains("UCL")) {
                    cmdURL = CommandBuilder.buildSetStandaloneLightShowOmniDirect(bowID, sysID, newShow, speed,
                            brightness);
                } else {
                    cmdURL = CommandBuilder.buildSetStandaloneLightShow(bowID, sysID, newShow);
                }
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);

                synchronized (aggLock) {
                    agg.show = newShow;
                }
                break;
            }
            case BindingConstants.CHANNEL_COLORLOGIC_SPEED: {
                String newSpeed = cmdToString(command);
                String show = orDefault(snap.show, DEFAULT_SHOW);
                String brightness = orDefault(snap.brightness, DEFAULT_BRIGHTNESS);

                String lightType = getThing().getProperties().get(BindingConstants.PROPERTY_COLORLOGIC_TYPE);
                if (lightType != null && lightType.contains("UCL")) {
                    cmdURL = CommandBuilder.buildSetStandaloneLightShowOmniDirect(bowID, sysID, show, newSpeed,
                            brightness);
                } else {
                    cmdURL = CommandBuilder.buildSetStandaloneLightShow(bowID, sysID, show);
                }
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);

                synchronized (aggLock) {
                    agg.speed = newSpeed;
                }
                break;
            }
            case BindingConstants.CHANNEL_COLORLOGIC_BRIGHTNESS: {
                String newBrightness = cmdToString(command);
                String show = orDefault(snap.show, DEFAULT_SHOW);
                String speed = orDefault(snap.speed, DEFAULT_SPEED);

                String lightType = getThing().getProperties().get(BindingConstants.PROPERTY_COLORLOGIC_TYPE);
                if (lightType != null && lightType.contains("UCL")) {
                    cmdURL = CommandBuilder.buildSetStandaloneLightShowOmniDirect(bowID, sysID, show, speed,
                            newBrightness);
                } else {
                    cmdURL = CommandBuilder.buildSetStandaloneLightShow(bowID, sysID, show);
                }
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);

                synchronized (aggLock) {
                    agg.brightness = newBrightness;
                }
                break;
            }
            // TODO
            // Special Effect
            // Flicker effect for solid color water bowl lights only.
            // Waiting on Linnette for command
            default:
                logger.warn("Unsupported channel {}", channelUID);
        }
    }
}
