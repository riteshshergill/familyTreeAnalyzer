package com.linejae.familytree.services;

import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.models.Report;
import com.linejae.familytree.models.Root;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class BatchProcessingServiceTest {

    BatchProcessingService batchProcessingService = new BatchProcessingService();

    @Test
    public void testThis() {

        try {
            Root data = FileUtils.loadJsonFile("familytree.json");
            List<Root> allData = new ArrayList<>();
            allData.add(data);
            batchProcessingService.processData(allData, 5);
            Report result = FileUtils.loadReportFile("Some Family Tree.json");
            assert(result != null && result.getFamilyTreeName().equals("Some Family Tree") && result.getLineage()!= null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
