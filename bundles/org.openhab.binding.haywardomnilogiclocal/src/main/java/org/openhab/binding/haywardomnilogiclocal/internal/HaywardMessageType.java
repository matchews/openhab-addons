package org.openhab.binding.haywardomnilogiclocal.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The type to request.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public enum HaywardMessageType {

    XML_ACK(0000),
    REQUEST_CONFIGURATION(1),
    SET_FILTER_SPEED(9),
    SET_HEATER_COMMAND(11),
    SET_SUPERCHLORINATE(15),
    REQUEST_LOG_CONFIG(31),
    SET_SOLAR_SET_POINT_COMMAND(40),
    SET_HEATER_MODE_COMMAND(42),
    SET_CHLOR_ENABLED(121),
    SET_HEATER_ENABLED(147),
    SET_CHLOR_PARAMS(155),
    SET_EQUIPMENT(164),
    CREATE_SCHEDULE(230),
    DELETE_SCHEDULE(231),
    GET_TELEMETRY(300),
    GET_ALARM_LIST(304),
    SET_STANDALONE_LIGHT_SHOW(308),
    SET_SPILLOVER(311),
    RUN_GROUP_CMD(317),
    RESTORE_IDLE_STATE(340),
    GET_FILTER_DIAGNOSTIC_INFO(386),
    HANDSHAKE(1000),
    ACK(1002),
    MSP_TELEMETRY_UPDATE(1004),
    MSP_CONFIGURATIONUPDATE(1003),
    MSP_ALARM_LIST(1304),
    MSP_LEADMESSAGE(1998),
    MSP_BLOCKMESSAGE(1999);

    private final int msgInt;

    private HaywardMessageType(int msgInt) {
        this.msgInt = msgInt;
    }
}