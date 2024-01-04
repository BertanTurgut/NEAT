package NEAT;

import java.util.ArrayList;

public class Gene {
    public static ArrayList<Gene> innovations = new ArrayList<>();

    public static NeuralNetwork createNNFromGenome(ArrayList<Gene> genome) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (Gene gene : genome) {
            if (gene.geneType == 0) {
                Node node = new Node(((NodeGene)gene).getId(), 0);
                nodes.add(node);
            }
        }
        ArrayList<Connection> connections = new ArrayList<>();
        for (Gene gene : genome) {
            if (gene.geneType == 1) {
                Node input = null;
                Node output = null;
                for (Node node : nodes) {
                    if (node.getId() == ((ConnectionGene)gene).getInId())
                        input = node;
                    if (node.getId() == ((ConnectionGene)gene).getOutId())
                        output = node;
                }
                Connection connection = new Connection(input, output, ((ConnectionGene)gene).getWeight(), ((ConnectionGene)gene).isEnabled());
                connections.add(connection);
            }
        }
        return new NeuralNetwork(nodes, connections);
    }

    private int innovationNumber;
    private int geneType; // 0->Node 1->Connection

    public Gene() {
        this.innovationNumber = innovations.size();
        innovations.add(this);
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    public int getGeneType() {
        return geneType;
    }

    public void setGeneType(int geneType) {
        this.geneType = geneType;
    }
}

