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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a device referenced within schedules or the DMT.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Device")
public class DeviceConfig {
    @XStreamAlias("Device-Name")
    private @Nullable String deviceName;

    @XStreamAlias("Type")
    private @Nullable String type;

    @XStreamAlias("Node-ID")
    private @Nullable String nodeId;

    @XStreamImplicit(itemFieldName = "Devices")
    private final List<DeviceConfig> devices = new ArrayList<>();

    public @Nullable String getName() {
        return deviceName;
    }

    public @Nullable String getType() {
        return type;
    }

    public @Nullable String getNodeId() {
        return nodeId;
    }

    public List<DeviceConfig> getDevices() {
        return devices;
    }

    @XStreamAlias("Devices")
    @NonNullByDefault
    private static class DeviceList {
        @XStreamImplicit(itemFieldName = "Device")
        private final List<DeviceConfig> devices = new ArrayList<>();

        public List<DeviceConfig> getDevices() {
            return devices;
        }
    }
}
