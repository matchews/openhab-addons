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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * Utility for parsing telemetry STATUS messages using XStream.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public final class TelemetryParser {
    private static final XStream XSTREAM = new XStream(new StaxDriver());

    static {
        XSTREAM.ignoreUnknownElements();
        XSTREAM.addPermission(AnyTypePermission.ANY);
        XSTREAM.setClassLoader(TelemetryParser.class.getClassLoader());
        XSTREAM.processAnnotations(Status.class);
        XSTREAM.processAnnotations(ValveActuator.class);
    }

    private TelemetryParser() {
    }

    public static Status parse(String xml) {
        return (Status) XSTREAM.fromXML(xml);
    }
}
