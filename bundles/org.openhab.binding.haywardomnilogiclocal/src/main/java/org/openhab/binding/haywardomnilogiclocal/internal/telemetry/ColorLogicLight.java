package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("ColorLogic-Light")
public class ColorLogicLight {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("currentShow")
    private String currentShow;

    @XStreamAsAttribute
    @XStreamAlias("brightness")
    private String brightness;

    public String getSystemId() {
        return systemId;
    }

    public String getCurrentShow() {
        return currentShow;
    }

    public String getBrightness() {
        return brightness;
    }
}
