package NEAT;

import java.util.*;

public class Connection {
    private Node inputNode;
    private Node outputNode;
    private float weight;
    private boolean enabled;

    public Connection(Node input, Node output, float weight, boolean enabled) {
        this.inputNode = input;
        this.outputNode = output;
        this.weight = weight;
        this.enabled = enabled;
    }

    public Connection(Connection other) {
        this.inputNode = other.getInputNode();
        this.outputNode = other.outputNode;
        this.weight = other.weight;
        this.enabled = other.enabled;
    }

    public void feedOutputNode() {
        this.outputNode.setValue(this.outputNode.getValue() + this.inputNode.getValue() * this.weight);
    }

    public static ArrayList<Connection> connectAll(ArrayList<Node> inputNodes, ArrayList<Node> outputNodes, boolean randomWeight, float weight, boolean enabled) {
        Random random = new Random(System.currentTimeMillis());
        ArrayList<Connection> connections = new ArrayList<>();
        for (Node in : inputNodes)
            for (Node out : outputNodes) {
                float weight_loc = (randomWeight) ? (float)(random.nextInt(100)) / 10 : weight;
                Connection connection = new Connection(in, out, weight_loc, enabled);
                connections.add(connection);
            }
        return connections;
    }

    public Node getInputNode() {
        return inputNode;
    }
    public Node getOutputNode() {
        return outputNode;
    }
    public float getWeight() {
        return weight;
    }
    public boolean getEnabled() {
        return enabled;
    }

    public void setInputNode(Node inputNode) {
        this.inputNode = inputNode;
    }
    public void setOutputNode(Node outputNode) {
        this.outputNode = outputNode;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
