import NEAT.*;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        test0();
        test1();
    }

    /**
     * test0 was tested on 04.01.2024 at 17.00
     */
    public static void test0() {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Connection> connections = new ArrayList<>();

        ArrayList<Node> inputs = new ArrayList<>();
        ArrayList<Node> hidden1 = new ArrayList<>();
        ArrayList<Node> hidden2 = new ArrayList<>();
        ArrayList<Node> output = new ArrayList<>();

        inputs.add(new Node(0, 3));
        hidden1.add(new Node(1, 0));
        hidden1.add(new Node(2, 0));
        hidden1.add(new Node(3, 0));
        hidden2.add(new Node(4, 0));
        hidden2.add(new Node(5, 0));
        hidden2.add(new Node(6, 0));
        output.add(new Node(7, 0));

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

        System.out.println("Test 0:\n#============#\n");
        System.out.println(nn);
        System.out.println(output.get(0).getValue());
        System.out.println("#============#\n");
    }

    /**
     * test1 was tested on 05.01.2024 at 01.30
     */
    public static void test1() {
        ArrayList<Gene> genome = new ArrayList<>();

        genome.add(new NodeGene(0));
        genome.add(new NodeGene(1));
        genome.add(new NodeGene(2));
        genome.add(new NodeGene(3));
        genome.add(new NodeGene(4));
        genome.add(new NodeGene(5));

        genome.add(new ConnectionGene(0, 1, 1, true));
        genome.add(new ConnectionGene(0, 2, 1, true));
        genome.add(new ConnectionGene(1, 3, 1, true));
        genome.add(new ConnectionGene(1, 4, 1, true));
        genome.add(new ConnectionGene(2, 3, 1, true));
        genome.add(new ConnectionGene(2, 4, 1, true));
        genome.add(new ConnectionGene(3, 5, 1, true));
        genome.add(new ConnectionGene(4, 5, 1, true));

        NeuralNetwork nn = Gene.createNNFromGenome(genome);
        for (Node node : nn.getNodes())
            if (node.getDepth() == 0)
                node.setValue(3);
        nn.forwardFeed();

        System.out.println("Test 1:\n#============#\n");
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println("#============#\n");
    }
}