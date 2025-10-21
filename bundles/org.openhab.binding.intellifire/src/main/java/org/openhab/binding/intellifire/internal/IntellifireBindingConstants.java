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
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link IntellifireBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class IntellifireBindingConstants {

    private static final String BINDING_ID = "intellifire";

    // Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ACCOUNT_BRIDGE = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID THING_TYPE_FIREPLACE = new ThingTypeUID(BINDING_ID, "fireplace");
    public static final ThingTypeUID THING_TYPE_REMOTE = new ThingTypeUID(BINDING_ID, "remote");

    public static final Set<ThingTypeUID> BRIDGE_THING_TYPES_UIDS = Set.of(THING_TYPE_ACCOUNT_BRIDGE);

    public static final Set<ThingTypeUID> THING_TYPES_UIDS = Set.of(IntellifireBindingConstants.THING_TYPE_FIREPLACE,
            IntellifireBindingConstants.THING_TYPE_REMOTE);

    // Channel typeIDs
    public static final String CHANNEL_TYPE_FAN = "intellifire:fan";
    public static final String CHANNEL_TYPE_LIGHT = "intellifire:light";

    // Channel ids
    public static final String CHANNEL_FIREPLACE_BATTERY = "battery";
    public static final String CHANNEL_FIREPLACE_COLDCLIMATEPILOT = "coldClimatePilot";
    public static final String CHANNEL_FIREPLACE_ECMLATENCY = "ecmLatency";
    public static final String CHANNEL_FIREPLACE_ERRORS = "errors";
    public static final String CHANNEL_FIREPLACE_FAN = "fan";
    public static final String CHANNEL_FIREPLACE_FLAMEHEIGHT = "flameHeight";
    public static final String CHANNEL_FIREPLACE_HOT = "hot";
    public static final String CHANNEL_FIREPLACE_LIGHT = "light";
    public static final String CHANNEL_FIREPLACE_POWER = "power";
    public static final String CHANNEL_FIREPLACE_PREPURGE = "prePurge";

    public static final String CHANNEL_REMOTE_CONNECTIONQUALITY = "remoteConnectionQuality";
    public static final String CHANNEL_REMOTE_DOWNTIME = "remoteDowntime";
    public static final String CHANNEL_REMOTE_ENABLE = "thermostatEnable";
    public static final String CHANNEL_REMOTE_SETPOINT = "thermostatSetpoint";
    public static final String CHANNEL_REMOTE_TEMPERATURE = "roomTemperature";
    public static final String CHANNEL_REMOTE_TIMERENABLE = "timerEnable";
    public static final String CHANNEL_REMOTE_TIMER = "timer";
    public static final String CHANNEL_REMOTE_UPTIME = "remoteUptime";

    // Properties
    public static final String PROPERTY_APIKEY = "API Key";
    public static final String PROPERTY_IPADDRESS = "IP Address";
    public static final String PROPERTY_LOCATIONID = "Location ID";
    public static final String PROPERTY_SERIALNUMBER = "Serial Number";
    public static final String PROPERTY_UNIQUEID = "Unique ID";
    public static final String PROPERTY_FIREPLACE_BRAND = "Brand";
    public static final String PROPERTY_FIREPLACE_FIRMWAREVERSION = "Firmware Version";
    public static final String PROPERTY_FIREPLACE_FEATURE_FAN = "Fan Feature";
    public static final String PROPERTY_FIREPLACE_FEATURE_LIGHT = "Light Feature";
    public static final String PROPERTY_FIREPLACE_MODEL = "Model";
    public static final String PROPERTY_FIREPLACE_NAME = "Name";

    // http Connection
    public static final String HTTP_HEADERS_ACCEPT = "*/*";
    public static final String HTTP_HEADERS_ACCEPTENCODING = "gzip, deflate";
    public static final String HTTP_HEADERS_CONNECTION = "keep-alive";
    public static final String HTTP_HEADERS_CONTENTTYPEURLENCODED = "application/x-www-form-urlencoded";
    public static final String HTTP_HEADERS_CONTENTTYPETEXT = "text/plain;charset=UTF-8";
    public static final String HTTP_HEADERS_HOST = "iftapi.net";
    public static final String HTTP_HEADERS_LANGUAGE = "en-US,en;q=0.9";
    public static final String URL_LOGIN = "http://iftapi.net/a/login";
    public static final String URL_ENUMLOCATIONS = "http://iftapi.net/a/enumlocations";
    public static final String URL_GETUSERNAME = "http://iftapi.net/a/getusername";
    public static final URI URI_COOKIE = URI.create("http://iftapi.net");

    // polling
    public static final boolean CLOUD_POLLING = true;
    public static final boolean LOCAL_POLLING = false;
}
