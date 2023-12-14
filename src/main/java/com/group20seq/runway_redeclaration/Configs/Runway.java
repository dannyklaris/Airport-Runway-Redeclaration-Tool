package com.group20seq.runway_redeclaration.Configs;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.function.*;

import javafx.scene.control.Alert;


/**
 * A class to represent a runway. This class stores the name, TORA, TODA, ASDA, LDA and displaced threshold of the
 * runway. It stores the original and working values of these attributes. It also stores the obstacle on the runway
 * and its position. It also stores the length of the unmodified runway.
 */
public class Runway { // TODO : should stripEnd and RESA have starting / working values -- not changed by obstacle
    private final String name;
    private final int startTORA;
    private final int startTODA;
    private final int startASDA;
    private final int startLDA;
    private final int startDisplacedThreshold;
    private final int startStripEnd;
    private final int startRESA;
    private int TORA;
    private int TODA;
    private int ASDA;
    private int LDA;
    private int displacedThreshold;
    private int stripEnd;
    private int RESA;
    private ArrayList<String> breakdown;
    private String sign;
    private String bearing;
    private Consumer<Boolean> listener;

    private final int length;

    private Obstacle obstacle = null;
    private int obstaclePosition = 0;


    /**
     * Create a new runway, with given parameters. Note the length is the length of the runway before any changes are
     * made to it (could equal TORA). This constructor is private as it is only used by the public constructor.
     * @param name The name of the runway.
     * @param TORA The TORA of the runway.
     * @param TODA The TODA of the runway.
     * @param ASDA The ASDA of the runway.
     * @param LDA The LDA of the runway.
     * @param displacedThreshold The displaced threshold of the runway.
     * @param length The length of the runway before any changes are made to it (could equal TORA).
     */
    private Runway(String name, int TORA, int TODA, int ASDA, int LDA, int displacedThreshold, int length) {
        // Load the name, TORA, TODA, ASDA, LDA and displaced threshold from the parameters. These are the values from
        // an XML file or manually inputted into the system. The length is the length of the runway before any changes
        // are made to it (could equal TORA).
        this.name = name;
        this.startTORA = TORA;
        this.length = length;
        this.startTODA = TODA;
        this.startASDA = ASDA;
        this.startLDA = LDA;
        this.startDisplacedThreshold = displacedThreshold;
        this.breakdown = new ArrayList<>();
        // Fix the strip end to 60 and the RESA to 240. These are constants that are not likely to change.
        this.startStripEnd = 60;
        this.startRESA = 240;

        Pattern bearing = Pattern.compile("\\d+");
        bearing.matcher(name);
        Matcher bearingM = bearing.matcher(name);
        while (bearingM.find()) {
            this.bearing = bearingM.group(0);
        }
        if (this.bearing.length() == 1) this.bearing = "0" + this.bearing;

        Pattern sign = Pattern.compile("(?<=\\d)[RL]");
        Matcher signM = sign.matcher(name);
        while (signM.find()) {
            this.sign = signM.group(0);
        }
        // Set the working values to the original values.
        resetToStart();
    }


    /**
     * Create a new runway, with given parameters. This is the public constructor, which calls the private constructor
     * with the length parameter set to the TORA. This constructor is used by the rest of the code as the way to create
     * a runway.
     * @param name The name of the runway.
     * @param TORA The TORA of the runway.
     * @param TODA The TODA of the runway.
     * @param ASDA The ASDA of the runway.
     * @param LDA The LDA of the runway.
     * @param displacedThreshold The displaced threshold of the runway.
     */
    public Runway(String name, int TORA, int TODA, int ASDA, int LDA, int displacedThreshold) {
        // Load the name, TORA, TODA, ASDA, LDA and displaced threshold from the parameters. These are the values from
        // an XML file or manually inputted into the system.
        this(name, TORA, TODA, ASDA, LDA, displacedThreshold, TORA);
    }


    /**
     * Reset the working values to the original values. This is used when an obstacle is removed from the runway, as
     * the working values are changed when an obstacle is added.
     */
    private void resetToStart() {
        // Reset the working values to the original values.
        this.TORA = startTORA;
        this.TODA = startTODA;
        this.ASDA = startASDA;
        this.LDA = startLDA;
        this.displacedThreshold = startDisplacedThreshold;
        this.stripEnd = startStripEnd;
        this.RESA = startRESA;
    }


    /**
     * Add an obstacle to the runway. Save the obstacle and its position.
     * @param obstacle The obstacle to add to the runway.
     * @param where The position of the obstacle on the runway.
     */
    public void addObstacle(Obstacle obstacle, int where) {
        // Register an obstacle on the runway.
        this.obstacle = obstacle;
        this.obstaclePosition = where;
        if (listener != null)
            listener.accept(true);
    }


    /**
     * Remove an obstacle from the runway. Reset the working values to the original values.
     */
    public void removeObstacle() {
        // Remove an obstacle from the runway.
        this.obstacle = null;
        resetToStart();
        if (listener != null)
            listener.accept(false);
    }


    /**
     * Recalculate the runway values after an obstacle is added. This method is used for when a plane is
     * "Taking Off Towards || Landing Towards"
     * @param aircraft The aircraft that is taking off or landing.
     */
    public void TOTLT(Aircraft aircraft) {
        // Take of towards or land towards.
        // If there is no obstacle, return the runway as it is.
        if (this.obstacle == null)
            return;

        // Recalculate the runway values so reset the working values to the original values, otherwise the runway
        // will try to calculate the values from the previously recalculated values.
        resetToStart();

        // Calculate the new values for the runway.
        TORA = Math.max(0, this.obstaclePosition - obstacle.getWidth() - this.stripEnd + this.displacedThreshold);
        TODA = Math.max(0, TORA);
        ASDA = Math.max(0, TORA);
        LDA  = Math.max(0, this.obstaclePosition - this.RESA - this.stripEnd);

        breakdown.clear();
        breakdown.add("TORA = Obstacle Position - Obstacle Width - Strip End + Displaced Threshold");
        breakdown.add(String.format("TORA = %d - %d - %d + %d = %d", this.obstaclePosition, obstacle.getWidth(), this.stripEnd, this.displacedThreshold, this.TORA));
        breakdown.add("TODA = TORA");
        breakdown.add(String.format("TODA = %d", this.TORA));
        breakdown.add("ASDA = TORA");
        breakdown.add(String.format("ASDA = %d", this.TORA));
        breakdown.add("LDA = Obstacle Position - RESA - Strip End");
        breakdown.add(String.format("LDA = %d - %d - %d = %d", this.obstaclePosition, this.RESA, this.stripEnd, this.LDA));

    }


    /**
     * Recalculate the runway values after an obstacle is added. This method is used for when a plane is
     * "Taking Off Away || Landing Over"
     * @param aircraft
     */
    public void TOALO(Aircraft aircraft) {
        // Take of away from or land over.
        // If there is no obstacle, return the runway as it is.
        if (this.obstacle == null)
            return ;

        // Recalculate the runway values so reset the working values to the original values, otherwise the runway
        // will try to calculate the values from the previously recalculated values.
        resetToStart();

        // Calculate the new values for the runway.
        TORA = Math.max(0, this.TORA - this.obstaclePosition - aircraft.blastProtection() - this.displacedThreshold);
        TODA = Math.max(0, TORA + getClearway());
        ASDA = Math.max(0, TORA + getStopway());
        LDA  = Math.max(0, this.LDA - this.obstaclePosition - this.stripEnd - this.obstacle.getWidth());

        this.breakdown.clear();
        this.breakdown.add("TORA = TORA - Obstacle Position - Blast Protection - Displaced Threshold");
        this.breakdown.add(String.format("TORA = %d - %d - %d - %d = %d", this.startTORA, this.obstaclePosition, aircraft.blastProtection(), this.displacedThreshold, this.TORA));
        this.breakdown.add("TODA = TORA + Clearway");
        this.breakdown.add(String.format("TODA = %d + %d = %d", this.TORA, this.getClearway(), this.TODA));
        this.breakdown.add("ASDA = TORA + Stopway");
        this.breakdown.add(String.format("ASDA = %d + %d = %d", this.TORA, this.getStopway(), this.ASDA));
        this.breakdown.add("LDA = LDA - Obstacle Position - Strip End - Obstacle Width");
        this.breakdown.add(String.format("LDA = %d - %d - %d - %d = %d", this.LDA, this.obstaclePosition, this.stripEnd, this.obstacle.getWidth(), this.LDA));
    }


    /**
     * Determine the name of the opposite logical runway.
     * @param runwayName The name of the runway.
     * @return The name of the opposite logical runway.
     */
    public static String determineOppositeRunwayName(String runwayName) {
        // Split the runway name into the number and the letter. The letter is after the first two numbers, and is
        // either L, R, C or empty. The number is the first two characters in the string.
        var runwayNumber = Integer.parseInt(runwayName.substring(0, 2));
        var runwayLetter = runwayName.substring(2);

        // The opposite runway's number is the current number plus 18, modulo 36. The opposite runway's letter is
        // determined by a small mapping under the switch statement.
        var oppositeRunwayNumber = (runwayNumber + 18) % 36;
        var oppositeRunwayLetter = switch (runwayLetter) {
            case "L" -> "R";
            case "R" -> "L";
            case "C" -> "C";
            default -> new String();
        };

        // Return the opposite runway's name (format the number to two digits) and append the letter.
        return String.format("%02d", oppositeRunwayNumber) + oppositeRunwayLetter;
    }

    public void setObstacleChangedListener(Consumer<Boolean> l) {
        this.listener = l;
    }

    public void removeObstacleChangedListener() {
        this.listener = null;
    }

    public int[] getRunwayParameters() {
        return new int[]{this.TORA, this.TODA, this.ASDA, this.LDA};
    }

    public int getStopway() {return this.startASDA - this.startTORA;}
    public int getClearway() {return this.startTODA - this.startASDA;}
    public int getTORA() {return this.TORA;}
    public int getTODA() {return this.TODA;}
    public int getASDA() {return this.ASDA;}
    public int getLDA() {return this.LDA;}
    public int getDisplacedThreshold() {return this.displacedThreshold;}
    public int getStripEnd() {return this.stripEnd;}
    public String getName() {return this.name;}

    public String getSign() {
        return this.bearing + this.sign;
    }
    public String getFormattedName() {return this.name.substring(0, 2) + "\n" + this.name.substring(2);}
    public int getLength() {return length;}
    public int getObstaclePosition() {return this.obstaclePosition;}
    public int getStartTORA() {return this.startTORA;}
    public int getStartTODA() {return this.startTODA;}
    public int getStartASDA() {return this.startASDA;}
    public int getStartLDA() {return this.startLDA;}
    public Obstacle getObstacle() {return this.obstacle;}
    public ArrayList<String> getBreakdown() {return this.breakdown;}
    public String getAllParams() {
        return String.format("TORA: %d, TODA: %d, ASDA: %d, LDA: %d", this.TORA, this.TODA, this.ASDA, this.LDA);
    }
    public int getAngle() {return Integer.parseInt(this.name.substring(0, 2));}

}
