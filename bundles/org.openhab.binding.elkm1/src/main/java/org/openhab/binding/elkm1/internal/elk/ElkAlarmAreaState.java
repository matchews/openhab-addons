/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

/**
 * The current alarm state of the area.
 *
 * @author David Bennett - Initial Contribution
 */
@NonNullByDefault
public enum ElkAlarmAreaState {
    NoAlarmActive,
    EntranceDelayIsActive,
    AlarmAbortDelayActive,
    FireAlarm,
    MedicalAlarm,
    PoliceAlarm,
    BurglarAlarm,
    Aux1Alarm,
    Aux2Alarm,
    Aux3Alarm,
    Aux4Alarm,
    CarbonMonoxideAlarm,
    EmergencyAlarm,
    FreezeAlarm,
    GasAlarm,
    HeatAlarm,
    WaterAlarm,
    FireSupervisory,
    VerifyFire
}
