import NEAT.Connection;
import NEAT.NeuralNetwork;
import NEAT.Node;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        ArrayList<Node> inputs = new ArrayList<>();
        ArrayList<Node> hidden1 =  new ArrayList<>();
        ArrayList<Node> hidden2 = new ArrayList<>();
        ArrayList<ArrayList<Node>> hiddens = new ArrayList<>();
        hiddens.add(hidden1);
        hiddens.add(hidden2);
        ArrayList<Node> output = new ArrayList<>();

        inputs.add(new Node("input", 3));
        hidden1.add(new Node("h11", 0));
        hidden1.add(new Node("h12", 0));
        hidden1.add(new Node("h13", 0));
        hidden2.add(new Node("h21", 0));
        hidden2.add(new Node("h22", 0));
        hidden2.add(new Node("h23", 0));
        output.add(new Node("output", 0));

        ArrayList<ArrayList<Connection>> connections = new ArrayList<>();
        connections.add(Connection.connectAll(inputs, hidden1, true, 1, true));
        connections.add(Connection.connectAll(hidden1, hidden2, true, 1, true));
        connections.add(Connection.connectAll(hidden2, output, true, 1, true));

        NeuralNetwork nn = new NeuralNetwork(inputs, output, hiddens, connections);
        nn.forwardFeed();
        System.out.println(nn.getOutputs().get(0).getValue());
    }
}
