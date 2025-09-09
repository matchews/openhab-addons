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
package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link HaywardConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public class HaywardConfig {
    private String endpointUrl = "";
    private String username = "";
    private String password = "";
    private int alarmPollTime = 60;
    private int telemetryPollTime = 10;

    public HaywardConfig() {
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
