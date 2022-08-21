package com.linejae.familytree.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linejae.familytree.models.Root;

import java.io.File;

/**
 * Utility class for file operations
 */
public class FileUtils {

    /**
     * Method to laod the json data from the given file
     * @param fileName File to load
     * @return Root of the btree
     * @throws Exception
     */
    public Root loadJsonFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Root familyTreeData = mapper.readValue(new File("src/main/resources/" + fileName), Root.class);
        return familyTreeData;
    }

    public static void writeRandomFile(Root root, String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("src/main/resources/" + fileName), root);
    }
}
