package NEAT;

import java.util.ArrayList;

public class Node {
    private int id;
    private float value;
    private int depth;
    private ArrayList<Node> inputNodes;
    private ArrayList<Node> outputNodes;

    public Node(int id, float value) {
        this.id = id;
        this.value = value;
        this.inputNodes = new ArrayList<>();
        this.outputNodes = new ArrayList<>();
    }

    // TODO: correct the stack overflow issue
    public void updateDepth() {
        if (this.inputNodes.isEmpty()) {
            this.depth = 0;
            return;
        }
        int previousDepth = this.depth;
        int max = this.inputNodes.get(0).depth + 1;
        for (Node inputNode : this.inputNodes)
            max = Math.max(inputNode.depth + 1, max);
        this.depth = max;
        if (this.depth != previousDepth)
            for (Node outputNode : this.outputNodes)
                if (this.depth + 1 > outputNode.depth)
                    outputNode.updateDepth();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void addInputNode(Node node) {
        this.inputNodes.add(node);
        try {
            this.updateDepth();
        } catch (Exception e) {
            throw new RuntimeException("! PROBLEM OCCURRED:\n" + this.inputNodes + "\n" + this.outputNodes);
        }
    }

    public void removeInputNode(Node node) {
        this.inputNodes.remove(node);
        this.updateDepth();
    }

    public void addOutputNode(Node node) {
        this.outputNodes.add(node);
    }

    public void removeOutputNode(Node node) {
        this.outputNodes.remove(node);
    }

    public ArrayList<Node> getInputNodes() {
        return inputNodes;
    }

    public ArrayList<Node> getOutputNodes() {
        return outputNodes;
    }
}
