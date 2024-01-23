/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
package org.openhab.binding.elkm1.internal.elk.message;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.elkm1.internal.elk.ElkCommand;
import org.openhab.binding.elkm1.internal.elk.ElkMessage;

/**
 * The Send Elk Command class, to put the elk into armed away mode.
 *
 * @author Matt Myers - Initial Contribution
 *
 */
@NonNullByDefault
public class ValidOrInvalidUserCode extends ElkMessage {
    private String userNumber;

    public ValidOrInvalidUserCode(String data) {
        super(ElkCommand.ValidOrInvalidUserCode);
        userNumber = data.substring(12, 15);
    }

    public String getUserCode() {
        return userNumber;
    }

    @Override
    public String getData() {
        return "";
    }
}
