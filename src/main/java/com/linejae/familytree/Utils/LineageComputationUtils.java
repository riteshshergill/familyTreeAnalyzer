package com.linejae.familytree.Utils;

import java.util.LinkedList;
import java.util.List;

public class LineageComputationUtils {

    public static void constructLineageResult(List<LinkedList<String>> antecedants, List<StringBuilder> finalLineages) {
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
