package com.linejae.familytree.controllers;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.Utils.LineageComputationUtils;
import com.linejae.familytree.Utils.TreeUtils;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.services.CacheManagerService;
import com.linejae.familytree.services.LineageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This controller exposes all the computation methods for the Lineage
 */
@RestController
public class FamilyTreeEvaluationController {

    @Autowired
    private CacheManagerService cacheManagerService;

    @Autowired
    private LineageServices lineageServices;

    @GetMapping("/familyTree/findLongestShortest")
    public List<StringBuilder> getLineage()
        throws Exception{
        return lineageServices.getLineage();
    }

    @GetMapping("/familyTree/printSorted/{sortOrder}")
    public List<StringBuilder> printByAge(@PathVariable String sortOrder) {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = lineageServices.getAllSortedNodes(sortOrder);
        List<StringBuilder> returnList = new ArrayList<>();
        for (Node node : allNodes) {
            StringBuilder ageWiseMember = new StringBuilder();
            ageWiseMember.append(node.getIdentifier() + " aged " + (Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear())));
            returnList.add(ageWiseMember);
        }
        return returnList;
    }

    @GetMapping("/familyTree/lineageRange")
    public String getLineageRange() {
        List<Node> allNodes = lineageServices.getAllGraphNodes();
        final AtomicInteger minBirthYear = new AtomicInteger(0);
        final AtomicInteger maxDeathYear = new AtomicInteger(3999);
        lineageServices.getLineageRange(allNodes, minBirthYear, maxDeathYear);
        StringBuilder result = new StringBuilder();
        result.append("Alive from " + minBirthYear.get() + " to " + maxDeathYear.get());
        return result.toString();
    }

    @GetMapping("/familyTree/meanAge")
    public String getMeanAge() {
        List<Node> allNodes = lineageServices.getAllGraphNodes();
        final AtomicInteger meanAge = new AtomicInteger(0);
        lineageServices.getMeanAge(allNodes, meanAge);
        StringBuilder result = new StringBuilder();
        result.append("Mean age " + meanAge.get()/(allNodes.size()-1));
        return result.toString();
    }

    @GetMapping("/familyTree/getMedianAge")
    public String getMedianAge() {
        return "Median age is: " + lineageServices.getMedianAge();
    }

    @GetMapping("/familyTree/getInterQuartileAge")
    public void getInterQuartileAge() {
        Integer[] quartileIndexes = lineageServices.getInterquartileRange();
        List<Node> allNodes = lineageServices.getAllGraphNodes();

        for(int i = quartileIndexes[0]; i <= quartileIndexes[1]; i++) {
            System.out.println("Name: " + ((Member)allNodes.get(i).getData()).getName() + " Age: " + deriveAge(allNodes.get(i)));
        }
    }

    @GetMapping("/familyTree/getLongestShortestLiving")
    public List<StringBuilder> getLongestAndShortestLiving() {
        List<StringBuilder> resultList = new ArrayList<>();
        StringBuilder longestLiving = new StringBuilder();
        StringBuilder shortestLiving = new StringBuilder();
        longestLiving.append("Longest living: " + ((Member)cacheManagerService.getLongestLiving().getData()).toString());
        shortestLiving.append("Shortest living: " + ((Member)cacheManagerService.getShortestLiving().getData()).toString());
        resultList.add(longestLiving);
        resultList.add(shortestLiving);
        return resultList;
    }


    private Integer deriveAge(Node data) {
        return (Integer.parseInt(((Member)data.getData()).getDeathYear()) - Integer.parseInt(((Member)data.getData()).getBirthYear()));
    }

    private void getLongestPath(List<Member> members, Integer longCount, LinkedList<String> longestLineage) {
        if(members == null) {
            return;
        }
        for(Member eachMember : members) {
            if(eachMember.getMembers() != null) {
                longCount++;
                longestLineage.add(eachMember.toString());
                getLongestPath(eachMember.getMembers(), longCount, longestLineage);
            } else {
                break;
            }
        }
    }
}
