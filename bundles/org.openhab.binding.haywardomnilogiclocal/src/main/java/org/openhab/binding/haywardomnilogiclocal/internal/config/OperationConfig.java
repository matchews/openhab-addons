package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a BodyOfWater element.
 */
@NonNullByDefault
@XStreamAlias("Operation")
public class OperationConfig {
    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamImplicit(itemFieldName = "Heater-Equipment")
    private final List<HeaterEquipConfig> heaterEquips = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Action")
    private final List<ActionConfig> actions = new ArrayList<>();

    public List<HeaterEquipConfig> getHeaterEquips() {
        return heaterEquips;
    }

    public List<ActionConfig> getActions() {
        return actions;
    }
}