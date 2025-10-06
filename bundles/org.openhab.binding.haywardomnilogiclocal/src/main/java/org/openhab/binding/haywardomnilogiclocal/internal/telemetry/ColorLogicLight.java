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
    @XStreamAlias("lightState")
    private @Nullable String lightState;

    @XStreamAsAttribute
    @XStreamAlias("currentShow")
    private @Nullable String currentShow;

    @XStreamAsAttribute
    @XStreamAlias("speed")
    private @Nullable String speed;

    @XStreamAsAttribute
    @XStreamAlias("brightness")
    private @Nullable String brightness;

    @XStreamAsAttribute
    @XStreamAlias("specialEffect")
    private @Nullable String specialEffect;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getlightState() {
        return lightState;
    }

    public @Nullable String getCurrentShow() {
        return currentShow;
    }

    public @Nullable String getSpeed() {
        return speed;
    }

    public @Nullable String getBrightness() {
        return brightness;
    }

    public @Nullable String getSpecialEffect() {
        return specialEffect;
    }
}
