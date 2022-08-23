package com.linejae.familytree.services;

import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.models.Report;
import com.linejae.familytree.models.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Service to generate reports for multiple files
 */
@Service
public class BatchProcessingService {

    /**
     * Multithreaded batch process to process several files at once
     * @param allData All the lineage data to process in parallel
     * @throws Exception
     */
    public void processData(List<Root> allData, Integer threadPoolSize) throws Exception {
        List<Tasklet> allTasks = new ArrayList<>();
        for(Root data: allData) {
            Tasklet tasklet = new Tasklet();
            tasklet.setFamilyTreeData(data);
            allTasks.add(tasklet);
        }
        //create a fixed thread pool of the same size as the number of lineages
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<Report>> results = executor.invokeAll(allTasks);
        for(Future<Report> resultData : results) {
            Report report = resultData.get();
            FileUtils.generateReport(report);
        }

    }
}
