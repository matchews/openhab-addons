package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.BindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.MessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.config.RelayConfig;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Relay;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public RelayHandler(Thing thing) {
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
                        if (object instanceof RelayConfig) {
                            RelayConfig relay = (RelayConfig) object;
                            Map<String, String> props = new HashMap<>();
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_RELAY_FUNCTION, relay.getFunction());
                            putStrStrIfNotNull(props, BindingConstants.PROPERTY_RELAY_TYPE, relay.getType());
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
        for (Relay relay : status.getRelays()) {
            if (sysId.equals(relay.getSystemId())) {

                @Nullable
                String relayState = relay.getRelayState();
                if (relayState != null) {
                    updateData(BindingConstants.CHANNEL_RELAY_STATE, relayState);
                } else {
                    logger.debug("Relay state missing from Telemtry");
                }

                @Nullable
                String whyOn = relay.getWhyOn();
                if (whyOn != null) {
                    updateData(BindingConstants.CHANNEL_RELAY_WHYON, whyOn);
                } else {
                    logger.debug("Relay why on missing from Telemtry");
                }
            }
        }
    }

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
            case BindingConstants.CHANNEL_RELAY_STATE:
                if (command == OnOffType.ON) {
                    cmdString = "1";
                } else if (command == OnOffType.OFF) {
                    cmdString = "0";
                }
                cmdURL = CommandBuilder.buildSetEquipmentCommand(bowId, sysId, cmdString);
                sendUdpCommand(cmdURL, MessageType.SET_EQUIPMENT_CMD);
                break;

            default:
                break;
        }
    }
}
