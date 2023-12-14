package com.group20seq.runway_redeclaration.Controllers;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import javafx.scene.control.Alert;

public class Controller
{
    /**
     * Get all items of a class from the resources folder - this is for when a file path isn't specified, to just use
     * the default resources folder.
     * @param c The class to get the items of.
     * @return A list of all the items of the class.
     * @param <U> The type of the class.
     */
    static public <U> List<U> getItems(Class<U> c) {
        try {
            // Construct the path to the resources folder and get the items.
            var uri = c.getResource("/" + c.getSimpleName() + "s/").toURI();
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath("/resources/" + c.getSimpleName() + "s/");
            } else {
                myPath = Paths.get(uri);
            }
            return getItems(c, new File(myPath.toString()));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    /**
     * Get all items of a class from a specified folder - this is for when a file path is specified. This is used for
     * loading in the user's custom items. The class must be given to so the output type is known.
     * @param c The class to get the items of.
     * @param folder The folder to get the items from.
     * @return A list of all the items of the class.
     * @param <U> The type of the class.
     */
    static public <U> List<U> getItems(Class<U> c, File folder) {
        try {
            // Get all the files in the folder, and map them to the objects through the desired classes fromXML method.
            var files = Arrays.stream(folder.listFiles());
            var objs = files.map(ff -> {
                try {
                    return (U)c.getDeclaredMethod("fromXML", File.class).invoke(null, ff);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });

            // Return the cast objects as a list.
            return objs.toList();
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
    }
}
