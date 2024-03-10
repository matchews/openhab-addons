/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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

package org.openhab.binding.elkm1.internal.elk;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The elk command to the codes used.
 *
 * @author David Bennett - Initial Contribution
 */
@NonNullByDefault
public enum ElkCommand {
    Disarm("a0"),
    ArmAway("a1"),
    ArmToStayHome("a2"),
    ArmToStayInstant("a3"),
    ArmToNight("a4"),
    ArmToNightInstant("a5"),
    ArmToVacation("a6"),
    ArmToStepToNextAwayMode("a7"),
    ArmToStepToNextStayMode("a8"),
    ForceArmToAwayMode("a9"),
    ForceArmToStayMode("a:"),
    AlarmMemory("AM"),
    ArmingStatusRequest("as"),
    ArmingStatusRequestReply("AS"),
    AlarmReportAcknowledge("ar"),
    AlarmReporting("AR"),
    AlarmReportTestAcknowledge("at"),
    AlarmReportingTest("AT"),
    AlarmZoneRequest("az"),
    AlarmZoneRequestReply("AZ"),
    OutputChangeUpdate("CC"),
    ControlOutputOff("cf"),
    ControlOutputOn("cn"),
    CustomValueReply("CR"),
    OutputStatusReply("CS"),
    DisplayTextOnLedScreen("dm"),
    EntryExitData("EE"),
    SendEmailTrigger("EM"),
    ValidOrInvalidUserCode("IC"),
    KeypadKeyChangeUpdate("KC"),
    SystemLogUpdate("LD"),
    PLCChangeUpdate("PC"),
    RawCommand("rc"),
    StringTextDescription("sd"),
    StringTextDescriptionReply("SD"),
    SpeakPhraseAtVoiceOutput("sp"),
    SystemTroubleStatusReply("ss"),
    TemperatureReply("ST"),
    SpeakWordAtVoiceOutput("sw"),
    TaskChangeUpdate("TC"),
    ThermostatDataReply("TR"),
    RequestVersionNumber("vn"),
    RequestVersionNumberReply("VN"),
    EthernetModuleTest("XK"),
    EthernetModuleTestAcknowledge("xk"),
    BypassedZoneState("ZB"),
    ZoneChangeUpdateReport("ZC"),
    ZoneDefintionRequest("zd"),
    ZoneDefinitionReply("ZD"),
    ZonePartition("zp"),
    ZonePartitionReply("ZP"),
    ZoneStatusRequest("zs"),
    ZoneStatusReply("ZS"),
    ZoneAnalogVolatageRequest("zv"),
    ZoneAnalogVoltageReply("ZV");

    private final String myValue;

    private ElkCommand(String value) {
        this.myValue = value;
    }

    /**
     * The string value of the command.
     */
    public String getValue() {
        return myValue;
    }

    /**
     * Get an elk command from the string value.
     */
    public static @Nullable ElkCommand fromValue(String val) {
        for (ElkCommand cmd : ElkCommand.values()) {
            if (cmd.getValue().equals(val)) {
                return cmd;
            }
        }
        return null;
    }

    public String toSendString() {
        return "";
    }
}
