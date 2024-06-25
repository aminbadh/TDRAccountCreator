package com.aminbadh.tdrmanagerslpm.custom;

public class Admin {
    String fistName, lastName, role;

    public Admin() {
        // Used in Firebase Cloud Firestore.
    }

    public Admin(String fistName, String lastName, String role) {
        this.fistName = fistName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getFistName() {
        return fistName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }
}
