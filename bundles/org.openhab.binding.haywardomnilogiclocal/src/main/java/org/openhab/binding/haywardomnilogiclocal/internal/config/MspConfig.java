package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the MSP configuration root element.
 */
@NonNullByDefault
@XStreamAlias("MSPConfig")
public class MspConfig {
    @XStreamImplicit(itemFieldName = "System")
    private final List<SystemConfig> systems = new ArrayList<>();

    public List<SystemConfig> getSystems() {
        return systems;
    }
}

