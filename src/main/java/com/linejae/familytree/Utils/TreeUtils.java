package com.linejae.familytree.Utils;

import com.gcache.graph.model.Node;

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
}
