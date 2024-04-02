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

import java.util.List;

/**
 *
 * @author Matt Myers - Initial contribution
 */
public class intellifireLocation {
    public static class fireplace {
        public String serial;
        public String brand;
        public String name;
        public String apikey;
        public int power;
    }

    public List<fireplace> fireplaces;
    public String location_name;
}
