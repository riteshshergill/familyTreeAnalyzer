package com.linejae.familytree.services;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

public class DataLoadingServiceTest {

    DataLoadingService loadingService = new DataLoadingService();

    private CacheManagerService dataManager;

    private LineageServices lineageServices;

    GraphUtil graphUtil = new GraphUtil();

    @Before
    public void setUp() {
        this.dataManager = Mockito.mock(CacheManagerService.class);
        this.lineageServices = Mockito.mock(LineageServices.class);
        ReflectionTestUtils.setField(loadingService, "dataManager", this.dataManager);
        ReflectionTestUtils.setField(loadingService, "lineageServices", this.lineageServices);
    }

    @Test
    public void testLoadData() {
        try {
            CacheManagerService result = loadTestData();
            assert(result != null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private CacheManagerService loadTestData() throws Exception {
        Root lineageData = FileUtils.loadJsonFile("familytree.json");
        Mockito.when(dataManager.getGraph()).thenReturn(graphUtil);
        CacheManagerService result = loadingService.loadData(lineageData);
        return result;
    }

    @Test
    public void testOperations() {
        try {
            List<Node> testDataList = new ArrayList<>();
            Member member1 = new Member();
            member1.setName("Parent1");
            member1.setBirthYear("1982");
            member1.setDeathYear("2001");
            Node node1 = new Node("Parent1", member1);
            Member member2 = new Member();
            member2.setName("Child1");
            member2.setBirthYear("1995");
            member2.setDeathYear("2008");
            Node node2 = new Node("Child1", member2);
            testDataList.add(node1);
            testDataList.add(node2);
            Mockito.when(lineageServices.getAllSortedNodes(any())).thenReturn(testDataList);
            List<StringBuilder> result = loadingService.printByAge("ASC");
            assert(result!=null && !result.isEmpty());
            Mockito.when(lineageServices.getAllGraphNodes()).thenReturn(testDataList);
            String lr = loadingService.getLineageRange();
            assert(lr!=null && !lr.isEmpty());
            String ma = loadingService.getMeanAge();
            assert(ma != null);
            Mockito.when(lineageServices.getMedianAge()).thenReturn(30);
            String medianAge = loadingService.getMedianAge();
            assert(medianAge != null);
            Integer[] iqr = {0,1};
            Mockito.when(lineageServices.getInterquartileRange()).thenReturn(iqr);
            List<StringBuilder> iqrResult = loadingService.getInterQuartileAge();
            assert(iqrResult!=null && iqrResult.size() == 2);
            Mockito.when(dataManager.getLongestLiving()).thenReturn(node1);
            Mockito.when(dataManager.getShortestLiving()).thenReturn(node2);
            List<StringBuilder> lsl = loadingService.getLongestAndShortestLiving();
            assert(lsl!=null && lsl.size() == 2);
        } catch (Exception e) {
            fail(e.getMessage());
        }


    }
}

