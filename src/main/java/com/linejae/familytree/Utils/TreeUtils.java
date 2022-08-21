package com.linejae.familytree.Utils;

import com.gcache.graph.model.Node;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeUtils {

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
