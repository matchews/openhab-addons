package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Representation of an Action element.
 */
@NonNullByDefault
@XStreamAlias("Action")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = "type")
public class ActionConfig {
    private @Nullable String type;

    @XStreamImplicit(itemFieldName = "Device")
    private final List<DeviceConfig> devices = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getType() {
        return type;
    }

    public List<DeviceConfig> getDevices() {
        return devices;
    }

    public List<ParameterConfig> getParameters() {
        return parameters;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }
}
