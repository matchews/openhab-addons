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
package org.openhab.binding.intellifire.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public enum IntellifireError {

    ACCESSORY(134, "Simplifire error."),
    DISABLED(129, "Appliance Safety Error. Appliance safely disabled"),
    DISABLED2(145,
            "Appliance Disabled. One or more error conditions has led to appliance being disabled. Schedule service"),
    ECMOFFLINE(3269,
            "ECM Offline. WFM and ECM hardware interaction issue. Can clear on its own, may require power cycling appliance"),
    FAN(132, "Fan Error. Check connection of fan on ACM."),
    FANDELAY(6, "Fan will turn on in 3 minutes."),
    FLAME(4, "Flame Error. Appliance is safely disabled."),
    LIGHTS(133, "Lights Error. Check connection of lights on ACM"),
    MAINTENANCE(64,
            "300 Hour Maintenance Error. Appliance has accumulated 300 hours of burn time and should be scheduled for maintenance."),
    NONE(9999, ""),
    OFFLINE(642,
            "Appliance Offline. The module was unreachable from the server, server was down recently, or WiFi network changed. Can clear on its own"),
    PILOTFLAME(2, "Pilot Flame Error. Appliance is safely disabled."),
    PILOTFLAME2(130, "Pilot Flame Error. Pilot Flame failed to rectify. Appliance will be safely disabled."),
    SOFTLOCKOUT(144,
            "Appliance Start Error. Appliance failed to activate main burner. Try again to ignite. If persistent or frequent, contact installer for service");

    private final int errorCode;
    private final String errorMessage;

    private IntellifireError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public static String fromErrorCode(String errorCode) {
        for (IntellifireError error : IntellifireError.values()) {
            if (error.errorCode == Integer.parseInt(errorCode)) {
                return error.errorMessage;
            }
        }
        return "";
    }
}
