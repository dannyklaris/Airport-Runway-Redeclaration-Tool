package com.group20seq.runway_redeclaration.Controllers;

import com.group20seq.runway_redeclaration.Configs.Serialisable;
import com.group20seq.runway_redeclaration.Configs.XMLLoader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Config implements Serialisable {
    private static Config c;
    private Config() {
    }

    public static Config getConfig() {
        if (c == null)
            c = new Config();
        return c;
    }

    public String getName() {
        return "config";
    }

    public static HashMap<Class<? extends Serialisable>, File> fromXML() {
        HashMap<Class<? extends Serialisable>, File> hs = new HashMap<>();
        try {

            File configPath = new File(Config.class.getProtectionDomain().getCodeSource().getLocation()
                                       .toURI()).getParentFile();
            File f = new File(FilenameUtils.concat(configPath.toString(), "config.xml"));
            var doc = XMLLoader.parse(f);
            NodeList ns = doc.getElementsByTagName("Entry");
            for (int i = 0; i < ns.getLength(); i++) {
                Node n = ns.item(i);
                hs.put((Class<? extends Serialisable>)Class.forName(n.getAttributes().getNamedItem("Key").getNodeValue()),
                       new File(n.getAttributes().getNamedItem("Value").getNodeValue()));
            }
            FilesController.getFilesController().setHashMap(hs);
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
        }
        return hs;
    }

    public String toXML() {
        HashMap<Class<? extends Serialisable>, File> files = FilesController.getFilesController().getHashMap();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xml.append("<root>\n");
        for (Map.Entry<Class<? extends Serialisable>, File> f : files.entrySet()) {
            xml.append("<Entry Key=\"" + f.getKey().toString().split(" ")[1] +
                       "\" Value=\"" + f.getValue().toString() +
                    "\"/>\n");
        }
        xml.append("</root>");
        return xml.toString();
    }

    public String getPath() {
        try {
            return new File(Config.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent();
        } catch (Exception ignored) {
            return "";
        }
    }
}
