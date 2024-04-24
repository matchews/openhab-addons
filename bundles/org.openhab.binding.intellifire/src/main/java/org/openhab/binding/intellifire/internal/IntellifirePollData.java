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

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Matt Myers - Initial contribution
 */
public class IntellifirePollData {
    public String timestamp;
    public String name;
    public int temperature;
    public int battery;
    public int pilot;
    public int light;
    public int height;
    public int fanspeed;
    public int hot;
    public int power;
    @SerializedName(value = "schedule_enable")
    public int scheduleEnable;
    public int thermostat;
    public int setpoint;
    public int timer;
    public int timeremaining;
    public int prepurge;
    @SerializedName(value = "remote_downtime")
    public int remoteDowntime;
    @SerializedName(value = "remote_uptime")
    public int remoteUptime;
    @SerializedName(value = "remote_connection_quality")
    public int remoteConnectionQuality;
    @SerializedName(value = "ecm_latency")
    public int ecmLatency;
    @SerializedName(value = "ipv4_address")
    public String ipv4Address;
    public List<String> errors;
    @SerializedName(value = "feature_light")
    public int featureLight;
    @SerializedName(value = "feature_thermostat")
    public int featureThermostat;
    @SerializedName(value = "power_vent")
    public int featurePowerVent;
    @SerializedName(value = "feature_fan")
    public int featureFan;
    @SerializedName(value = "firmware_version")
    public String firmwareVersion;
    @SerializedName(value = "firmware_version_string")
    public String firmwareVersionString;
    public String brand;
}
