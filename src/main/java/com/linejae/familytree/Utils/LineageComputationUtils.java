package com.linejae.familytree.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class for computations
 */
public class LineageComputationUtils {

    /**
     * Get the lineage
     * @param antecedants Parents for a node
     * @param finalLineages Resulting lineage
     * @param longest Longest lineage
     * @param shortest Shortest lineage
     */
    public static void constructLineageResult(List<LinkedList<String>> antecedants, List<StringBuilder> finalLineages, AtomicInteger longest, AtomicInteger shortest) {
        antecedants.stream().forEach((antecedantList -> {
            StringBuilder lineage = new StringBuilder();
            final StringBuilder longAppendString = new StringBuilder();
            final StringBuilder shortAppendString = new StringBuilder();

            if(antecedantList.size() == longest.get()) {
                longAppendString.append("Longest");
            }
            if(antecedantList.size() == shortest.get()) {
                shortAppendString.append("Shortest");
            }
            antecedantList.stream().forEach(nodeValue -> {
                lineage.append(nodeValue + "<-");
            });
            if(!longAppendString.isEmpty()) {
                lineage.append("---" + longAppendString);
                longAppendString.delete(0, longAppendString.length());
            }
            if(!shortAppendString.isEmpty()) {
                lineage.append("---" + shortAppendString);
                shortAppendString.delete(0, longAppendString.length());
            }
            finalLineages.add(lineage);
        }));
    }
}
