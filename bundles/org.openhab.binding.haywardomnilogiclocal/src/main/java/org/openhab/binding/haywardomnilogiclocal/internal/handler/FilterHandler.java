package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Filter;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public FilterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String sysId = getThing().getProperties().get("systemID");
        Bridge bridge = getBridge();
        if (sysId == null || bridge == null || !(bridge.getHandler() instanceof BridgeHandler bridgehandler)) {
            return;
        }

        switch (channelUID.getId()) {
            case "filterEnable":
                // sendUdpCommand(CommandBuilder.setEquipmentEnable(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, "ON".equalsIgnoreCase(command.toString())),
                // HaywardMessageType.SET_FILTER_SPEED);
                break;
            case "filterSpeed":
                int speedVal = ((Number) command).intValue();
                // sendUdpCommand(
                // CommandBuilder.setFilterSpeed(bridgehandler.getAccount().getToken(),
                // bridgehandler.getAccount().getMspSystemID(), sysId, speedVal),
                // HaywardMessageType.SET_FILTER_SPEED);
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

                @Nullable
                String state = f.getState();
                if (state != null) {
                    if (Integer.parseInt(state) > 0) {
                        updateData(HaywardBindingConstants.CHANNEL_FILTER_ENABLE, "1");
                    } else {
                        updateData(HaywardBindingConstants.CHANNEL_FILTER_ENABLE, "0");
                    }
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_STATE, state);
                } else {
                    logger.debug("Filter state missing from Telemtry");
                }

                @Nullable
                String valvePosition = f.getValvePosition();
                if (valvePosition != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_VALVEPOSITION, valvePosition);
                } else {
                    logger.debug("Filter valve position missing from Telemtry");
                }

                @Nullable
                String speed = f.getSpeed();
                if (speed != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_SPEED, speed);
                } else {
                    logger.debug("Filter speed missing from Telemtry");
                }

                @Nullable
                String lastSpeed = f.getLastSpeed();
                if (lastSpeed != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_LASTSPEED, lastSpeed);
                } else {
                    logger.debug("Filter last speed missing from Telemtry");
                }

                @Nullable
                String whyFilterIsOn = f.getWhyFilterIsOn();
                if (whyFilterIsOn != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_WHYFILTERISON, whyFilterIsOn);
                } else {
                    logger.debug("Filter why filter is on missing from Telemtry");
                }

                @Nullable
                String fpOverride = f.getFpOverride();
                if (fpOverride != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_FPOVERRIDE, fpOverride);
                } else {
                    logger.debug("Filter fpOverride missing from Telemtry");
                }

                @Nullable
                String reportedSpeed = f.getReportedSpeed();
                if (reportedSpeed != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_REPORTEDSPEED, reportedSpeed);
                } else {
                    logger.debug("Filter reported speed missing from Telemtry");
                }

                @Nullable
                String power = f.getPower();
                if (power != null) {
                    updateData(HaywardBindingConstants.CHANNEL_FILTER_POWER, power);
                } else {
                    logger.debug("Filter power missing from Telemtry");
                }
            }
        }
    }
}
