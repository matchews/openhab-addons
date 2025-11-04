/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Container for all schedule elements in the MSP configuration.
 *
 * @author Matt Myers - Initial contribution
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
