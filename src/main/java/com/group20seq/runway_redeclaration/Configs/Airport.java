package com.group20seq.runway_redeclaration.Configs;

import java.io.File;
import java.util.ArrayList;

import com.group20seq.runway_redeclaration.Controllers.FilesController;

/**
 * Class that represents an airport. The airport is defined by a name and a list of runway groups. The airport can be
 * constructed from an XML file.
 */
public class Airport implements Serialisable {
    private final String name;
    private final ArrayList<RunwayGroup> runwayGroups;


    /**
     * Constructor for the Airport class. The airport is defined by a name and a list of runway groups. Public
     * constructor used by the rest of the code to create airports.
     * @param name Name of the airport.
     * @param runways List of runway groups.
     */
    public Airport(String name, ArrayList<RunwayGroup> runways) {
        this.name = name;
        this.runwayGroups = runways;
    }

    public String getPath() {
        return FilesController.getFilesController().getFile(Airport.class).getPath();
    }

    public String toXML() {
        // Create the XML string
        StringBuilder xml= new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xml.append("<root>\n");
        xml.append("<Name>" + getName() + "</Name>\n");
        for (RunwayGroup rg : runwayGroups) {
            Runway u = rg.getUpper();
            Runway l = rg.getLower();
            xml.append("<Runway Name=\"" + u.getSign() +
                       "\" TORA=\"" + u.getTORA() +
                       "\" TODA=\"" + u.getTODA() +
                       "\" ASDA=\"" + u.getASDA() +
                       "\" LDA=\"" + u.getLDA() +
                       "\" DPTH=\"" + u.getDisplacedThreshold() +
                    "\"/>\n");
            xml.append("<Runway Name=\"" + l.getSign() +
                    "\" TORA=\"" + l.getTORA() +
                    "\" TODA=\"" + l.getTODA() +
                    "\" ASDA=\"" + l.getASDA() +
                    "\" LDA=\"" + l.getLDA() +
                    "\" DPTH=\"" + l.getDisplacedThreshold() +
                    "\"/>\n");
        }
        xml.append("</root>");


        return xml.toString();
    }

    public static String getClassName() {
        return "Airport";
    }
    /**
     * Creates an airport from an XML file. Used by the XMLLoader class to deserialize airports. The "name" and "runway"
     * XML attributes are required to create the object.
     * @param configPath Path to the XML file.
     * @return Airport object.
     */
    public static Airport fromXML(File configPath) {
        try {
            // Get the XML Document from the MXL file. Create an empty list of Runways to store the parsed results before
            // the grouping occurs. Get the name of the airport from the XML file.
            var document = XMLLoader.parse(configPath);
            var runways = new ArrayList<Runway>();
            var name = document.getElementsByTagName("Name").item(0).getTextContent();

            // Parse the XML file and add the runways to the list of runways. The XML file is structured such that each
            // runway is a child of the root node. The attributes of the runway are the TORA, TODA, ASDA, LDA and DPTH.
            for (int i = 0; i < document.getElementsByTagName("Runway").getLength(); i++) {
                var runway = document.getElementsByTagName("Runway").item(i);
                var runwayName = runway.getAttributes().getNamedItem("Name").getNodeValue();

                var TORA = Integer.parseInt(runway.getAttributes().getNamedItem("TORA").getNodeValue());
                var TODA = Integer.parseInt(runway.getAttributes().getNamedItem("TODA").getNodeValue());
                var ASDA = Integer.parseInt(runway.getAttributes().getNamedItem("ASDA").getNodeValue());
                var LDA = Integer.parseInt(runway.getAttributes().getNamedItem("LDA").getNodeValue());
                var DPTH = Integer.parseInt(runway.getAttributes().getNamedItem("DPTH").getNodeValue());
                runways.add(new Runway(runwayName, TORA, TODA, ASDA, LDA, DPTH));
            }


            // Group the runways into pairs. The runways are grouped by their opposite runways. For example, if there is a
            // runway named 09L, then the opposite runway is 27R.
            var runwayGroups = new ArrayList<RunwayGroup>();
            for (Runway r: runways) {

                // Determine the opposite runway name. For example, if the runway is 09L, then the opposite runway is 27R.
                String oppositeRunwayName = Runway.determineOppositeRunwayName(r.getName());

                // The first loop checks if the opposite runway has already been added to a group. If it has, then the
                // current runway is added to the opposite runway's group. If the opposite runway has not been added to a
                // group, then a new group is created for the current runway.
                var added = false;
                for (var existingGroup: new ArrayList<>(runwayGroups)) {
                    // If the opposite runway is a left runway, then check if the left runway of the group is the opposite
                    // runway. If it is, then add the current runway to the right of the group.
                    if (existingGroup.getLower().getName().equals(oppositeRunwayName)) {
                        existingGroup.setUpper(r);
                        added = true; break;
                    }
                }

                // If the opposite runway has not been added to a group, then create a new group for the current runway.
                // The new group is added to the left if the current runway is a left runway, and to the right if the
                // current runway is a right runway.
                if (!added)
                    runwayGroups.add(new RunwayGroup(r, null));
            }

            // Return the airport with the name and runway groups.
            var airport = new Airport(name, runwayGroups);
            for (var runwayGroup: runwayGroups)
                runwayGroup.setAirport(airport);
            return airport;
        }

        // Catch any exceptions that occur when parsing the XML file.
        catch (Exception ignored) {
            // TODO : popup error message.
            return null;
        }
    }

    public ArrayList<RunwayGroup> getRunwayGroups() {
        // Return the runway groups.
        return this.runwayGroups;
    }

    public ArrayList<Runway> getRunways() {
        // Return the runways, by getting the runways from the runway groups.
        var runways = new ArrayList<Runway>();
        for (var runwayGroup: runwayGroups) {
            runways.add(runwayGroup.getLower());
            runways.add(runwayGroup.getUpper());
        }
        return runways;
    }

    public String getName() {
        // Return the name of the airport.
        return name;
    }
}
