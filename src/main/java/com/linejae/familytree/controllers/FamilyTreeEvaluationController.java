package com.linejae.familytree.controllers;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.services.CacheManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This controller exposes all the computation methods
 */
@RestController
public class FamilyTreeEvaluationController {

    @Autowired
    private CacheManagerService cacheManagerService;

    private LinkedList<String> findParents(Node leaf, AtomicInteger longest, AtomicInteger shortest) {
        LinkedList<String> antecedantsList = new LinkedList<>();
        Node parentNode = leaf.getParentNode();
        if(antecedantsList.isEmpty()) {
            antecedantsList.add(leaf.getIdentifier());
        }
        while(parentNode != null) {
            antecedantsList.add(parentNode.getIdentifier());
            parentNode = parentNode.getParentNode();
        }
        if(antecedantsList.size() > longest.get()) {
            longest.set(antecedantsList.size());
        }
        if(antecedantsList.size() < shortest.get()) {
            shortest.set(antecedantsList.size());
        }
        return antecedantsList;
    }

    @GetMapping("/familyTree/findLongestShortest")
    public Integer getLineage()
        throws Exception{

        List<Node> leafNodes = cacheManagerService.getLeafNodes();
        final List<LinkedList<String>> antecedants = new ArrayList<>();
        final AtomicInteger longest = new AtomicInteger(0);
        final AtomicInteger shortest = new AtomicInteger(cacheManagerService.getDepth() + 4);
        leafNodes.stream().forEach((leaf) -> antecedants.add(findParents(leaf, longest, shortest)));

        antecedants.stream().forEach((antecedantList -> {
            System.out.println();
            final StringBuilder longAppendString = new StringBuilder();
            final StringBuilder shortAppendString = new StringBuilder();

            if(antecedantList.size() == longest.get()) {
                longAppendString.append("Longest");
            }
            if(antecedantList.size() == shortest.get()) {
                shortAppendString.append("Shortest");
            }
            antecedantList.stream().forEach(nodeValue -> {
                System.out.print(nodeValue + "<-");
            });
            System.out.print(antecedantList.size());
            if(!longAppendString.isEmpty()) {
                System.out.print("---" + longAppendString);
                longAppendString.delete(0, longAppendString.length());
            }
            if(!shortAppendString.isEmpty()) {
                System.out.print("---" + shortAppendString);
                shortAppendString.delete(0, longAppendString.length());
            }
        }));
        return antecedants.size();
    }

    @GetMapping("/familyTree/printSorted/{sortOrder}")
    public void printByAge(@PathVariable String sortOrder) {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
        allNodes.sort((node1, node2) -> {
            if(!(node1.getData() instanceof String) && !(node2.getData() instanceof String)) {
                int node1age = Integer.parseInt(((Member)node1.getData()).getDeathYear()) - Integer.parseInt(((Member)node1.getData()).getBirthYear());
                int node2age = Integer.parseInt(((Member)node2.getData()).getDeathYear()) - Integer.parseInt(((Member)node2.getData()).getBirthYear());
                if(sortOrder.equals("ASC")) {
                    return node1age - node2age;
                } else {
                    return node2age - node1age;
                }
            }
            return 0;
        });
        for (Node node : allNodes) {
            System.out.println(node.getIdentifier() + " aged " + (Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear())));
        }
    }

    @GetMapping("/familyTree/lineageRange")
    public void getLineageRange() {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
        final AtomicInteger minBirthYear = new AtomicInteger(3999);
        final AtomicInteger maxDeathYear = new AtomicInteger(0);
        allNodes.stream().forEach(node -> {
            if(!(node.getData() instanceof String)) {
                if((Integer.parseInt(((Member)node.getData()).getDeathYear()) > maxDeathYear.get())) {
                    maxDeathYear.set((Integer.parseInt(((Member)node.getData()).getDeathYear())));
                }
                if((Integer.parseInt(((Member)node.getData()).getBirthYear()) < minBirthYear.get())) {
                    minBirthYear.set((Integer.parseInt(((Member)node.getData()).getBirthYear())));
                }
            }
        });
        System.out.println("Alive from " + minBirthYear.get() + " to " + maxDeathYear.get());
    }

    @GetMapping("/familyTree/meanAge")
    public void getMeanAge() {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
        final AtomicInteger meanAge = new AtomicInteger(0);
        allNodes.stream().forEach(node -> {
            if(!(node.getData() instanceof String)) {
                meanAge.set(meanAge.get() + Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear()));
            }
        });
        System.out.println("Mean age " + meanAge.get()/(allNodes.size()-1));
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
