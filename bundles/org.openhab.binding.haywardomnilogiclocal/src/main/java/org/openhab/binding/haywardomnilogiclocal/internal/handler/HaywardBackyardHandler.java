package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardThingHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.protocol.ParameterValue;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class HaywardBackyardHandler extends HaywardThingHandler {
    private final Logger logger = LoggerFactory.getLogger(HaywardBackyardHandler.class);

    public HaywardBackyardHandler(Thing thing) {
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

    public boolean getAlarmList(String systemID) {
        List<String> bowID = new ArrayList<>();
        List<String> parameter1 = new ArrayList<>();
        List<String> message = new ArrayList<>();
        String alarmStr;

        Bridge bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof HaywardBridgeHandler bridgehandler) {
            String urlParameters = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><Name>GetAlarmList</Name><Parameters>"
                    + "<Parameter name=\"Token\" dataType=\"String\">" + bridgehandler.getAccount().getToken()
                    + "</Parameter><Parameter name=\"MspSystemID\" dataType=\"int\">"
                    + bridgehandler.getAccount().getMspSystemID()
                    + "</Parameter><Parameter name=\"CultureInfoName\" dataType=\"String\">en-us</Parameter></Parameters></Request>";

            try {
                String xmlResponse = bridgehandler.udpXmlResponse(urlParameters, HaywardMessageType.MSP_ALARM_LIST);

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
