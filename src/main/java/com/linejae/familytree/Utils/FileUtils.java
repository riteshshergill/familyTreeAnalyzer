package com.linejae.familytree.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linejae.familytree.models.Root;

import java.io.File;

public class FileUtils {

    public Root loadJsonFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Root familyTreeData = mapper.readValue(new File("src/main/resources/familytree.json"), Root.class);
        return familyTreeData;
    }
}
