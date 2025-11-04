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
package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Representation of a Heater element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Heater")
public class Heater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("heaterState")
    private @Nullable String heaterState;

    @XStreamAsAttribute
    @XStreamAlias("temp")
    private @Nullable String temp;

    @XStreamAsAttribute
    @XStreamAlias("enable")
    private @Nullable String enable;

    @XStreamAsAttribute
    @XStreamAlias("priority")
    private @Nullable String priority;

    @XStreamAsAttribute
    @XStreamAlias("maintainFor")
    private @Nullable String maintainFor;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getState() {
        return heaterState;
    }

    public @Nullable String getTemp() {
        return temp;
    }

    public @Nullable String getEnable() {
        return enable;
    }

    public @Nullable String getPriority() {
        return priority;
    }

    public @Nullable String getMaintainFor() {
        return maintainFor;
    }
}
