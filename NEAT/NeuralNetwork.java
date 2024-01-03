package NEAT;

import java.util.ArrayList;
import java.util.Collections;

public class NeuralNetwork {
    private ArrayList<Node> nodes;
    ArrayList<Integer> layers;
    private ArrayList<Connection> connections;

    public NeuralNetwork(ArrayList<Node> nodes, ArrayList<Connection> connections) {
        this.nodes = nodes;
        this.connections = connections;
        this.layers = new ArrayList<>();
        for (Node node : this.nodes)
            if (!this.layers.contains(node.getDepth()))
                this.layers.add(node.getDepth());
        Collections.sort(this.layers);
    }

    public void forwardFeed() {
        for (Integer layer : this.layers)
            for (Connection connection : this.connections)
                if (connection.getInputNode().getDepth() == layer)
                    connection.feedOutputNode();
    }

    @Override
    public String toString() {
        String str = "==========\n";
        for (Integer layer : this.layers) {
            for (Connection connection : connections)
                if (connection.getInputNode().getDepth() == layer)
                    str += connection.getInputNode().getId() + " -(" + connection.getWeight() + ")-> " + connection.getOutputNode().getId() + "\n";
            if (!this.layers.get(layers.size() - 1).equals(layer))
                str += "==========\n";
        }
        return str;
    }
}
