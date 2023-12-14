package com.group20seq.runway_redeclaration.Configs;

import java.util.function.*;

/**
 * A class to represent a group of runways. This is used to represent a pair of logical runways ie 09L/27R. The two
 * runways are stored as Runway objects, in lower and upper attributes.
 */
public class RunwayGroup {
    private Airport airport;
    private Runway lower;
    private Runway upper;

    public RunwayGroup(Runway lower, Runway upper) {
        // Store the two runways in the group.
        this.lower = lower;
        this.upper = upper;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public Runway getUpper() {
        // Return the upper runway.
        return upper;
    }

    public Runway getLower() {
        // Return the lower runway.
        return lower;
    }

    public void setUpper(Runway upper) {
        // Set the upper runway.
        this.upper = upper;
    }

    public void setLower(Runway right) {
        // Set the lower runway.
        this.lower = right;
    }

    public String getNames() {
        // Return the names of the runways in the group, separated by a slash.
        return lower.getName() + "/" + upper.getName();
    }

    public void setObstacleChangedListener(Consumer<Boolean> l) {
        upper.setObstacleChangedListener(l);
        lower.setObstacleChangedListener(l);
    }

}
