package com.gcache.graph.model;

import java.util.Objects;

/**
 * Node object to be stored in JGrapht MultiGraph instance
 */
public class Node {

    private String identifier;
    private Object data;

    private Node parentNode;

    public Node(String identifier, Object data) {
        this.identifier = identifier;
        this.data = data;
    }

    public Node(String identifier, Object data, Node parentNode) {
        this.identifier = identifier;
        this.data = data;
        this.parentNode = parentNode;
    }

    /**
     *
     * @return String unique identifier that identifies a node within the context
     * of your application
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @param identifier Set the unique identifier for the node within the context
     * of your application
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @return Object Get the data stored in the node
     */
    public Object getData() {
        return data;
    }

    /**
     * Set the data to be stored in the node
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return data.equals(node.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
