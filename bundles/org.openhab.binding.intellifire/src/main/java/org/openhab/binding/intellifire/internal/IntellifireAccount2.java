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
// @NonNullByDefault
public class IntellifireAccount2 {
    public static class location {
        public String location_id = "";
        public String location_name = "";
        public String wifi_essid = "";
        public String wifi_password = "";
        public String postal_code = "";
        public String user_class = "";
        public List<IntellifireLocation> fireplaces;
    }

    public List<location> locations;
    public int email_notifications_enabled = 0;
}
