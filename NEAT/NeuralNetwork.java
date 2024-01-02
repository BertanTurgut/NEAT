package NEAT;

import java.util.ArrayList;

public class NeuralNetwork {
    private ArrayList<Node> inputs;
    private ArrayList<Node> outputs;
    private ArrayList<ArrayList<Node>> hiddenNodes;
    private ArrayList<ArrayList<Connection>> connections;

    public void forwardFeed() {
        for (ArrayList<Connection> layer : this.connections)
            for (Connection connection : layer)
                connection.getOutputNode().setValue(0);
        for (ArrayList<Connection> layer : this.connections)
            for (Connection connection : layer)
                connection.feedOutputNode();
    }

    public NeuralNetwork(ArrayList<Node> inputs, ArrayList<Node> outputs, ArrayList<ArrayList<Node>> hiddenNodes, ArrayList<ArrayList<Connection>> connections) {
        this.inputs = new ArrayList<>(inputs);
        this.outputs = new ArrayList<>(outputs);
        this.hiddenNodes = new ArrayList<>(hiddenNodes);
        this.connections = new ArrayList<>(connections);
    }

    public NeuralNetwork(NeuralNetwork other) {
        this.inputs = other.getInputs();
        this.outputs = other.getOutputs();
        this.hiddenNodes = other.getHiddenNodes();
        this.connections = other.getConnections();
    }

    public ArrayList<Node> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<Node> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<Node> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<Node> outputs) {
        this.outputs = outputs;
    }

    public ArrayList<ArrayList<Node>> getHiddenNodes() {
        return hiddenNodes;
    }

    public void setHiddenNodes(ArrayList<ArrayList<Node>> hiddenNodes) {
        this.hiddenNodes = hiddenNodes;
    }

    public ArrayList<ArrayList<Connection>> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<ArrayList<Connection>> connections) {
        this.connections = connections;
    }

    @Override
    public String toString() {
        String str = "==========\n";
        for (ArrayList<Connection> connectionLayer : this.connections) {
            for (Connection connection : connectionLayer)
                str += connection.getInputNode().getId() + " -(" + connection.getWeight() + ")-> " + connection.getOutputNode().getId() + "\n";
            str += "==========\n";
        }
        return str;
    }
}
