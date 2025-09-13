package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("ColorLogic-Light")
public class ColorLogicLight {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("currentShow")
    private @Nullable String currentShow;

    @XStreamAsAttribute
    @XStreamAlias("brightness")
    private @Nullable String brightness;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getCurrentShow() {
        return currentShow;
    }

    public @Nullable String getBrightness() {
        return brightness;
    }
}
