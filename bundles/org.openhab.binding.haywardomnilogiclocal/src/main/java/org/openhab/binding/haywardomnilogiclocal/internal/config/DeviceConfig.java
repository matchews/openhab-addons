package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a device referenced within schedules or the DMT.
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
