package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Backyard;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.Status;
import org.openhab.binding.haywardomnilogiclocal.internal.telemetry.TelemetryParser;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class BackyardHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BackyardHandler.class);

    public BackyardHandler(Thing thing) {
        super(thing);
    }

    public void updateFromConfig(Map<String, ParameterValue> values) {
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        updateIfPresent(values, "backyardAirTemp_" + sysId, "backyardAirTemp");
        putIfPresent(values, "backyardStatus_" + sysId, getThing().getProperties(), "backyardStatus");
        putIfPresent(values, "backyardState_" + sysId, getThing().getProperties(), "backyardState");
        updateIfPresent(values, "backyardAlarm1_" + sysId, "backyardAlarm1");
    }

    @Override
    public void getTelemetry(String xmlResponse) throws HaywardException {
        Status status = TelemetryParser.parse(xmlResponse);
        String sysId = getThing().getProperties().get("systemID");
        if (sysId == null) {
            return;
        }

        for (Backyard by : status.getBackyards()) {
            if (sysId.equals(by.getSystemId())) {
                @Nullable
                String airTemp = by.getAirTemp();
                if (airTemp != null) {
                    updateData(HaywardBindingConstants.CHANNEL_BACKYARD_AIRTEMP, airTemp);
                } else {
                    logger.debug("Backyard air temperature missing");
                }

                @Nullable
                String state = by.getState();
                if (state != null) {
                    updateData(HaywardBindingConstants.CHANNEL_BACKYARD_STATE, state);
                } else {
                    logger.debug("Backyard state missing");
                }
            }
        }
        updateStatus(ThingStatus.ONLINE);
    }

    public boolean getAlarmList(String systemID) {
        List<String> bowID = new ArrayList<>();
        List<String> parameter1 = new ArrayList<>();
        List<String> message = new ArrayList<>();
        String alarmStr;
        // todo
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof BridgeHandler bridgehandler) {
            String urlParameters = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Name>GetAlarmList</Name><Parameters>"
                    // + "<Parameter name=\"Token\" dataType=\"String\">" + bridgehandler.getAccount().getToken()
                    + "</Parameter><Parameter name=\"MspSystemID\" dataType=\"int\">"
                    // + bridgehandler.getAccount().getMspSystemID()
                    + "</Parameter><Parameter name=\"CultureInfoName\" dataType=\"String\">en-us</Parameter></Parameters></Request>";

            try {
                String xmlResponse = bridgehandler.sendRequest(urlParameters, HaywardMessageType.MSP_ALARM_LIST);

                if (xmlResponse.isEmpty()) {
                    logger.debug("Hayward getAlarmList XML response was empty");
                    return false;
                }

                String status = bridgehandler
                        .evaluateXPath("/Response/Parameters//Parameter[@name='Status']/text()", xmlResponse).get(0);

                if (!("0".equals(status))) {
                    logger.trace("Hayward getAlarmList XML response: {}", xmlResponse);
                    return false;
                }

                bowID = bridgehandler.evaluateXPath("//Property[@name='BowID']/text()", xmlResponse);
                parameter1 = bridgehandler.evaluateXPath("//Property[@name='Parameter1']/text()", xmlResponse);
                message = bridgehandler.evaluateXPath("//Property[@name='Message']/text()", xmlResponse);

                for (int i = 0; i < 5; i++) {
                    if (i < bowID.size()) {
                        alarmStr = parameter1.get(i) + ": " + message.get(i);
                    } else {
                        alarmStr = "";
                    }
                    updateData("backyardAlarm" + String.format("%01d", i + 1), alarmStr);
                }
                this.updateStatus(ThingStatus.ONLINE);
                return true;
            } catch (HaywardException e) {
                logger.debug("Unable to get alarm list: {}", e.getMessage());
                return false;
            }
        } else {
            this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
            return false;
        }
    }
}
