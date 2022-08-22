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

@Service
public class BatchProcessingService {

    public void processData(List<Root> allData) throws Exception {
        List<Tasklet> allTasks = new ArrayList<>();
        for(Root data: allData) {
            Tasklet tasklet = new Tasklet();
            tasklet.setFamilyTreeData(data);
            allTasks.add(tasklet);
        }
        ExecutorService executor = Executors.newFixedThreadPool(allData.size());
        List<Future<Report>> results = executor.invokeAll(allTasks);
        for(Future<Report> resultData : results) {
            Report report = resultData.get();
            FileUtils.generateReport(report);
        }

    }
}
