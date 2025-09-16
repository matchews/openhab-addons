package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

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

    @XStreamImplicit(itemFieldName = "Backyard")
    private final List<BackyardConfig> backyards = new ArrayList<>();

    @XStreamAlias("Schedules")
    private final SchedulesConfig schedules = new SchedulesConfig();

    @XStreamAlias("DMT")
    private final DmtConfig dmt = new DmtConfig();

    @XStreamAlias("CHECKSUM")
    private @Nullable String checksum;

    public List<SystemConfig> getSystems() {
        return systems;
    }

    public List<BackyardConfig> getBackyards() {
        return backyards;
    }

    public List<ScheduleConfig> getSchedules() {
        return schedules.getSchedules();
    }

    public DmtConfig getDmt() {
        return dmt;
    }

    public @Nullable String getChecksum() {
        return checksum;
    }
}

