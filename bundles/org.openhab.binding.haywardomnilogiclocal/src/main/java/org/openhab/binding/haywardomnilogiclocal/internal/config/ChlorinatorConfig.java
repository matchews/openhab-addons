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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a Chlorinator element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Chlorinator")
public class ChlorinatorConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedType;

    @XStreamAlias("Enabled")
    private @Nullable String enabled;

    @XStreamAlias("Mode")
    private @Nullable String mode;

    @XStreamAlias("Timed-Percent")
    private @Nullable String timedPercent;

    @XStreamAlias("SuperChlor-Timeout")
    private @Nullable String superChlorTimeout;

    @XStreamAlias("Cell-Type")
    private @Nullable String cellType;

    @XStreamAlias("Dispenser-Type")
    private @Nullable String dispenserType;

    @XStreamAlias("ORP-Timeout")
    private @Nullable String orpTimeout;

    @XStreamAlias("ORP-Sensor-ID")
    private @Nullable String orpSensorId;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getSharedType() {
        return sharedType;
    }

    public @Nullable String getEnabled() {
        return enabled;
    }

    public @Nullable String getMode() {
        return mode;
    }

    public @Nullable String getTimedPercent() {
        return timedPercent;
    }

    public @Nullable String getSuperChlorTimeout() {
        return superChlorTimeout;
    }

    public @Nullable String getCellType() {
        return cellType;
    }

    public @Nullable String getDispenserType() {
        return dispenserType;
    }

    public @Nullable String getOrpTimeout() {
        return orpTimeout;
    }

    public @Nullable String getOrpSensorId() {
        return orpSensorId;
    }
}
