package com.linejae.familytree.controllers;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.CacheManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class FamilyTreeEvaluationController {

    @Autowired
    private CacheManagerService cacheManagerService;

    private LinkedList<String> findParents(Node leaf) {
        LinkedList<String> antecedantsList = new LinkedList<>();
        Node parentNode = leaf.getParentNode();
        if(antecedantsList.isEmpty()) {
            antecedantsList.add(leaf.getIdentifier());
        }
        while(parentNode != null) {
            antecedantsList.add(parentNode.getIdentifier());
            parentNode = parentNode.getParentNode();
        }
        return antecedantsList;
    }

    @GetMapping("/familyTree/findLongestShortest")
    public Integer getLongestLineage()
        throws Exception{

        List<Node> leafNodes = cacheManagerService.getLeafNodes();
        final List<LinkedList<String>> antecedants = new ArrayList<>();
        leafNodes.stream().forEach((leaf) -> antecedants.add(findParents(leaf)));
        antecedants.stream().forEach((antecedantList -> {
            System.out.println();
            antecedantList.stream().forEach(nodeValue -> {
                System.out.print(nodeValue + "<-");
            });
            System.out.print(antecedantList.size());
        }));
        return antecedants.size();

        /*List<LinkedList<String>> returnListOfLineage = new ArrayList<>();

        GraphUtil graphObj = cacheManagerService.getGraph();

        Node rootNode = cacheManagerService.getRootNode();

        if(rootNode == null) {
            throw new Exception("No tree found!");
        }

        LinkedList<String> shortestLineage = new LinkedList<>();
        LinkedList<String> longestLineage = new LinkedList<>();

        Integer shortCount = 0;
        Integer longCount = 0;

        getLongestPath((List<Member>) rootNode.getData(), longCount, longestLineage);

        StringBuilder longest = new StringBuilder();

        longestLineage.forEach(member -> {
            longest.append(member.toString());
        });

        return longest.toString();*/
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
