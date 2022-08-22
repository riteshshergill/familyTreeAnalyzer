package com.linejae.familytree.services;

import com.linejae.familytree.Utils.DataLoader;
import com.linejae.familytree.models.Report;
import com.linejae.familytree.models.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

public class Tasklet implements Callable<Report> {
    Root familyTreeData;

    @Override
    public Report call() throws Exception {
        if(familyTreeData == null) {
            return null;
        }
        Report reportObject = new Report();
        try {
            DataLoader dataLoadingService = new DataLoader();
            CacheManagerService cacheManagerService = dataLoadingService.loadData(familyTreeData);
            reportObject.setFamilyTreeName(familyTreeData.getLineage().getFamilyTree());
            reportObject.setLineage(dataLoadingService.getLineage(cacheManagerService));
            reportObject.setAgeList(dataLoadingService.printByAge("ASC", cacheManagerService));
            reportObject.setLineageRange(dataLoadingService.getLineageRange(cacheManagerService));
            reportObject.setMeanAge(dataLoadingService.getMeanAge(cacheManagerService));
            reportObject.setMedianAge(dataLoadingService.getMedianAge(cacheManagerService));
            reportObject.setInterQuartileRange(dataLoadingService.getInterQuartileAge(cacheManagerService));
            reportObject.setLongestAndShortestLiving(dataLoadingService.getLongestAndShortestLiving(cacheManagerService));
        } catch (Exception e) {
            System.out.println("Error loading family tree: " + familyTreeData.getLineage().getFamilyTree());
            System.out.println(e.getMessage());
        }

        return reportObject;
    }

    public Root getFamilyTreeData() {
        return familyTreeData;
    }

    public void setFamilyTreeData(Root familyTreeData) {
        this.familyTreeData = familyTreeData;
    }
}
