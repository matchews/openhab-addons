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
package org.openhab.binding.haywardomnilogiclocal.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link HaywardAccount} class contains fields mapping thing configuration parameters.
 *
 * @author Matt Myers - Initial contribution
 */

@NonNullByDefault
public class HaywardAccount {
    private String token = "";
    private String mspSystemID = "";
    private String userID = "";
    private String backyardName = "";
    private String address = "";
    private String firstName = "";
    private String lastName = "";
    private String roleType = "";
    private String units = "";
    private String vspSpeedFormat = "";

    public HaywardAccount() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMspSystemID() {
        return mspSystemID;
    }

    public void setMspSystemID(String mspSystemID) {
        this.mspSystemID = mspSystemID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBackyardName() {
        return backyardName;
    }

    public void setBackyardName(String backyardName) {
        this.backyardName = backyardName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getVspSpeedFormat() {
        return vspSpeedFormat;
    }

    public void setVspSpeedFormat(String vspSpeedFormat) {
        this.vspSpeedFormat = vspSpeedFormat;
    }
}
