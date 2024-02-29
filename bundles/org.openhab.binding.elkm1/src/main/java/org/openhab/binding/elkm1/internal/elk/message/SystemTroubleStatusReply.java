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

package org.openhab.binding.elkm1.internal.elk.message;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.elkm1.internal.elk.ElkCommand;
import org.openhab.binding.elkm1.internal.elk.ElkMessage;

/**
 * System trouble status is sent by Elk upon trouble status change
 *
 * @author Matt Myers - Initial Contribution
 */
@NonNullByDefault
public class SystemTroubleStatusReply extends ElkMessage {

    public SystemTroubleStatusReply(String incomingData) {
        super(ElkCommand.SystemTroubleStatusReply);
    }
}
