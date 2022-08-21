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
        final AtomicInteger minBirthYear = new AtomicInteger(3999);
        final AtomicInteger maxDeathYear = new AtomicInteger(0);
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
    public void getMedianAge() {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
        allNodes.sort((node1, node2) -> {
            if(!(node1.getData() instanceof String) && !(node2.getData() instanceof String)) {
                int node1age = Integer.parseInt(((Member)node1.getData()).getDeathYear()) - Integer.parseInt(((Member)node1.getData()).getBirthYear());
                int node2age = Integer.parseInt(((Member)node2.getData()).getDeathYear()) - Integer.parseInt(((Member)node2.getData()).getBirthYear());
                return node1age - node2age;

            }
            return 0;
        });
        cacheManagerService.setLongestLiving(allNodes.get(allNodes.size()-1));
        cacheManagerService.setShortestLiving(allNodes.get(0));
        Integer medianIndex = (allNodes.size()-1)/2;
        if((allNodes.size()-1)%2 == 0) {
            System.out.println("Median age: " + (deriveAge(allNodes.get(medianIndex)) + deriveAge(allNodes.get(medianIndex+1)))/2);
        } else {
            System.out.println("Median age: " + deriveAge(allNodes.get(medianIndex)));
        }

    }

    @GetMapping("/familyTree/getInterQuartileAge")
    public void getInterQuartileAge() {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
        allNodes.sort((node1, node2) -> {
            if(!(node1.getData() instanceof String) && !(node2.getData() instanceof String)) {
                int node1age = Integer.parseInt(((Member)node1.getData()).getDeathYear()) - Integer.parseInt(((Member)node1.getData()).getBirthYear());
                int node2age = Integer.parseInt(((Member)node2.getData()).getDeathYear()) - Integer.parseInt(((Member)node2.getData()).getBirthYear());
                return node1age - node2age;

            }
            return 0;
        });

        Integer medianIndex = (allNodes.size()-1)/2;

        Integer interQuartileStartIndex = 0;
        Integer interQuartileEndIndex = 0;

        if((allNodes.size()-1)%2 == 0) {
            interQuartileStartIndex = medianIndex/2;
            interQuartileEndIndex = (allNodes.size() + medianIndex)/2;
        } else {
            interQuartileStartIndex = (medianIndex/2) + 1;
            interQuartileEndIndex = (allNodes.size() + (medianIndex))/2 + 1;
        }

        for(int i = interQuartileStartIndex; i <= interQuartileEndIndex; i++) {
            System.out.println("Name: " + ((Member)allNodes.get(i).getData()).getName() + " Age: " + deriveAge(allNodes.get(i)));
        }
    }

    @GetMapping("/familyTree/getLongestShortestLiving")
    public void getLongestAndShortestLiving() {
        System.out.println("Longest living: " + ((Member)cacheManagerService.getLongestLiving().getData()).toString());
        System.out.println("Shortest living: " + ((Member)cacheManagerService.getShortestLiving().getData()).toString());
    }

    private Integer deriveBirthYear(Node data) {
        return Integer.parseInt(((Member)data.getData()).getBirthYear());
    }

    private Integer deriveDeathYear(Node data) {
        return Integer.parseInt(((Member)data.getData()).getDeathYear());
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
