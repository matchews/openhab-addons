/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
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
package org.openhab.binding.haywardomnilogiclocal.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link Config} class contains fields mapping thing configuration parameters.
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public class Config {
    private String host = "";
    private int alarmPollTime = 60;
    private int telemetryPollTime = 10;

    public Config() {
    }

    public String getEndpointUrl() {
        return host;
    }

    public void setEndpointUrl(String host) {
        this.host = host;
    }

    public int getAlarmPollTime() {
        return alarmPollTime;
    }

    public void setAlarmPollTime(int alarmPollTime) {
        this.alarmPollTime = alarmPollTime;
    }

    public int getTelemetryPollTime() {
        return telemetryPollTime;
    }

    public void setTelemetryPollTime(int telemetryPollTime) {
        this.telemetryPollTime = telemetryPollTime;
    }
}
