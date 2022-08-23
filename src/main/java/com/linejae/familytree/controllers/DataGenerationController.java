package com.linejae.familytree.controllers;

import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.BatchProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class DataGenerationController {

    @Autowired
    private BatchProcessingService batchProcessingService;

    /**
     * Generate lineage data automatically
     * @throws Exception
     */
    @GetMapping("/familyTree/generateData/{threadPoolSize}")
    public void generateReports(@PathVariable Integer threadPoolSize) throws Exception {
        List<Root> allData = FileUtils.loadAllFiles();
        batchProcessingService.processData(allData, threadPoolSize);
    }
}
