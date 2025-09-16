package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the device mapping table (DMT) within the MSP configuration.
 */
@NonNullByDefault
@XStreamAlias("DMT")
public class DmtConfig {
    @XStreamImplicit(itemFieldName = "Device")
    private final List<DeviceConfig> devices = new ArrayList<>();

    public List<DeviceConfig> getDevices() {
        return devices;
    }
}
