package com.linejae.familytree.Utils;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.services.CacheManagerService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class for tree or graph related operations
 */
public class TreeUtils {

    /**
     * Given a leaf node, find its hierarchy upwards
     * @param leaf The leaf node for getting the hierarchy
     * @param longest Longest hierarchy indicator
     * @param shortest Shortest hierarchy indicator
     * @return The hierarchy in a list
     */
    public static LinkedList<String> findParents(Node leaf, AtomicInteger longest, AtomicInteger shortest) {
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

    /**
     * Recursive function to add the members into a graph
     * @param members All the members added recursively
     * @param graphUtil Wrapper utility for the graph
     * @param parent Parent object
     * @param dataManager To store the results and pass to other services
     */
    public static void cacheChildren(ArrayList<Member> members, GraphUtil graphUtil, Node parent, CacheManagerService dataManager) {
        if(members == null) {
            return;
        }
        members.forEach((memberObject -> {

            try {
                LineageComputationUtils.validateMember(memberObject);
            } catch (Exception e) {
                System.out.println("Invalid member found, skipping..");
                return;
            }

            if(memberObject.getMembers() != null) {
                dataManager.setDepth(dataManager.getDepth() + 1);
                for(Member memberChild: memberObject.getMembers()) {
                    try {
                        LineageComputationUtils.validateMember(memberChild);
                    } catch (Exception e) {
                        System.out.println("Invalid member found, skipping..");
                        continue;
                    }
                    Node parentNode = new Node(memberObject.toString(), memberObject, parent);
                    Node memberNode = new Node(memberChild.toString(), memberChild, parentNode);
                    graphUtil.addRelationship(parentNode, memberNode);
                    cacheChildren(memberChild.getMembers(), graphUtil, memberNode, dataManager);
                }
            } else {
                Node leafNode = new Node(memberObject.toString(), memberObject, parent);
                graphUtil.addRelationship(leafNode, null);
                dataManager.getLeafNodes().add(leafNode);
            }
        }));
    }
}
