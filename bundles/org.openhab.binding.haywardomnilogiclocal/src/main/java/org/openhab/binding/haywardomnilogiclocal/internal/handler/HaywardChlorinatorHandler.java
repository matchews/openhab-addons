package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Chlorinator;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
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
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof HaywardBridgeHandler bridgehandler)) {
            return;
        }

        switch (channelUID.getId()) {
            case "chlorEnable":
                sendUdpCommand(CommandBuilder.setEquipmentEnable(bridgehandler.getAccount().getToken(),
                        bridgehandler.getAccount().getMspSystemID(), sysId, "ON".equalsIgnoreCase(command.toString())),
                        HaywardMessageType.SET_CHLOR_ENABLED);
                break;
            case "chlorSaltOutput":
                int val = ((Number) command).intValue();
                sendUdpCommand(
                        CommandBuilder.setChlorinatorOutput(bridgehandler.getAccount().getToken(),
                                bridgehandler.getAccount().getMspSystemID(), sysId, val),
                        HaywardMessageType.SET_CHLOR_PARAMS);
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

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (Chlorinator c : status.getChlorinators()) {
            if (sysId.equals(c.getSystemId())) {
                updateData("chlorEnable", c.getOperatingState());
                updateData("chlorOperatingMode", c.getOperatingMode());
                updateData("chlorSaltOutput", c.getTimedPercent());
                updateData("chlorAvgSaltLevel", c.getAvgSaltLevel());
                updateData("chlorInstantSaltLevel", c.getInstantSaltLevel());
                updateData("chlorStatus", c.getStatus());
            }
        }
    }
}
