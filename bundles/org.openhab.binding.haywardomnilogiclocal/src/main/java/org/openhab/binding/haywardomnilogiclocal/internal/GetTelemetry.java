/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.haywardomnilogiclocal.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class GetTelemetry {

    private final String xmlns;

    @SerializedName("Name")
    private final String name;

    public GetTelemetry() {
        this.xmlns = "http://nextgen.hayward.com/api";
        this.name = "RequestTelemetryData";
    }

    public String getXmlns() {
        return xmlns;
    }

    public String getName() {
        return name;
    }
}
