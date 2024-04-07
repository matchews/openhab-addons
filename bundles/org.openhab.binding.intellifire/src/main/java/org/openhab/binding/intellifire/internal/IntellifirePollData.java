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
    public int schedule_enable;
    public int thermostat;
    public int setpoint;
    public int timer;
    public int timeremaining;
    public int prepurge;
    public int remote_downtime;
    public int remote_uptime;
    public int remote_connection_quality;
    public int ecm_latency;
    public String ipv4_address;
    public List<String> errors;
    public int feature_light;
    public int feature_thermostat;
    public int power_vent;
    public int feature_fan;
    public String firmware_version;
    public String firmware_version_string;
    public String brand;
}
