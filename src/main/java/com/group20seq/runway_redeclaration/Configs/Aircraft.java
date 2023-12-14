package com.group20seq.runway_redeclaration.Configs;


/**
 * Default dummy aircraft class. This is used to represent an aircraft to land or take off on a runway. It stores the
 * blast protection of the aircraft.
 */
public class Aircraft {
    private final int blastProtection;

    public Aircraft() {
        this.blastProtection = 300;
    }

    public int blastProtection() {return this.blastProtection;}
}
