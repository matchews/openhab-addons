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
package org.openhab.binding.intellifire.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class IntellifireAccount {
    public static class Location {
        @SerializedName(value = "location_id")
        public String locationId = "";
        @SerializedName(value = "location_name")
        public String locationName = "";
        @SerializedName(value = "wifi_essid")
        public String wifiEssid = "";
        @SerializedName(value = "wifi_password")
        public String wifiPassword = "";
        @SerializedName(value = "postal_code")
        public String postalCode = "";
        @SerializedName(value = "user_class")
        public String userClass = "";
        public IntellifireLocation fireplaces = new IntellifireLocation();
    }

    public List<Location> locations = new ArrayList<>();
    @SerializedName(value = "email_notifications_enabled")
    public int emailNotificationsEnabled = 0;
    public String userName = "";

    public @Nullable IntellifirePollData getPollData(String serialNumber) {
        for (int i = 0; i < this.locations.size(); i++) {
            for (int j = 0; j < this.locations.get(i).fireplaces.fireplaces.size(); j++) {
                if (serialNumber.equals(this.locations.get(i).fireplaces.fireplaces.get(j).serial)) {
                    return this.locations.get(i).fireplaces.fireplaces.get(j).pollData;
                }
            }
        }
        return null;
    }

    public String getIPAddress(String serialNumber) {
        for (int i = 0; i < this.locations.size(); i++) {
            for (int j = 0; j < this.locations.get(i).fireplaces.fireplaces.size(); j++) {
                if (serialNumber.equals(this.locations.get(i).fireplaces.fireplaces.get(j).serial)) {
                    return this.locations.get(i).fireplaces.fireplaces.get(j).pollData.ipv4Address;
                }
            }
        }
        return "";
    }

    public boolean getlastLocalPollSuccesful(String serialNumber) {
        for (int i = 0; i < this.locations.size(); i++) {
            for (int j = 0; j < this.locations.get(i).fireplaces.fireplaces.size(); j++) {
                if (serialNumber.equals(this.locations.get(i).fireplaces.fireplaces.get(j).serial)) {
                    return this.locations.get(i).fireplaces.fireplaces.get(j).lastLocalPollSuccesful;
                }
            }
        }
        return false;
    }
}
