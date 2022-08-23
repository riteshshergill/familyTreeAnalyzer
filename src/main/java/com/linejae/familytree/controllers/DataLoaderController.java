package com.linejae.familytree.controllers;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.Utils.LineageComputationUtils;
import com.linejae.familytree.Utils.MockDataGeneratorUtil;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.CacheManagerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to load all members and cache their relationshsips
 */
@RestController
public class DataLoaderController {

    @Autowired
    private CacheManagerService cacheManagerService;

    /**
     *
     * @param fileName File from which to load the data
     * @return The Root object containing the hierarchy
     * @throws Exception
     */
    @GetMapping("/familyTree/loadData/{fileName}")
    public Root loadFamilyTreeData(@PathVariable String fileName) throws Exception {
        return LineageComputationUtils.loadData(fileName, cacheManagerService);
    }

    /**
     * Generate random lineage data
     */
    @GetMapping("/generateMockData/{numFiles}")
    public void generateMockData(@PathVariable Integer numFiles) {
        try {
            MockDataGeneratorUtil.generateMockData(numFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
