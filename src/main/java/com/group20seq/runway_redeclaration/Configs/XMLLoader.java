package com.group20seq.runway_redeclaration.Configs;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.io.FilenameUtils;


/**
 * Class that handles the serialization and deserialization of objects into XML files.
 */
public class XMLLoader {
    private static Boolean b = true;
    /**
     * Parses an XML file into a Document object. Individual classes then handle the parsing of the document (inspecting
     * attributes etc).
     * @param configPath Path to the XML file
     * @return Document object
     * @throws ParserConfigurationException If the DocumentBuilder cannot be created
     * @throws IOException If the file cannot be read
     * @throws SAXException If the file cannot be parsed
     */
    public static Document parse(File configPath) throws ParserConfigurationException, IOException, SAXException {
        // Create a DocumentBuilder and parse the file
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var builder = factory.newDocumentBuilder();
        return builder.parse(configPath);
    }

    public static Document parse(String s) throws ParserConfigurationException, IOException, SAXException {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(XMLLoader.class.getClassLoader().getResourceAsStream(s)));
    }

    /**
     * Serializes an object into an XML file. The object must have a getter for each property that should be serialized.
     * @param configPath Path to the XML file
     * @param object Object to serialize
     * @param <T> Type of the object
     * @throws IOException If the file cannot be written
     * @throws IllegalAccessException If the object cannot be accessed
     * @throws InvocationTargetException If the object cannot be accessed
     */
    public static <T extends Serialisable> void serialize(T object) throws IOException, IllegalAccessException, InvocationTargetException {
        // Get all properties of the object by inspecting the methods and only keeping the getter methods
        String f = object.getPath();
        FileWriter configPath = new FileWriter(FilenameUtils.concat(f, object.getName() + ".xml"));

        // Create the XML string
        // StringBuilder xmlProperties = new StringBuilder("<" + object.getClass().getSimpleName() + " ");
        // for (var property: properties) {
        //     var name = property.getName();
        //     var value = String.valueOf(property.invoke(object));

        //     // remove "get" from name, and capitalize first letter
        //     name = name.substring(3);
        //     name = name.substring(0, 1).toUpperCase() + name.substring(1);

        //     xmlProperties.append(name).append("=\"").append(value).append("\" ");
        // }
        // xmlProperties.append("/>");

        // // Write the XML string to the file
        // var output = String.format("""
        //         <?xml version="1.0" encoding="UTF-8" ?>
        //         <root>
        //             %s
        //         </root>
        //         """, xmlProperties.toString());

        configPath.write(object.toXML());
        configPath.close();
    }
}
