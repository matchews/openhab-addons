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

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Matt Myers - Initial contribution
 */
// @NonNullByDefault
public class IntellifireAccount {
    public static class location {
        public String location_id = "";
        public String location_name = "";
        public String wifi_essid = "";
        public String wifi_password = "";
        public String postal_code = "";
        public String user_class = "";
        public @Nullable IntellifireLocation fireplaces;
    }

    public @Nullable List<location> locations;
    public int email_notifications_enabled = 0;

    public IntellifirePollData getPollData(String serialNumber) {
        if (this.locations != null) {
            for (int i = 0; i < this.locations.size(); i++) {
                for (int j = 0; j < this.locations.get(i).fireplaces.fireplaces.size(); j++) {
                    if (serialNumber.equals(this.locations.get(i).fireplaces.fireplaces.get(j).serial)) {
                        return this.locations.get(i).fireplaces.fireplaces.get(j).pollData;
                    }
                }
            }
        }
        return null;
    }
}
