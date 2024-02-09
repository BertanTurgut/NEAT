import Graphics.TestAnimation;
import NEAT.*;
import Physics.Car;
import Physics.MathService;
import Physics.Object;
import Physics.Vertice;
import Simulation.Simulation;

import java.awt.*;
import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        //test0();
        //test1();
        //test2();
        //test3();
        //test4();
        //test5();
        //test6();
        //test7();
        //test8();
        //test9();
        test10();
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
        for (Gene gene : genome)
            System.out.println(gene);
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println("#============#\n");
    }

    /**
     * test2 was tested on 07.01.2024 at 01.00
     */
    public static void test2() {
        ArrayList<Gene> genome = new ArrayList<>();

        genome.add(new NodeGene(0));
        genome.add(new NodeGene(1));
        genome.add(new NodeGene(2));
        genome.add(new NodeGene(3));
        genome.add(new NodeGene(4));
        genome.add(new NodeGene(5));

        genome.add(new ConnectionGene(0, 3, 1, false));
        genome.add(new ConnectionGene(0, 5, 1, true));
        genome.add(new ConnectionGene(4, 5, 1, true));
        genome.add(new ConnectionGene(2, 4, 1, false));
        genome.add(new ConnectionGene(3, 4, 1, true));
        genome.add(new ConnectionGene(1, 3, 1, true));

        NeuralNetwork nn = Gene.createNNFromGenome(genome);
        for (Node inputNode : nn.getInputNodes())
            inputNode.setValue(3);
        nn.forwardFeed();

        String encoding = Gene.encodeGenome(genome);
        ArrayList<Gene> genome2 = Gene.decodeGenome(encoding);

        System.out.println("Test 2:\n#============#\n");
        for (Gene gene : Gene.innovations)
            System.out.println(gene);
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println(encoding);
        for (Gene gene : genome2)
            System.out.println(gene);
        System.out.println("#============#\n");
    }

    /**
     * test3 was tested on 07.01.2024 at 02.00
     */
    public static void test3() {
        Gene gene0 = new NodeGene(0);
        Gene gene1 = new NodeGene(1);
        Gene gene2 = new NodeGene(2);
        Gene gene3 = new NodeGene(3);
        Gene gene4 = new NodeGene(4);

        Gene gene5 = new NodeGene(5);

        Gene gene6 = new ConnectionGene(0, 3, 1, true);
        Gene gene7 = new ConnectionGene(1, 3, 1, true);
        Gene gene8 = new ConnectionGene(2, 4, 1, true);
        Gene gene9 = new ConnectionGene(3, 4, 1, true);

        Gene gene10 = new ConnectionGene(0, 5, 1, true);
        Gene gene11 = new ConnectionGene(2, 3, 1, true);
        Gene gene12= new ConnectionGene(5, 3, 1, true);

        ArrayList<Gene> genome1 = new ArrayList<>();

        genome1.add(gene0);
        genome1.add(gene1);
        genome1.add(gene2);
        genome1.add(gene3);
        genome1.add(gene4);

        genome1.add(gene6);
        genome1.add(gene7);
        genome1.add(gene8);
        genome1.add(gene9);

        ArrayList<Gene> genome2 = new ArrayList<>();

        genome2.add(gene0);
        genome2.add(gene1);
        genome2.add(gene2);
        genome2.add(gene3);
        genome2.add(gene4);
        genome2.add(gene5);

        genome2.add(gene10);
        genome2.add(gene7);
        genome2.add(gene11);
        genome2.add(gene12);
        genome2.add(gene9);

        ArrayList<Gene> offspringGenome = Gene.matchGenomes(genome1, 1, genome2, 1);
        NeuralNetwork nn = Gene.createNNFromGenome(offspringGenome);

        System.out.println("Test 3:\n#============#\n");
        System.out.println(nn);
        System.out.println("#============#\n");
    }

    /**
     * test4 was tested on 12.01.2024 at 15.00
     */
    public static void test4() {
        Vertice vertex0 = new Vertice(7f, 3.3f);

        Vertice boxVertex0 = new Vertice(0f, 0f);
        Vertice boxVertex1 = new Vertice(0f, 3f);
        Vertice boxVertex2 = new Vertice(3f, 3f);
        Vertice boxVertex3 = new Vertice(3f, 0f);

        ArrayList<Vertice> box0 = new ArrayList<>();
        box0.add(boxVertex0);
        box0.add(boxVertex1);
        box0.add(boxVertex2);
        box0.add(boxVertex3);

        Vertice a = new Vertice(4.4f, 2.3f);
        Vertice b = new Vertice(10.3f, 7.3f);

        System.out.println("Test 4:\n#============#\n");
        System.out.println(MathService.isVertexInsideBox(vertex0, box0));
        System.out.println(MathService.getDistanceBetweenPoints(a, b));
        System.out.println(Math.tan(Math.toRadians(-45)));
        System.out.println("#============#\n");

    }

    /**
     * test5 was tested on 16.01.2024 at 13.00
     */
    public static void test5() {
        TestAnimation testAnimation = new TestAnimation();
        testAnimation.startAnimation();
    }

    /**
     * test6 was tested on 18.01.2024 at 23.30
     */
    public static void test6() {
        ArrayList<Gene> genome = new ArrayList<>();

        genome.add(new NodeGene(0));
        genome.add(new NodeGene(1));
        genome.add(new NodeGene(2));
        genome.add(new NodeGene(3));

        genome.add(new ConnectionGene(0, 1, 1 ,true));
        genome.add(new ConnectionGene(2, 1, 1 ,true));
        genome.add(new ConnectionGene(1, 3, 1, true));

        NeuralNetwork nn = Gene.createNNFromGenome(genome);
        for (Node inputNode : nn.getInputNodes())
            inputNode.setValue(3);
        nn.forwardFeed();

        String encoding = Gene.encodeGenome(genome);

        Car testCar = new Car(20, 45, 0, 0, 0);
        Agent agent = new Agent(testCar, encoding);

        System.out.println("Test 6:\n#============#\n");
        for (Gene gene : Gene.innovations)
            System.out.println(gene);
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println(encoding);
        agent.weightMutation();
        for (Node inputNode : agent.getNeuralNetwork().getInputNodes())
            inputNode.setValue(3);
        agent.getNeuralNetwork().forwardFeed();
        for (Gene gene : Gene.innovations)
            System.out.println(gene);
        System.out.println(agent.getNeuralNetwork());
        System.out.println(agent.getNeuralNetwork().output());
        System.out.println(agent.getGenomeEncoding());
        System.out.println("#============#\n");
    }

    /**
     * test7 was tested on 19.01.2024 at 03.00
     */
    public static void test7() {
        ArrayList<Gene> genome = new ArrayList<>();

        genome.add(new NodeGene(0));
        genome.add(new NodeGene(1));
        genome.add(new NodeGene(2));

        genome.add(new ConnectionGene(0, 2, 0.2f, true));
        genome.add(new ConnectionGene(1, 2, 0.5f, true));

        NeuralNetwork nn = Gene.createNNFromGenome(genome);
        for (Node inputNode : nn.getInputNodes())
            inputNode.setValue(0.3f);
        nn.forwardFeed();

        String encoding = Gene.encodeGenome(genome);

        Car testCar = new Car(20, 45, 0, 0, 0);
        Agent agent = new Agent(testCar, encoding);

        System.out.println("Test 7:\n#============#\n");
        for (Gene gene : Gene.innovations)
            System.out.println(gene);
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println(encoding + "\n");
        agent.nodeAddMutation();
        for (Node inputNode : agent.getNeuralNetwork().getInputNodes())
            inputNode.setValue(1f);
        agent.getNeuralNetwork().forwardFeed();
        for (Gene gene : agent.getGenome())
            System.out.println(gene);
        System.out.println(agent.getNeuralNetwork());
        System.out.println(agent.getNeuralNetwork().output());
        System.out.println(agent.getGenomeEncoding() + "\n");
        System.out.println("#============#\n");
    }

    /**
     * test8 was tested on 19.01.2024 at 15.30
     */
    public static void test8() {
        ArrayList<Gene> genome = new ArrayList<>();

        genome.add(new NodeGene(0));
        genome.add(new NodeGene(1));
        genome.add(new NodeGene(2));
        genome.add(new NodeGene(3));

        genome.add(new ConnectionGene(0, 2, 0.3f, true));
        genome.add(new ConnectionGene(1, 2, 0.5f, true));
        genome.add(new ConnectionGene(2, 3, -0.1f, true));

        NeuralNetwork nn = Gene.createNNFromGenome(genome);
        for (Node inputNode : nn.getInputNodes())
            inputNode.setValue(1f);
        nn.forwardFeed();

        String encoding = Gene.encodeGenome(genome);

        Car testCar = new Car(20, 45, 0, 0, 0);
        Agent agent = new Agent(testCar, encoding);

        System.out.println("Test 8:\n#============#\n");
        for (Gene gene : Gene.innovations)
            System.out.println(gene);
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println(encoding + "\n");
        for (Node node : nn.getNodes())
            System.out.println(node.getId() + ", " + node.getDepth() + ", " + node.getValue());
        System.out.println("\n");
        agent.connectionSwitchMutation();
        for (Node inputNode : agent.getNeuralNetwork().getInputNodes())
            inputNode.setValue(1f);
        agent.getNeuralNetwork().forwardFeed();
        for (Gene gene : agent.getGenome())
            System.out.println(gene);
        System.out.println(agent.getNeuralNetwork());
        System.out.println(agent.getNeuralNetwork().output());
        System.out.println(agent.getGenomeEncoding() + "\n");
        for (Node node : agent.getNeuralNetwork().getNodes())
            System.out.println(node.getId() + ", " + node.getDepth() + ", " + node.getValue());
        System.out.println("\n");
        System.out.println("#============#\n");
    }

    /**
     * test9 was tested on 19.01.2024 at 19.00
     */
    public static void test9() {
        ArrayList<Gene> genome = new ArrayList<>();

        genome.add(new NodeGene(0));
        genome.add(new NodeGene(1));
        genome.add(new NodeGene(2));
        genome.add(new NodeGene(3));

        genome.add(new ConnectionGene(0, 2, 0.3f, true));
        genome.add(new ConnectionGene(1, 2, 0.5f, true));
        genome.add(new ConnectionGene(2, 3, -0.1f, true));

        NeuralNetwork nn = Gene.createNNFromGenome(genome);
        for (Node inputNode : nn.getInputNodes())
            inputNode.setValue(1f);
        nn.forwardFeed();

        String encoding = Gene.encodeGenome(genome);

        Car testCar = new Car(20, 45, 0, 0, 0);
        Agent agent = new Agent(testCar, encoding);

        System.out.println("Test 8:\n#============#\n");
        for (Gene gene : Gene.innovations)
            System.out.println(gene);
        System.out.println(nn);
        System.out.println(nn.output());
        System.out.println(encoding + "\n");
        for (Node node : nn.getNodes())
            System.out.println(node.getId() + ", " + node.getDepth() + ", " + node.getValue());
        System.out.println();
        agent.connectionAddMutation();
        for (Node inputNode : agent.getNeuralNetwork().getInputNodes())
            inputNode.setValue(1f);
        agent.getNeuralNetwork().forwardFeed();
        for (Gene gene : agent.getGenome())
            System.out.println(gene);
        System.out.println(agent.getNeuralNetwork());
        System.out.println(agent.getNeuralNetwork().output());
        System.out.println(agent.getGenomeEncoding() + "\n");
        for (Node node : agent.getNeuralNetwork().getNodes())
            System.out.println(node.getId() + ", " + node.getDepth() + ", " + node.getValue());
        System.out.println();
        System.out.println("#============#\n");
    }

    /**
     * test10 was tested on __ at __
     */
    public static void test10() {
        ArrayList<Vertice> parkPlot = new ArrayList<>();
        parkPlot.add(new Vertice(9.5f*10+300, 16.2f*10+600));
        parkPlot.add(new Vertice(20.3f*10+300, 12.6f*10+600));
        parkPlot.add(new Vertice(16.6f*10+300, 1.8f*10+600));
        parkPlot.add(new Vertice(5.9f*10+300, 5.4f*10+600));

        Simulation simulation = new Simulation();
        simulation.process(parkPlot, 1000);
    }
}