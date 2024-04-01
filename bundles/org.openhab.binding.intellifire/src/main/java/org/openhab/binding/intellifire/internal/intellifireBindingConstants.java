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

import java.net.URI;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link intellifireBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class intellifireBindingConstants {

    private static final String BINDING_ID = "intellifire";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_TYPE_FAN = new ThingTypeUID(BINDING_ID, "fan");
    public static final ThingTypeUID THING_TYPE_FIREPLACE = new ThingTypeUID(BINDING_ID, "fireplace");
    public static final ThingTypeUID THING_TYPE_LIGHT = new ThingTypeUID(BINDING_ID, "light");
    public static final ThingTypeUID THING_TYPE_POWERVENT = new ThingTypeUID(BINDING_ID, "powerVent");
    public static final ThingTypeUID THING_TYPE_THERMOSTAT = new ThingTypeUID(BINDING_ID, "thermostat");

    // List of all Channel ids
    public static final String CHANNEL_FIREPLACE_FLAME = "fireplaceFlame";
    public static final String CHANNEL_FIREPLACE_HOT = "fireplaceHot";
    public static final String CHANNEL_FIREPLACE_PILOT = "fireplacePilot";
    public static final String CHANNEL_FIREPLACE_PREPURGE = "fireplacePrePurge";
    public static final String CHANNEL_FIREPLACE_TIMERENABLE = "timerEnable";
    public static final String CHANNEL_FIREPLACE_TIMEREMAINING = "timeRemaining";
    public static final String CHANNEL_FAN = "fan";
    public static final String CHANNEL_LIGHT = "light";
    public static final String CHANNEL_THERMO_ENABLE = "thermoEnable";
    public static final String CHANNEL_THERMO_SETPOINT = "thermoSetpoint";
    public static final String CHANNEL_THERMO_TEMPERATURE = "thermoTemperature";

    // Properties
    public static final String PROPERTY_BRIDGE_UPTIME = "bridgeUptime";
    public static final String PROPERTY_BRIDGE_FIRMWAREVERSION = "bridgeFirmwareVersion";
    public static final String PROPERTY_BRIDGE_BRAND = "bridgeBrand";
    public static final String PROPERTY_BRIDGE_IPADDRESS = "bridgeIPAddress";
    public static final String PROPERTY_FIREPLACE_NAME = "fireplaceName";

    // http Connection
    public static final String HTTP_HEADERS_HOST = "iftapi.net";
    public static final String HTTP_HEADERS_CONNECTION = "keep-alive";
    public static final String HTTP_HEADERS_ACCEPT = "*/*";
    public static final String HTTP_HEADERS_lANGUAGE = "en-US,en;q=0.9";
    public static final String HTTP_HEADERS_ACCEPTENCODING = "gzip, deflate";
    public static final String URL_LOGIN = "http://iftapi.net/a/login";
    public static final String URL_ENUMLOCATIONS = "http://iftapi.net/a/enumlocations";
    public static final String URL_GETUSERNAME = "http://iftapi.net/a/getusername";
    public static final URI URI_COOKIE = URI.create("http://iftapi.net");
}
