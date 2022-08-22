package com.linejae.familytree.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linejae.familytree.models.Report;
import com.linejae.familytree.models.Root;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations
 */
public class FileUtils {

    /**
     * Write the results for processing a lineage
     * @param report
     * @throws Exception
     */
    public static void generateReport(Report report) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("C:/temp/familytreedata/" + report.getFamilyTreeName() + ".json"), report);
    }

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

    /**
     * Write a randomly generated lineage
     * @param root
     * @param fileName
     * @throws Exception
     */
    public static void writeRandomFile(Root root, String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("src/main/resources/" + fileName), root);
    }

    /**
     * Method to load data for multiple files
     * @return All the lineages read from the files
     * @throws Exception
     */
    public static List<Root> loadAllFiles() throws Exception {
        File file = new File("src/main/resources");
        ObjectMapper mapper = new ObjectMapper();
        List<Root> allDataList = new ArrayList<>();
        String[] pathNames = file.list();
        for(String path: pathNames) {
            if(path.contains("json")) {
                Root familyTreeData = mapper.readValue(new File("src/main/resources/" + path), Root.class);
                allDataList.add(familyTreeData);
            }

        }
        return allDataList;
    }
}
