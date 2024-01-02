package NEAT;

import java.util.ArrayList;

public class Gene {
    public static ArrayList<Gene> innovations = new ArrayList<>();
    public static NeuralNetwork createNNFromGenome(ArrayList<NodeGene> nodeGenome, ArrayList<ConnectionGene> connectionGenome) {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Integer> createdNodeIds = new ArrayList<>();
        for (NodeGene gene : nodeGenome) {
            if (!createdNodeIds.contains(gene.getId())) {
                createdNodeIds.add(gene.getId());
                Node node = new Node(gene.getId(), 0);
                nodes.add(node);
            }
        }

        return null;
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

