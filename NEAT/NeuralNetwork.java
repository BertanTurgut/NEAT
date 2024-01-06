package NEAT;

import java.util.ArrayList;
import java.util.Collections;

public class NeuralNetwork {
    private ArrayList<Node> nodes;
    private ArrayList<Node> inputNodes;
    private ArrayList<Node> outputNodes;
    private ArrayList<Integer> layers;
    private ArrayList<Connection> connections;

    public NeuralNetwork(ArrayList<Node> nodes, ArrayList<Connection> connections) {
        this.nodes = nodes;
        this.connections = connections;
        this.inputNodes = new ArrayList<>();
        this.outputNodes = new ArrayList<>();
        this.layers = new ArrayList<>();
        for (Node node : this.nodes)
            if (!this.layers.contains(node.getDepth()))
                this.layers.add(node.getDepth());
        for (Node node : this.nodes) {
            if (node.getDepth() == 0)
                this.inputNodes.add(node);
            else if (node.getDepth() == this.layers.get(this.layers.size() - 1))
                this.outputNodes.add(node);
        }
        Collections.sort(this.layers);
    }

    public void forwardFeed() {
        for (Integer layer : this.layers)
            for (Connection connection : this.connections)
                if (connection.getInputNode().getDepth() == layer)
                    connection.feedOutputNode();
    }

    public String output() {
        String str = "";
        int counter = 0;
        for (Node node : this.outputNodes) {
            str += "Output " + counter + ": " + node.getValue() + "\n";
            counter++;
        }
        return str;
    }

    @Override
    public String toString() {
        String str = "NEURAL NETWORK:\n------------\n";
        for (Integer layer : this.layers) {
            for (Connection connection : connections)
                if (connection.getInputNode().getDepth() == layer) {
                    str += connection.getInputNode().getId() + " -(" + connection.getWeight() + ")-> " + connection.getOutputNode().getId();
                    str = connection.getEnabled() ? str + "\n" : str + " [X]\n";
                }
            if (!this.layers.get(layers.size() - 1).equals(layer))
                str += "------------\n";
        }
        return str;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Node> getInputNodes() {
        return inputNodes;
    }

    public void setInputNodes(ArrayList<Node> inputNodes) {
        this.inputNodes = inputNodes;
    }

    public ArrayList<Node> getOutputNodes() {
        return outputNodes;
    }

    public void setOutputNodes(ArrayList<Node> outputNodes) {
        this.outputNodes = outputNodes;
    }

    public ArrayList<Integer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Integer> layers) {
        this.layers = layers;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }
}
