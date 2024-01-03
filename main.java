import NEAT.Connection;
import NEAT.NeuralNetwork;
import NEAT.Node;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        test0();
    }

    public static void test0() {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Connection> connections = new ArrayList<>();

        ArrayList<Node> inputs = new ArrayList<>();
        ArrayList<Node> hidden1 =  new ArrayList<>();
        ArrayList<Node> hidden2 = new ArrayList<>();
        ArrayList<Node> output = new ArrayList<>();

        inputs.add(new Node(0, 3, 0));
        hidden1.add(new Node(1, 0, 0));
        hidden1.add(new Node(2, 0, 0));
        hidden1.add(new Node(3, 0, 0));
        hidden2.add(new Node(4, 0, 0));
        hidden2.add(new Node(5, 0, 0));
        hidden2.add(new Node(6, 0, 0));
        output.add(new Node(7, 0, 0));

        ArrayList<Connection> connections0 = Connection.connectAll(inputs, hidden1, true, 1, true);
        ArrayList<Connection> connections1 = Connection.connectAll(hidden1, hidden2, true, 1, true);
        ArrayList<Connection> connections2 = Connection.connectAll(hidden2, output, true, 1, true);

        nodes.addAll(inputs);
        nodes.addAll(hidden1);
        nodes.addAll(hidden2);
        nodes.addAll(output);

        connections.addAll(connections0);
        connections.addAll(connections1);
        connections.addAll(connections2);

        NeuralNetwork nn = new NeuralNetwork(nodes, connections);
        nn.forwardFeed();

        System.out.println(nn);
        System.out.println(output.get(0).getValue());
    }
}