package com.linejae.familytree.Utils;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.CacheManagerService;
import com.linejae.familytree.services.LineageServices;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataLoader {

    /**
     * Load all data for a lineage for further processing
     * @param lineageData
     * @return The processed lineage data stored in CacheManagerService object
     * @throws Exception
     */
    public CacheManagerService loadData(Root lineageData) throws Exception {
        CacheManagerService dataManager = new CacheManagerService();
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
            //the root node will just contain the family tree name
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
            cacheChildren(lineageData.getLineage().getMembers(), dataManager.getGraph(), startingNode, dataManager);

        }
        return dataManager;
    }

    /**
     * Print individual family lines and mark the family line that was shortest and longest
     * @param cacheManagerService
     * @return get the lineage from parent to leaf child
     * @throws Exception
     */
    public List<StringBuilder> getLineage(CacheManagerService cacheManagerService)
            throws Exception{
        LineageServices lineageServices = new LineageServices(cacheManagerService);
        return lineageServices.getLineage();
    }

    /**
     * Print all family members and their age in sorted order
     * @param sortOrder
     * @param cacheManagerService
     * @return all family numbers and their ages
     * @throws Exception
     */
    public List<StringBuilder> printByAge(String sortOrder, CacheManagerService cacheManagerService) throws Exception {
        LineageServices lineageServices = new LineageServices(cacheManagerService);
        List<Node> allNodes = lineageServices.getAllSortedNodes(sortOrder);
        List<StringBuilder> returnList = new ArrayList<>();
        for (Node node : allNodes) {
            StringBuilder ageWiseMember = new StringBuilder();
            ageWiseMember.append(node.getIdentifier() + " aged " + (Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear())));
            returnList.add(ageWiseMember);
        }
        return returnList;
    }

    /**
     * Find the range of period this lineage was active
     * @param cacheManagerService
     * @return active range
     * @throws Exception
     */
    public String getLineageRange(CacheManagerService cacheManagerService) throws Exception {
        LineageServices lineageServices = new LineageServices(cacheManagerService);
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

    /**
     * Find mean age for this lineage
     * @param cacheManagerService
     * @return The mean age
     * @throws Exception
     */
    public String getMeanAge(CacheManagerService cacheManagerService) throws Exception {
        LineageServices lineageServices = new LineageServices(cacheManagerService);
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

    /**
     * Find the median age for this lineage
     * @param cacheManagerService
     * @return The median age
     * @throws Exception
     */
    public String getMedianAge(CacheManagerService cacheManagerService) throws Exception {
        LineageServices lineageServices = new LineageServices(cacheManagerService);
        return "Median age is: " + lineageServices.getMedianAge();
    }

    /**
     * Group and print middle 50% of members (name and age) of this lineage using IQR (Interquartile Range)
     * @param cacheManagerService
     * @return The interquartile range members
     * @throws Exception
     */
    public List<StringBuilder> getInterQuartileAge(CacheManagerService cacheManagerService) throws Exception {
        LineageServices lineageServices = new LineageServices(cacheManagerService);
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

    /**
     * Who lived longest (name and age) in this lineage?  Who died the youngest (name and age)?
     * @param dataManager
     * @return Return longest and shortest living persons
     * @throws Exception
     */
    public List<StringBuilder> getLongestAndShortestLiving(CacheManagerService dataManager) throws Exception {
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

    private void cacheChildren(ArrayList<Member> members, GraphUtil graphUtil, Node parent, CacheManagerService dataManager) {
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
