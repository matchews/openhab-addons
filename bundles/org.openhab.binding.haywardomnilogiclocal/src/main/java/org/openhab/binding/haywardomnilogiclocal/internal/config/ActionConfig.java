package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of an Action element.
 */
@NonNullByDefault
@XStreamAlias("Action")
@XStreamConverter(ActionConfigConverter.class)
public class ActionConfig {
    private @Nullable String legacyType;

    private @Nullable String actionFunction;

    private final Map<Integer, String> actionData = new TreeMap<>();

    @XStreamImplicit(itemFieldName = "Device")
    private final List<DeviceConfig> devices = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    void setLegacyType(@Nullable String legacyType) {
        String trimmed = trimToNull(legacyType);
        if (trimmed != null) {
            this.legacyType = trimmed;
        }
    }

    void setActionFunction(@Nullable String actionFunction) {
        String trimmed = trimToNull(actionFunction);
        if (trimmed != null) {
            this.actionFunction = trimmed;
        }
    }

    void addActionData(int index, @Nullable String value) {
        if (index <= 0) {
            return;
        }

        String trimmed = trimToNull(value);
        if (trimmed != null) {
            actionData.put(index, trimmed);
        }
    }

    void addDevice(DeviceConfig device) {
        devices.add(device);
    }

    void addParameter(ParameterConfig parameter) {
        parameters.add(parameter);
    }

    void addOperation(OperationConfig operation) {
        operations.add(operation);
    }

    public @Nullable String getType() {
        return getActionFunction();
    }

    public @Nullable String getActionFunction() {
        if (actionFunction != null) {
            return actionFunction;
        }

        return legacyType;
    }

    public Map<Integer, String> getActionDataByIndex() {
        return Collections.unmodifiableMap(actionData);
    }

    public List<String> getActionData() {
        return Collections.unmodifiableList(new ArrayList<>(actionData.values()));
    }

    public @Nullable String getActionDataValue(int index) {
        return actionData.get(index);
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

    private static @Nullable String trimToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
