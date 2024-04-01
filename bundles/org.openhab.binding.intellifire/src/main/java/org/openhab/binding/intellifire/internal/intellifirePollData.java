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
public class intellifirePollData {
    String timestamp;
    String name;
    int temperature;
    int battery;
    int pilot;
    int light;
    int height;
    int fanspeed;
    int hot;
    int power;
    int schedule_enable;
    int thermostat;
    int setpoint;
    int timer;
    int timeremaining;
    int prepurge;
    int remote_downtime;
    int remote_uptime;
    int remote_connection_quality;
    int ecm_latency;
    String ipv4_address;
    List<String> errors;
    int feature_light;
    int feature_thermostat;
    int power_vent;
    int feature_fan;
    String firmware_version;
    String firmware_version_string;
    String brand;
}