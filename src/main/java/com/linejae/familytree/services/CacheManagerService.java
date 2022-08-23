package com.linejae.familytree.services;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.models.Report;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Store data like a cache to be passed around between services
 */
@Service
@Scope("singleton")
public class CacheManagerService {

    private GraphUtil graphUtil;

    Map<String, Report> multiLineageMap = new HashMap<>();

    private Node rootNode;

    private List<Node> leafNodes = new ArrayList<>();

    private Integer depth = 0;

    private Integer shortestDepth = 0;

    private Node longestLiving;

    private Node shortestLiving;

    public GraphUtil getGraph() {
        return this.graphUtil;
    }

    public void setGraph(GraphUtil graphUtil) {
        this.graphUtil = graphUtil;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getRootNode() {
        return this.rootNode;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getShortestDepth() {
        return shortestDepth;
    }

    public void setShortestDepth(Integer shortestDepth) {
        this.shortestDepth = shortestDepth;
    }

    public List<Node> getLeafNodes() {
        return leafNodes;
    }

    public void setLeafNodes(List<Node> leafNodes) {
        this.leafNodes = leafNodes;
    }

    public Node getLongestLiving() {
        return longestLiving;
    }

    public void setLongestLiving(Node longestLiving) {
        this.longestLiving = longestLiving;
    }

    public Node getShortestLiving() {
        return shortestLiving;
    }

    public void setShortestLiving(Node shortestLiving) {
        this.shortestLiving = shortestLiving;
    }

    public Map<String, Report> getMultiLineageMap() {
        return multiLineageMap;
    }

    public void setMultiLineageMap(Map<String, Report> multiLineageMap) {
        this.multiLineageMap = multiLineageMap;
    }
}
