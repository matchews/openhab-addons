package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a device referenced within schedules or the DMT.
 */
@NonNullByDefault
@XStreamAlias("Device")
public class DeviceConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAlias("Device-System-Id")
    private @Nullable String deviceSystemIdElement;

    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAlias("Name")
    private @Nullable String nameElement;

    @XStreamAlias("Device-Name")
    private @Nullable String deviceNameElement;

    @XStreamAsAttribute
    private @Nullable String type;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAlias("Device-Type")
    private @Nullable String deviceTypeElement;

    @XStreamAsAttribute
    private @Nullable String subType;

    @XStreamAlias("Sub-Type")
    private @Nullable String subTypeElement;

    @XStreamAlias("Device-Sub-Type")
    private @Nullable String deviceSubTypeElement;

    @XStreamAsAttribute
    @XStreamAlias("parentSystemId")
    private @Nullable String parentSystemId;

    @XStreamAlias("Parent-System-Id")
    private @Nullable String parentSystemIdElement;

    @XStreamAlias("Device-Parent-System-Id")
    private @Nullable String deviceParentSystemIdElement;

    @XStreamAlias("Node-ID")
    private @Nullable String nodeIdElement;

    @XStreamAlias("Devices")
    private @Nullable DeviceList devicesElement;

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    public @Nullable String getSystemId() {
        if (deviceSystemIdElement != null) {
            return deviceSystemIdElement;
        }
        if (systemIdElement != null) {
            return systemIdElement;
        }
        return systemId;
    }

    public @Nullable String getName() {
        if (deviceNameElement != null) {
            return deviceNameElement;
        }
        if (nameElement != null) {
            return nameElement;
        }
        return name;
    }

    public @Nullable String getType() {
        if (deviceTypeElement != null) {
            return deviceTypeElement;
        }
        if (typeElement != null) {
            return typeElement;
        }
        return type;
    }

    public @Nullable String getSubType() {
        if (deviceSubTypeElement != null) {
            return deviceSubTypeElement;
        }
        if (subTypeElement != null) {
            return subTypeElement;
        }
        return subType;
    }

    public @Nullable String getParentSystemId() {
        if (deviceParentSystemIdElement != null) {
            return deviceParentSystemIdElement;
        }
        if (parentSystemIdElement != null) {
            return parentSystemIdElement;
        }
        return parentSystemId;
    }

    public @Nullable String getNodeId() {
        return nodeIdElement;
    }

    public List<DeviceConfig> getChildDevices() {
        DeviceList list = devicesElement;
        if (list == null) {
            return Collections.emptyList();
        }
        return list.getDevices();
    }

    public List<ParameterConfig> getParameters() {
        return parameters;
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
