package com.linejae.familytree.controllers;

import com.linejae.familytree.Utils.DataLoader;
import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.Utils.LineageComputationUtils;
import com.linejae.familytree.models.Report;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.BatchProcessingService;
import com.linejae.familytree.services.CacheManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MultiLoadController {

    @Autowired
    private CacheManagerService cms;

    @Autowired
    private BatchProcessingService batchProcessingService;

    /**
     * Load lineage for the specified file names from the resources folder
     * @param fileNames File names to be loaded
     * @return Loaded lineages
     * @throws Exception
     */
    @GetMapping("/familyTree/loadMultiple/{fileNames}")
    public List<Root> loadFamilyTreeData(@PathVariable String[] fileNames) throws Exception {
        List<Root> allData = new ArrayList<>();
        for(String fileName: fileNames) {
            allData.add(FileUtils.loadJsonFile(fileName));
        }
        DataLoader dataLoadingService = new DataLoader();
        for(Root data : allData) {
            CacheManagerService cacheManagerService = dataLoadingService.loadData(data);
            Report reportObject = new Report();
            reportObject.setFamilyTreeName(data.getLineage().getFamilyTree());
            reportObject.setLineage(dataLoadingService.getLineage(cacheManagerService));
            reportObject.setAgeList(dataLoadingService.printByAge("ASC", cacheManagerService));
            reportObject.setLineageRange(dataLoadingService.getLineageRange(cacheManagerService));
            reportObject.setMeanAge(dataLoadingService.getMeanAge(cacheManagerService));
            reportObject.setMedianAge(dataLoadingService.getMedianAge(cacheManagerService));
            reportObject.setInterQuartileRange(dataLoadingService.getInterQuartileAge(cacheManagerService));
            reportObject.setLongestAndShortestLiving(dataLoadingService.getLongestAndShortestLiving(cacheManagerService));
            cms.getMultiLineageMap().put(data.getLineage().getFamilyTree(), reportObject);
        }

        return allData;
    }

    /**
     * Generate reports for multiple filenames
     * @param threadPoolSize The parallel thread pool size
     * @param fileNames File names to generate reports for
     * @return Status of generation
     * @throws Exception
     */
    @GetMapping("/familyTree/generateMultiple/{threadPoolSize}/{fileNames}")
    public String generateMultipleFamilyTreeDataReports(@PathVariable Integer threadPoolSize, @PathVariable String[] fileNames) throws Exception {
        List<Root> allData = new ArrayList<>();
        for(String fileName: fileNames) {
            allData.add(FileUtils.loadJsonFile(fileName));
        }
        String generationStatus = "Succesfully generated in project resources folder";
        //files will be written to src/main/resources
        try {
            batchProcessingService.processData(allData, threadPoolSize);
        } catch(Exception e) {
            System.out.println("Error generating the reports!");
            System.out.println(e.getMessage());
            generationStatus = "Failed to generate files";
        }
        return generationStatus;
    }
}
