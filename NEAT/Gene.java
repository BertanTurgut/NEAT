package NEAT;

import org.w3c.dom.traversal.NodeFilter;

import java.util.ArrayList;
import java.util.Random;

public class Gene {
    public static ArrayList<Gene> innovations = new ArrayList<>();
    public static int innovationTracker = 0;

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

    public static String encodeGenome(ArrayList<Gene> genome) {
        String str = "";
        for (Gene gene : genome) {
            if (gene instanceof ConnectionGene)
                str = ((ConnectionGene) gene).isEnabled() ? str : str + "X";
            str += gene.innovationNumber + "|";
        }
        return str;
    }

    public static ArrayList<Gene> decodeGenome(String encoding) {
        ArrayList<Gene> genome = new ArrayList<>();
        String part = "";
        boolean isEnabled = true;
        for (int i = 0; i < encoding.length(); i++) {
            if (encoding.charAt(i) != '|')
                part += encoding.substring(i, i + 1);
            else {
                if (part.length() > 0 && part.charAt(0) == 'X')
                    isEnabled = false;
                int number = isEnabled ? Integer.parseInt(part.substring(0)) : Integer.parseInt(part.substring(1));
                for (Gene gene : innovations)
                    if (gene.innovationNumber == number)
                        if (gene instanceof ConnectionGene && ((ConnectionGene)gene).isEnabled() == isEnabled)
                            genome.add(gene);
                        else if (gene instanceof NodeGene)
                            genome.add(gene);
                part = "";
                isEnabled = true;
            }
        }
        return genome;
    }

    public static ArrayList<Gene> matchGenomes(ArrayList<Gene> genome1, float fitness1, ArrayList<Gene> genome2, float fitness2) {
        ArrayList<Gene> offspringGenome = new ArrayList<>();
        Random r = new Random();
        for (Gene gene1 : genome1) {
            boolean find = false;
            for (Gene gene2 : genome2) {
                if (gene1.innovationNumber == gene2.innovationNumber) {
                    offspringGenome.add(r.nextBoolean() ? gene1 : gene2);
                    find = true;
                    break;
                }
            }
            if (!find)
                if (fitness1 >= fitness2)
                    offspringGenome.add(gene1);
        }
        for (Gene gene2 : genome2) {
            boolean find = false;
            for (Gene gene1 : genome1) {
                if (gene1.innovationNumber == gene2.innovationNumber) {
                    find = true;
                    break;
                }
            }
            if (!find)
                if (fitness2 >= fitness1)
                    offspringGenome.add(gene2);
        }
        return offspringGenome;
    }

    private int innovationNumber;
    private int geneType; // 0->Node 1->Connection

    public Gene(boolean enabled) {
        this.innovationNumber = innovationTracker;
        innovations.add(this);
        if (!enabled)
            innovationTracker++;
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

