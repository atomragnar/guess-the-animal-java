package animals.tree;


import com.fasterxml.jackson.annotation.*;

import java.util.UUID;

/*
    Node class for the Animal Fact Tree.

    This class represents a node in the Animal Fact Tree. It contains the data and the references to the
    left and right child nodes.
    If the node is a leaf node, then the left and right child nodes are null.
    if the node is a root node, then the parent node is null.
    If the node is a left child node, then the parent node's left child node is this node.
    And left will represent "no" and right will represent "yes". So vice versa for right child node.

 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "id")
public class Node {
    private NodeLabel label;
    private String data;
    private Node leftChild;
    private Node rightChild;


    public Node() {
    }

    public Node(String data) {
        this.data = data;
    }

    public Node(String data, NodeLabel label) {
        this.data = data;
        this.label = label;
    }


    @JsonIgnore
    private UUID generateUniqueId() {
        return UUID.randomUUID();
    }


    @JsonIgnore
    public boolean isLeaf() {
        return leftChild == null || rightChild == null;
    }

    @JsonIgnore
    public boolean isRoot() {
        return label == NodeLabel.ROOT;
    }

    @JsonIgnore
    public boolean isRightChild() {
        return label == NodeLabel.RIGHT_CHILD;
    }

    @JsonIgnore
    public boolean isLeftChild() {
        return label == NodeLabel.LEFT_CHILD;
    }

    // Getters and setters
    @JsonProperty
    public Node getLeftChild() {
        return leftChild;
    }

    @JsonProperty
    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
        this.leftChild.setLabel(NodeLabel.LEFT_CHILD);
    }

    @JsonProperty
    public Node getRightChild() {
        return rightChild;
    }

    @JsonProperty
    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
        this.rightChild.setLabel(NodeLabel.RIGHT_CHILD);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public NodeLabel getLabel() {
        return label;
    }

    public void setLabel(NodeLabel label) {
        this.label = label;
    }


}
