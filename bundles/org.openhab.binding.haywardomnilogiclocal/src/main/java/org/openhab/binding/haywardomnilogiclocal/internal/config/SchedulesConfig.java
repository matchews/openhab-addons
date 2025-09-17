package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Container for all schedule elements in the MSP configuration.
 */
@NonNullByDefault
@XStreamAlias("Schedules")
public class SchedulesConfig {
    @XStreamImplicit(itemFieldName = "sche")
    private final List<ScheduleConfig> schedules = new ArrayList<>();

    @SuppressWarnings("unused")
    @XStreamImplicit(itemFieldName = "Schedule")
    private final List<ScheduleConfig> legacySchedules = schedules;

    public List<ScheduleConfig> getSchedules() {
        return schedules;
    }
}
