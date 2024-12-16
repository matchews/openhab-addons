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

    ACCESSORY(134,
            "Your appliance has detected that an AUX port or accessory is not functional. Please contact your dealer and report this issue."),
    DISABLED(129,
            "Appliance Safely Disabled: Your appliance has been disabled. Please contact your dealer and report this issue."),
    DISABLED2(145,
            "Appliance Safely Disabled: Your appliance has been disabled. Please contact your dealer and report this issue."),
    ECMOFFLINE(3269, "ECM is offline."),
    FAN(132, "Fan Error. Your appliance has detected that an accessory is not functional. Please contact your dealer and report this issue."),
    FANDELAY(6,
            "Fan Information: Fan will turn on within 3 minutes. Your appliance has a built-in delay that prevents the fan from operating within the first 3 minutes of turning on the appliance. This allows the air to be heated prior to circulation."),
    FLAME(4, "Pilot Flame Error. Your appliance has been safely disabled. Please contact your dealer and report this issue."),
    LIGHTS(133,
            "Lights Error. Your appliance has detected that an accessory is not functional. Please contact your dealer and report this issue."),
    MAINTENANCE(64,
            "Maintenance: Your appliance is due for a routine maintenance check. Please contact your dealer to ensure your appliance is operating at peak performance."),
    NONE(9999, ""),
    OFFLINE(642, "Your appliance is currently offline."),
    PILOTFLAME(2,
            "Pilot Flame Error: Your appliance has been safely disabled. Please contact your dealer and report this issue."),
    PILOTFLAME2(130,
            "Pilot Flame Error: Your appliance has been safely disabled. Please contact your dealer and report this issue."),
    SOFTLOCKOUT(144, "Sorry your appliance did not start. Try again by pressing Flame ON.");

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
