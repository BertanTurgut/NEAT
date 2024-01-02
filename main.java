import NEAT.Connection;
import NEAT.NeuralNetwork;
import NEAT.Node;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        test0();
    }

    public static void test0() {
        ArrayList<Node> inputs = new ArrayList<>();
        ArrayList<Node> hidden1 =  new ArrayList<>();
        ArrayList<Node> hidden2 = new ArrayList<>();
        ArrayList<ArrayList<Node>> hiddens = new ArrayList<>();
        hiddens.add(hidden1);
        hiddens.add(hidden2);
        ArrayList<Node> output = new ArrayList<>();

        inputs.add(new Node(0, 3));
        hidden1.add(new Node(1, 0));
        hidden1.add(new Node(2, 0));
        hidden1.add(new Node(3, 0));
        hidden2.add(new Node(4, 0));
        hidden2.add(new Node(5, 0));
        hidden2.add(new Node(6, 0));
        output.add(new Node(7, 0));

        ArrayList<ArrayList<Connection>> connections = new ArrayList<>();
        connections.add(Connection.connectAll(inputs, hidden1, true, 1, true));
        connections.add(Connection.connectAll(hidden1, hidden2, true, 1, true));
        connections.add(Connection.connectAll(hidden2, output, true, 1, true));

        NeuralNetwork nn = new NeuralNetwork(inputs, output, hiddens, connections);
        nn.forwardFeed();

        System.out.println(nn);
    }
}