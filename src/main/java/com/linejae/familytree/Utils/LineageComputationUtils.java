package com.linejae.familytree.Utils;

import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Member;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * Validate that a member has all the required attributes
     * @param member
     * @throws Exception
     */
    public static void validateMember(Member member) throws Exception {
        if(member.getName() == null || member.getBirthYear() == null || member.getDeathYear() == null
                || StringUtils.isEmpty(member.getName()) || StringUtils.isEmpty(member.getBirthYear())
                || StringUtils.isEmpty(member.getDeathYear())) {
            throw new Exception("Member must have a name, birth year and death year");
        }
        int deathYear = 0;
        int birthYear = 0;
        try {
            deathYear = Integer.parseInt(member.getDeathYear());
            birthYear = Integer.parseInt(member.getBirthYear());
        } catch(NumberFormatException e) {
            throw new Exception("Error parsing the birth or death year value!");
        }
        if(deathYear < birthYear) {
            throw new Exception("Birth year must come before Death year");
        }

    }

    //utility methods to parse the year data and calculate age
    public static Integer deriveBirthYear(Node data) {
        return Integer.parseInt(((Member)data.getData()).getBirthYear());
    }

    public static Integer deriveDeathYear(Node data) {
        return Integer.parseInt(((Member)data.getData()).getDeathYear());
    }

    public static Integer deriveAge(Node data) {
        return (Integer.parseInt(((Member)data.getData()).getDeathYear()) - Integer.parseInt(((Member)data.getData()).getBirthYear()));
    }
}
