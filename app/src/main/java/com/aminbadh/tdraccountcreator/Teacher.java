package com.aminbadh.tdraccountcreator;

import java.io.Serializable;
import java.util.ArrayList;

public class Teacher implements Serializable {
    private String email, firstName, lastName, subject;
    private ArrayList<String> levels, level1S, level2L, level2Sc;

    public Teacher() {
        // Used in FireStore Database.
    }

    public Teacher(String email, String firstName, String lastName, String subject,
                   ArrayList<String> levels, ArrayList<String> level1S, ArrayList<String> level2L,
                   ArrayList<String> level2Sc) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subject = subject;
        this.levels = levels;
        this.level1S = level1S;
        this.level2L = level2L;
        this.level2Sc = level2Sc;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSubject() {
        return subject;
    }

    public ArrayList<String> getLevels() {
        return levels;
    }

    public ArrayList<String> getLevel1S() {
        return level1S;
    }

    public ArrayList<String> getLevel2L() {
        return level2L;
    }

    public ArrayList<String> getLevel2Sc() {
        return level2Sc;
    }
}
