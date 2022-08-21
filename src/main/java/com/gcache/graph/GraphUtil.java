package com.gcache.graph;

import com.gcache.graph.model.DataWeighedEdge;
import com.gcache.graph.model.Node;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to access the Graph instance and perform functions on it
 */
public class GraphUtil {

    private GraphInstance currentGraphInstance = new GraphInstance();

    /**
     * Print in Depth First, Breadth First or Edge Wise order
     */
    public static enum PrintOrder{
        DF,
        BF,
        EW
    }

    /**
     * Add an edge between a parent node and a child node
     * @param parentNode Parent node in the relationship
     * @param childNode Child Node in the relationship
     */
    public void addRelationship(Node parentNode, Node childNode) {
        if(!currentGraphInstance.getMultiGraph().containsVertex(parentNode)) {
            currentGraphInstance.getMultiGraph().addVertex(parentNode);
        }
        if(childNode != null && !currentGraphInstance.getMultiGraph().containsVertex(childNode)) {
            currentGraphInstance.getMultiGraph().addVertex(childNode);
        }
        if(childNode != null && !currentGraphInstance.getMultiGraph().containsEdge(parentNode, childNode)) {
            currentGraphInstance.getMultiGraph().addEdge(parentNode, childNode).setRelationMetadata(parentNode.getIdentifier() + ":" + childNode.getIdentifier());
        }
    }

    public Node findParent(Node targetNode) {
        List<DataWeighedEdge> edges = currentGraphInstance.getMultiGraph().edgeSet().stream().filter(edge -> edge.getTargetVertex() == targetNode).collect(Collectors.toList());
        List<Node> parentNodeList = (List)edges.stream().map(edge -> {
            return edge.getSourceVertex();
        }).collect(Collectors.toList());
        if(parentNodeList != null && parentNodeList.size() == 1) {
            return parentNodeList.get(0);
        }
        return null;
    }

    /**
     * Find the node with the given identifier
     * @param identifier Identifier that uniquely identifies the node
     * @return List nodes that share the same Identifier, ideally
     * this list should only have one record
     */
    public List<Node> findNodes(String identifier) {
        return currentGraphInstance.getMultiGraph().vertexSet().stream().filter(vertex -> ((Node)vertex).getIdentifier().equals(identifier)).collect(Collectors.toList());
    }

    /**
     * Locate the first edge that matches the parent and child identifiers
     * @param parentIdentifier Parent node identifier for the parent node in the edge
     * @param childIdentifier Child node identifier for the child node in the edge
     * @return DataWeighedEdge Edge object with the matching source and target vertex
     */
    public DataWeighedEdge findEdge(String parentIdentifier, String childIdentifier) {
        return currentGraphInstance.getMultiGraph().edgeSet().stream().filter(edge -> ((Node)edge.getSourceVertex()).getIdentifier().equals(parentIdentifier) && ((Node)edge.getTargetVertex()).getIdentifier().equals(childIdentifier)).collect(Collectors.toList()).get(0);
    }

    /**
     * Locate all the edges that match the parent and child identifiers
     * @param parentIdentifier Parent node identifier for the parent node in the edge
     * @param childIdentifier Child node identifier for the child node in the edge
     * @return List<DataWeighedEdge> Edge objects with the matching source and target vertex
     */
    public List<DataWeighedEdge> findAllEdges(String parentIdentifier, String childIdentifier) {
        return currentGraphInstance.getMultiGraph().edgeSet().stream().filter(edge -> ((Node)edge.getSourceVertex()).getIdentifier().equals(parentIdentifier) && ((Node)edge.getTargetVertex()).getIdentifier().equals(childIdentifier)).collect(Collectors.toList());
    }

    /**
     * Print the Graph maintained in this current instance
     * @param printOrder Print order can be Df(Depth first), BF(Breadth First) or EW(Edge Wise)
     * @param graphInstance Graph to be printed
     */
    public void printGraph(PrintOrder printOrder, GraphInstance graphInstance) {
        Iterator di = null;
        if(printOrder.equals(PrintOrder.DF)) {
            di = new DepthFirstIterator(graphInstance.getMultiGraph());
        } else if(printOrder.equals(PrintOrder.BF)){
            di = new BreadthFirstIterator(graphInstance.getMultiGraph());
        } else {
            graphInstance.getMultiGraph().edgeSet().stream().forEach(edge -> {
                System.out.print("Relation metadata: " + (edge.getRelationMetadata() != null ? edge.getRelationMetadata().toString() : ""));
                Node parentNode = (Node) edge.getSourceVertex();
                Node childNode = (Node) edge.getTargetVertex();

                System.out.print("parent identifier: " + parentNode.getIdentifier());
                System.out.print("parent data: " + parentNode.getData());

                System.out.print("child identifier: " + childNode.getIdentifier());
                System.out.print("child data: " + childNode.getData());
            });
            return;
        }

        while(di.hasNext()) {
            Node dataNode = (Node)di.next();
            System.out.print(dataNode.getData().toString() + "->");
        }
    }

    public List<Node> getAllNodes(GraphInstance graphInstance) {
        Iterator di = new DepthFirstIterator(graphInstance.getMultiGraph());
        HashSet<Node> dataNodesSet = new HashSet<>();
        while(di.hasNext()) {
            dataNodesSet.add((Node)di.next());
        }
        return new ArrayList<>(dataNodesSet);
    }

    /**
     * Get the currently maintained instance of the Graph
     * @return GraphInstance current Graph object
     */
    public GraphInstance getCurrentGraphInstance() {
        return this.currentGraphInstance;
    }
}
