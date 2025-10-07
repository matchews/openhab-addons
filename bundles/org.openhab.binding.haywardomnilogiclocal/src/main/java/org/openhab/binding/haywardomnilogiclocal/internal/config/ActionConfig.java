package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a Relay element.
 */
@NonNullByDefault
@XStreamAlias("Action")
public class ActionConfig {
    @XStreamAlias("Action-Function")
    private @Nullable String actionFunction;

    @XStreamAlias("Action-Data1")
    private @Nullable String actionData1;

    @XStreamAlias("Action-Data2")
    private @Nullable String actionData2;

    @XStreamAlias("Action-Data3")
    private @Nullable String actionData3;

    public @Nullable String getActionFunction() {
        return actionFunction;
    }

    public @Nullable String getActionData1() {
        return actionData1;
    }

    public @Nullable String getActionData2() {
        return actionData2;
    }

    public @Nullable String getActionData3() {
        return actionData3;
    }
}
