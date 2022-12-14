package com.linejae.familytree.services;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.Utils.LineageComputationUtils;
import com.linejae.familytree.Utils.TreeUtils;
import com.linejae.familytree.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LineageServices {

    @Autowired
    private CacheManagerService cacheManagerService;

    public LineageServices() {

    }

    public LineageServices(CacheManagerService cacheManagerService) {
        this.cacheManagerService = cacheManagerService;
    }

    /**
     * Method to compute the family treelineage
     * @return The resulting lineage
     */
    public List<StringBuilder> getLineage() throws Exception {
        List<Node> allNodes = getAllSortedNodes("ASC");
        if(allNodes == null || allNodes.size() == 0) {
            throw new Exception("Tree doesn't have leaves cant evaluate Lineage");
        }
        final List<LinkedList<String>> antecedants = new ArrayList<>();
        AtomicInteger longest = new AtomicInteger(0);
        AtomicInteger shortest = new AtomicInteger(cacheManagerService.getDepth() + 4);
        allNodes.stream().forEach((node) -> antecedants.add(TreeUtils.findParents(node, longest, shortest)));
        List<StringBuilder> finalLineages = new ArrayList<>();
        LineageComputationUtils.constructLineageResult(antecedants, finalLineages, longest, shortest);
        return finalLineages;
    }

    /**
     * Method to get nodes sorted in provided order
     * @param sortOrder ASC or DESC
     * @return Nodes in sorted order of age
     */
    public List<Node> getAllSortedNodes(String sortOrder) throws Exception {
        List<Node> allNodes = getAllGraphNodes();
        if(allNodes == null || allNodes.isEmpty()) {
            throw new Exception("Cannot sort an empty tree");
        }
        allNodes.sort((node1, node2) -> {
            if(!(node1.getData() instanceof String) && !(node2.getData() instanceof String)) {
                int node1age = Integer.parseInt(((Member)node1.getData()).getDeathYear()) - Integer.parseInt(((Member)node1.getData()).getBirthYear());
                int node2age = Integer.parseInt(((Member)node2.getData()).getDeathYear()) - Integer.parseInt(((Member)node2.getData()).getBirthYear());
                if(sortOrder.equals("ASC")) {
                    return node1age - node2age;
                } else {
                    return node2age - node1age;
                }
            }
            return 0;
        });
        return allNodes;
    }

    /**
     * Method to get the range of the lineage from first birth year to last death year
     * @param allNodes
     * @param maxDeathYear Max death year for any member
     * @param minBirthYear Min birth year for any member
     */
    public void getLineageRange(List<Node> allNodes, AtomicInteger minBirthYear, AtomicInteger maxDeathYear) {
        allNodes.forEach(node -> {
            if(!(node.getData() instanceof String)) {
                int maximumDeathYear = LineageComputationUtils.deriveDeathYear(node);
                int minimumBirthYear = LineageComputationUtils.deriveBirthYear(node);
                if(maximumDeathYear > maxDeathYear.get()) {
                    maxDeathYear.set(maximumDeathYear);
                }
                if(minimumBirthYear < minBirthYear.get()) {
                    minBirthYear.set(minimumBirthYear);
                }
            }
        });
    }

    /**
     * Method to get the mean age across the lienage
     * @param allNodes
     * @param meanAge Calculate mean of all the ages
     */
    public void getMeanAge(List<Node> allNodes, AtomicInteger meanAge) {
        allNodes.stream().forEach(node -> {
            if(!(node.getData() instanceof String)) {
                meanAge.set(meanAge.get() + Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear()));
            }
        });
    }

    /**
     * Method to get the media of all the ages in the lineage
     * @return the median of all the ages
     */
    public Integer getMedianAge() throws Exception {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());

        if(allNodes == null || allNodes.isEmpty()) {
            throw new Exception("Cannot get Median age for an empty tree");
        }

        allNodes.sort((node1, node2) -> {
            if(!(node1.getData() instanceof String) && !(node2.getData() instanceof String)) {
                int node1age = LineageComputationUtils.deriveDeathYear(node1) - LineageComputationUtils.deriveBirthYear(node1);
                int node2age = LineageComputationUtils.deriveDeathYear(node2) - LineageComputationUtils.deriveBirthYear(node2);
                return node1age - node2age;

            }
            return 0;
        });
        cacheManagerService.setLongestLiving(allNodes.get(allNodes.size()-1));
        cacheManagerService.setShortestLiving(allNodes.get(0));
        Integer medianIndex = (allNodes.size()-1)/2;
        if((allNodes.size()-1)%2 == 0) {
            return (LineageComputationUtils.deriveAge(allNodes.get(medianIndex)) + LineageComputationUtils.deriveAge(allNodes.get(medianIndex+1)))/2;
        } else {
            return LineageComputationUtils.deriveAge(allNodes.get(medianIndex));
        }
    }

    /**
     * Methog to the interquartile range members of the lineage
     * @return the Interqurtile start index and endindex in an array with these 2 values
     */
    public Integer[] getInterquartileRange() {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        List<Node> allNodes = graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
        allNodes.sort((node1, node2) -> {
            if(!(node1.getData() instanceof String) && !(node2.getData() instanceof String)) {
                int node1age = LineageComputationUtils.deriveDeathYear(node1) - LineageComputationUtils.deriveBirthYear(node1);
                int node2age = LineageComputationUtils.deriveDeathYear(node2) - LineageComputationUtils.deriveBirthYear(node2);
                return node1age - node2age;

            }
            return 0;
        });

        Integer medianIndex = (allNodes.size()-1)/2;

        Integer interQuartileStartIndex = 0;
        Integer interQuartileEndIndex = 0;

        if((allNodes.size()-1)%2 == 0) {
            interQuartileStartIndex = (medianIndex/2);
            interQuartileEndIndex = (allNodes.size() + medianIndex)/2 - 1;
        } else {
            interQuartileStartIndex = (medianIndex/2) + 1;
            interQuartileEndIndex = (allNodes.size() + (medianIndex))/2;
        }

        Integer[] quartileIndexes = new Integer[2];
        quartileIndexes[0] = interQuartileStartIndex;
        quartileIndexes[1] = interQuartileEndIndex;
        return quartileIndexes;
    }

    /**
     * Method to get all the nodes stored in the graph
     * @return List of all unique nodes in the graph
     */
    public List<Node> getAllGraphNodes() throws Exception {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        return graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
    }
}
