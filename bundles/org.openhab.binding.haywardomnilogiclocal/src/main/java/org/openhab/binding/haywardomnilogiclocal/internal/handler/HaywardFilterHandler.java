package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.net.CommandBuilder;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Filter;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;

public class HaywardFilterHandler extends HaywardThingHandler {

    public HaywardFilterHandler(Thing thing) {
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
            case "filterEnable":
                sendUdpCommand(CommandBuilder.setEquipmentEnable(bridgehandler.getAccount().getToken(),
                        bridgehandler.getAccount().getMspSystemID(), sysId, "ON".equalsIgnoreCase(command.toString())),
                        HaywardMessageType.SET_FILTER_SPEED);
                break;
            case "filterSpeed":
                int speedVal = ((Number) command).intValue();
                sendUdpCommand(
                        CommandBuilder.setFilterSpeed(bridgehandler.getAccount().getToken(),
                                bridgehandler.getAccount().getMspSystemID(), sysId, speedVal),
                        HaywardMessageType.SET_FILTER_SPEED);
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

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }
        for (Filter f : status.getFilters()) {
            if (sysId.equals(f.getSystemId())) {
                @Nullable String filterState = f.getFilterState();
                if (filterState != null) {
                    updateData("filterEnable", filterState);
                    updateData("filterState", filterState);
                }

                @Nullable String filterSpeed = f.getFilterSpeed();
                if (filterSpeed != null) {
                    updateData("filterSpeed", filterSpeed);
                }

                @Nullable String lastSpeed = f.getLastSpeed();
                if (lastSpeed != null) {
                    updateData("filterLastSpeed", lastSpeed);
                }
            }
        }
    }
}
