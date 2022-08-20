package com.linejae.familytree.services;

import com.gcache.cache.CacheUtil;
import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class CacheManagerService {

    private GraphUtil graphUtil = new GraphUtil();

    private Node rootNode;

    private List<Node> leafNodes = new ArrayList<>();

    private Integer depth = 0;

    private Integer shortestDepth = 0;

    public GraphUtil getGraph() {
        return this.graphUtil;
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
}
