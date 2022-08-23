package com.linejae.familytree.Utils;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.CacheManagerService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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

        StringBuilder longAppendString = new StringBuilder();

        StringBuilder shortAppendString = new StringBuilder();

        antecedants.forEach((antecedantList -> {
            StringBuilder lineage = new StringBuilder();
            antecedantList.stream().forEach(nodeValue -> {
                lineage.append(nodeValue + "<-");
            });

            if(antecedantList.size() == longest.get() && longAppendString.isEmpty()) {
                longAppendString.append("##### Longest");
                lineage.append(longAppendString);
            }
            if(antecedantList.size() == shortest.get() && shortAppendString.isEmpty()) {
                shortAppendString.append("##### Shortest");
                lineage.append(shortAppendString);
            }

            if(lineage.substring(lineage.length() - 2, lineage.length()).equals("<-")) {
                lineage.replace(lineage.length() - 2, lineage.length(), "");
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

    public static Root loadData(String fileName, CacheManagerService cacheManagerService) throws Exception {
        Root lineageData = FileUtils.loadJsonFile(fileName);
        cacheManagerService.setGraph(new GraphUtil());
        //caching all relationships in the graph cache
        if(lineageData.getLineage() != null) {
            if(lineageData.getLineage().getFamilyTree() == null
                    || StringUtils.isEmpty(lineageData.getLineage().getFamilyTree())) {
                throw new Exception("Family tree name must be present!");
            }
            if(lineageData.getLineage().getMembers() == null) {
                throw new Exception("Family tree must have members!");
            }
            Node startingNode = new Node(lineageData.getLineage().getFamilyTree(), lineageData.getLineage().getFamilyTree());

            //add the root to the graph first then send its children for addition
            //recursively
            for(Member rootchildren: lineageData.getLineage().getMembers()) {
                try {
                    LineageComputationUtils.validateMember(rootchildren);
                } catch (Exception e) {
                    System.out.println("Invalid member found, skipping..");
                    continue;
                }
                Node rootchild = new Node(rootchildren.toString(), rootchildren);
                cacheManagerService.getGraph().addRelationship(startingNode, rootchild);
            }
            cacheManagerService.setRootNode(startingNode);
            //Add the depth for the tree even as we construct it
            cacheManagerService.setDepth(cacheManagerService.getDepth() + 1);
            //send the children to be added to the graph recursively
            cacheChildren(lineageData.getLineage().getMembers(), cacheManagerService.getGraph(), startingNode, cacheManagerService);

        }
        return lineageData;
    }

    //this method will add all the nodes to a JGrapht directed graph
    private static void cacheChildren(ArrayList<Member> members, GraphUtil graphUtil, Node parent, CacheManagerService cacheManagerService) {
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
                cacheManagerService.setDepth(cacheManagerService.getDepth() + 1);
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
                    cacheChildren(memberChild.getMembers(), graphUtil, memberNode, cacheManagerService);
                }
            } else {
                Node leafNode = new Node(memberObject.toString(), memberObject, parent);
                graphUtil.addRelationship(leafNode, null);
                cacheManagerService.getLeafNodes().add(leafNode);
            }
        }));
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
