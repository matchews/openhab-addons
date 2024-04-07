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
package org.openhab.binding.intellifire.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link IntellifireException} is thrown during the getMspConfig, mspConfigDiscovery, getTelemetry,
 * evaluateXPath and httpXmlResponse methods//todo
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class IntellifireException extends Exception {

    /**
     * The {@link IntellifireException} is thrown by //toDO
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message Hayward error message
     */
    public IntellifireException(String message) {
        super(message);
    }
}
