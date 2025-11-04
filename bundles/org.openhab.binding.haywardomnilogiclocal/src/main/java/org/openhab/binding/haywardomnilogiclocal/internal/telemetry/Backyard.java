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
 * Representation of a Backyard element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Backyard")
public class Backyard {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    private @Nullable String statusVersion;

    @XStreamAsAttribute
    private @Nullable String airTemp;

    @XStreamAsAttribute
    private @Nullable String state;

    @XStreamAlias("ConfigChksum")
    @XStreamAsAttribute
    private @Nullable String configChksum;

    @XStreamAsAttribute
    private @Nullable String mspVersion;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getStatusVersion() {
        return statusVersion;
    }

    public @Nullable String getAirTemp() {
        return airTemp;
    }

    public @Nullable String getState() {
        return state;
    }

    public @Nullable String getConfigChksum() {
        return configChksum;
    }

    public @Nullable String getMspVersion() {
        return mspVersion;
    }
}
