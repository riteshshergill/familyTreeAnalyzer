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

    /**
     * Method to compute the family treelineage
     * @return
     */
    public List<StringBuilder> getLineage() {
        List<Node> leafNodes = cacheManagerService.getLeafNodes();
        final List<LinkedList<String>> antecedants = new ArrayList<>();
        final AtomicInteger longest = new AtomicInteger(0);
        final AtomicInteger shortest = new AtomicInteger(cacheManagerService.getDepth() + 4);
        leafNodes.stream().forEach((leaf) -> antecedants.add(TreeUtils.findParents(leaf, longest, shortest)));
        List<StringBuilder> finalLineages = new ArrayList<>();
        LineageComputationUtils.constructLineageResult(antecedants, finalLineages);
        return finalLineages;
    }

    public List<Node> getAllSortedNodes(String sortOrder) {
        List<Node> allNodes = getAllGraphNodes();
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

    public void getLineageRange(List<Node> allNodes, AtomicInteger maxDeathYear, AtomicInteger minBirthYear) {
        allNodes.stream().forEach(node -> {
            if(!(node.getData() instanceof String)) {
                if((Integer.parseInt(((Member)node.getData()).getDeathYear()) > maxDeathYear.get())) {
                    maxDeathYear.set((Integer.parseInt(((Member)node.getData()).getDeathYear())));
                }
                if((Integer.parseInt(((Member)node.getData()).getBirthYear()) < minBirthYear.get())) {
                    minBirthYear.set((Integer.parseInt(((Member)node.getData()).getBirthYear())));
                }
            }
        });
    }

    public void getMeanAge(List<Node> allNodes, AtomicInteger meanAge) {
        allNodes.stream().forEach(node -> {
            if(!(node.getData() instanceof String)) {
                meanAge.set(meanAge.get() + Integer.parseInt(((Member)node.getData()).getDeathYear()) - Integer.parseInt(((Member)node.getData()).getBirthYear()));
            }
        });
    }

    public List<Node> getAllGraphNodes() {
        GraphUtil graphUtil = cacheManagerService.getGraph();
        return graphUtil.getAllNodes(graphUtil.getCurrentGraphInstance()).stream().filter((node) -> (node.getData() instanceof Member)).collect(Collectors.toList());
    }
}
