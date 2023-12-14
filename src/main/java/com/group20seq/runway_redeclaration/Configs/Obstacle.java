package com.group20seq.runway_redeclaration.Configs;


import java.io.File;
import java.io.FileWriter;

import com.group20seq.runway_redeclaration.Controllers.FilesController;


/**
 * Class handling obstacles that can be placed on the runway.
 */
public class Obstacle implements Serialisable {
    private final String name;
    private final int height;


    /**
     * Constructor for the obstacle class. Public constructor used by the rest of the code to create obstacles.
     * @param name Name of the obstacle
     * @param height Height of the obstacle
     */
    public Obstacle(String name, int height) {
        // Store the properties of the obstacle
        this.name = name;
        this.height = height;
    }

    public String getPath() {
        return FilesController.getFilesController().getFile(Obstacle.class).getPath();
    }

    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xml.append("<root>\n");
        xml.append("<Obstacle Name=\"" + getName() +
                   "\" Height=\"" + getHeight() +
                   "\"/>\n");
        xml.append("</root>");
        return xml.toString();
    }


    /**
     * Creates an obstacle from an XML file. Used by the XMLLoader class to deserialize obstacles. The "name" and
     * "height" XML attributes are required to create the object.
     * @param configPath Path to the XML file
     * @return Obstacle object
     */
    public static Obstacle fromXML(File configPath) {
        // Parse the XML file into an Obstacle object
        try {
            var document = XMLLoader.parse(configPath);

            var obstacle = document.getElementsByTagName("Obstacle").item(0);
            var name = obstacle.getAttributes().getNamedItem("Name").getTextContent();
            var height = Integer.parseInt(obstacle.getAttributes().getNamedItem("Height").getTextContent());
            return new Obstacle(name, height);
        }

        // If there is an error, return a default obstacle that will be ignored when added (no size)
        catch (Exception ignored) {
            // TODO - Error popup or something
            return new Obstacle("", 0);
        }
    }


    /**
     * Serializes an obstacle into an XML file. Used by the XMLLoader class to serialize obstacles. The "name" and
     * "height" Java attributes are saved to the XML file.
     * @param obstacle Obstacle to serialize
     */
    public static void toXML(Obstacle obstacle) {
        // Serialize the obstacle into an XML file
        try {
            var folder = new File(Obstacle.class.getResource("/Obstacles").toURI());
            var file = new File(folder, String.format("/%s.xml", obstacle.getName()));
            if (file.createNewFile())
                XMLLoader.serialize(obstacle);
        }

        // If there is an error, do nothing
        catch (Exception ignored) {
            // TODO - Error popup or something
        }
    }

    public String getName() {
        return this.name;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.height * 50;
    }
}
