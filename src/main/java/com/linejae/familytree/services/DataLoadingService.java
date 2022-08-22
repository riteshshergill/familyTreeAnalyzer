package com.linejae.familytree.services;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.Utils.LineageComputationUtils;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DataLoadingService {

    @Autowired
    private CacheManagerService dataManager;
    @Autowired
    private LineageServices lineageServices;

    public CacheManagerService loadData(Root lineageData) throws Exception {
        dataManager.setGraph(new GraphUtil());
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
                dataManager.getGraph().addRelationship(startingNode, rootchild);
            }
            dataManager.setRootNode(startingNode);
            //Add the depth for the tree even as we construct it
            dataManager.setDepth(dataManager.getDepth() + 1);
            //send the children to be added to the graph recursively
            cacheChildren(lineageData.getLineage().getMembers(), dataManager.getGraph(), startingNode);

        }
        return dataManager;
    }

    public List<StringBuilder> getLineage()
            throws Exception{
        return lineageServices.getLineage();
    }

    public List<StringBuilder> printByAge(String sortOrder) throws Exception {
        List<Node> allNodes = lineageServices.getAllSortedNodes(sortOrder);
        List<StringBuilder> returnList = new ArrayList<>();
        for (Node node : allNodes) {
            StringBuilder ageWiseMember = new StringBuilder();
            ageWiseMember.append(node.getIdentifier() + " aged " + (Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear())));
            returnList.add(ageWiseMember);
        }
        return returnList;
    }

    public String getLineageRange() throws Exception {
        List<Node> allNodes = lineageServices.getAllGraphNodes();
        if(allNodes == null || allNodes.isEmpty()) {
            throw new Exception("Cannot get lineage for an empty tree");
        }
        final AtomicInteger minBirthYear = new AtomicInteger(0);
        final AtomicInteger maxDeathYear = new AtomicInteger(3999);
        lineageServices.getLineageRange(allNodes, minBirthYear, maxDeathYear);
        StringBuilder result = new StringBuilder();
        result.append("Alive from " + minBirthYear.get() + " to " + maxDeathYear.get());
        return result.toString();
    }

    public String getMeanAge() throws Exception {
        List<Node> allNodes = lineageServices.getAllGraphNodes();
        if(allNodes == null || allNodes.isEmpty()) {
            throw new Exception("Cannot get mean age for an empty tree");
        }
        final AtomicInteger meanAge = new AtomicInteger(0);
        lineageServices.getMeanAge(allNodes, meanAge);
        StringBuilder result = new StringBuilder();
        result.append("Mean age " + meanAge.get()/(allNodes.size()-1));
        return result.toString();
    }

    public String getMedianAge() throws Exception {
        return "Median age is: " + lineageServices.getMedianAge();
    }

    public List<StringBuilder> getInterQuartileAge() throws Exception {
        Integer[] quartileIndexes = lineageServices.getInterquartileRange();
        List<Node> allNodes = lineageServices.getAllGraphNodes();
        if(allNodes == null || allNodes.isEmpty()) {
            throw new Exception("Cannot get inter quartile for an empty tree");
        }
        List<StringBuilder> returnList = new ArrayList<>();
        for(int i = quartileIndexes[0]; i <= quartileIndexes[1]; i++) {
            StringBuilder retStr = new StringBuilder();
            retStr.append("Name: " + ((Member)allNodes.get(i).getData()).getName() + " Age: " + deriveAge(allNodes.get(i)));
            returnList.add(retStr);
        }
        return returnList;
    }

    public List<StringBuilder> getLongestAndShortestLiving() throws Exception {
        List<StringBuilder> resultList = new ArrayList<>();
        StringBuilder longestLiving = new StringBuilder();
        StringBuilder shortestLiving = new StringBuilder();
        if(dataManager.getLongestLiving() == null) {
            throw new Exception("Please calculate the median age first for longest living to be available.");
        }
        if(dataManager.getShortestLiving() == null) {
            throw new Exception("Please calculate the median age first for shortest living to be available.");
        }
        longestLiving.append("Longest living: " + ((Member) dataManager.getLongestLiving().getData()).toString());
        shortestLiving.append("Shortest living: " + ((Member) dataManager.getShortestLiving().getData()).toString());
        resultList.add(longestLiving);
        resultList.add(shortestLiving);
        return resultList;
    }

    private Integer deriveAge(Node data) {
        return (Integer.parseInt(((Member)data.getData()).getDeathYear()) - Integer.parseInt(((Member)data.getData()).getBirthYear()));
    }

    private void cacheChildren(ArrayList<Member> members, GraphUtil graphUtil, Node parent) {
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
                    cacheChildren(memberChild.getMembers(), graphUtil, memberNode);
                }
            } else {
                Node leafNode = new Node(memberObject.toString(), memberObject, parent);
                graphUtil.addRelationship(leafNode, null);
                dataManager.getLeafNodes().add(leafNode);
            }
        }));
    }
}
