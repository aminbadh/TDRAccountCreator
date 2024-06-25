package com.aminbadh.tdrcontentcreatorlpm.custom;

public class Admin {
    private String firstName, lastName, role;

    public Admin() {
        // Used In Firebase Cloud Firestore.
    }

    public Admin(String firstName, String lastName, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }
}
