package NEAT;

import Physics.Car;

import java.util.ArrayList;
import java.util.Random;

// TODO: implement speciation
public class NEAT {
    private ArrayList<NeuralNetwork> networks;
    private int agentCount;
    private final float mutationProb = 0.025f; // %2.5
    private final float weightMutationProb = 0.25f; // default
    private final float nodeAddMutationProb = 0.25f; // default
    private final float connectionSwitchMutationProb = 0.25f; // default
    private final float connectionAddMutationProb = 0.25f; // default

    /**
     * Randomly selects a connection gene and produces a new connection gene by manipulating the initial weight.
     */
    public static void weightMutation(Agent agent) {
        Random random = new Random();
        int connectionGeneCount = 0;
        for (Gene gene : agent.getGenome())
            if (gene instanceof ConnectionGene)
                connectionGeneCount++;
        int randomIndex = random.nextInt(connectionGeneCount + 1);
        ConnectionGene initialGene = null;
        int iterator = 1;
        for (Gene gene : agent.getGenome()) {
            if (gene instanceof ConnectionGene && iterator < randomIndex)
                iterator++;
            else if (gene instanceof ConnectionGene) {
                initialGene = (ConnectionGene) gene;
                agent.getGenome().remove(gene);
                break;
            }
        }
        int inId = initialGene.getInId();
        int outId = initialGene.getOutId();
        boolean enabled = initialGene.isEnabled();
        float randomWeight = random.nextFloat() * 2 - 1;
        agent.getGenome().add(new ConnectionGene(inId, outId, randomWeight, enabled));
        agent.setGenomeEncoding(Gene.encodeGenome(agent.getGenome()));
        agent.setNeuralNetwork(Gene.createNNFromGenome(agent.getGenome()));
    }

    /**
     * Removes a connection gene from the genome of the agent and replaces it with 1 node genes and 2 connection genes.
     */
    public static void nodeAddMutation(Agent agent) {
        Random random = new Random();
        int connectionGeneCount = 0;
        int biggestNodeId = 0;
        for (Gene gene : agent.getGenome())
            if (gene instanceof ConnectionGene)
                connectionGeneCount++;
            else if (gene instanceof NodeGene && ((NodeGene) gene).getId() > biggestNodeId)
                biggestNodeId = ((NodeGene) gene).getId();
        int randomIndex = random.nextInt(connectionGeneCount + 1);
        ConnectionGene initialGene = null;
        int iterator = 1;
        for (Gene gene : agent.getGenome()) {
            if (gene instanceof ConnectionGene && iterator < randomIndex)
                iterator++;
            else if (gene instanceof ConnectionGene) {
                initialGene = (ConnectionGene) gene;
                if (initialGene.isEnabled())
                    agent.getGenome().remove(gene);
                break;
            }
        }
        int inId = initialGene.getInId();
        int outId = initialGene.getOutId();
        float initialWeight = initialGene.getWeight();
        boolean enabled = initialGene.isEnabled();
        if (initialGene.isEnabled()) {
            for (Gene gene : Gene.innovations)
                if (gene instanceof ConnectionGene)
                    if (((ConnectionGene) gene).getInnovationNumber() == initialGene.getInnovationNumber()
                        && !((ConnectionGene) gene).isEnabled()) {
                        agent.getGenome().add(gene);
                        break;
                    }
        }
        agent.getGenome().add(new NodeGene(biggestNodeId + 1));
        agent.getGenome().add(new ConnectionGene(inId, biggestNodeId + 1, 1, enabled));
        agent.getGenome().add(new ConnectionGene(biggestNodeId + 1, outId, initialWeight, enabled));
        agent.setGenomeEncoding(Gene.encodeGenome(agent.getGenome()));
        agent.setNeuralNetwork(Gene.createNNFromGenome(agent.getGenome()));
    }

    /**
     * Randomly selects a connection gene and disables/enables it.
     */
    public static void connectionSwitchMutation(Agent agent) {
        Random random = new Random();
        int connectionGeneCount = 0;
        for (Gene gene : agent.getGenome())
            if (gene instanceof ConnectionGene)
                connectionGeneCount++;
        int randomIndex = random.nextInt(connectionGeneCount + 1);
        ConnectionGene initialGene = null;
        int iterator = 1;
        for (Gene gene : agent.getGenome()) {
            if (gene instanceof ConnectionGene && iterator < randomIndex)
                iterator++;
            else if (gene instanceof ConnectionGene) {
                initialGene = (ConnectionGene) gene;
                agent.getGenome().remove(gene);
                break;
            }
        }
        for (Gene gene : Gene.innovations)
            if (gene.getInnovationNumber() == initialGene.getInnovationNumber() &&
                    ((ConnectionGene) gene).isEnabled() != initialGene.isEnabled()) {
                agent.getGenome().add(gene);
                agent.setGenomeEncoding(Gene.encodeGenome(agent.getGenome()));
                agent.setNeuralNetwork(Gene.createNNFromGenome(agent.getGenome()));
                break;
            }
    }

    /**
     * Selects two nodes that are not connected and adds a new gene that connects them.
     */
    public static void connectionAddMutation(Agent agent) {
        Random random = new Random();
        ArrayList<Integer> integerList = new ArrayList<>();
        int nodeCount = 0;
        for (Node node : agent.getNeuralNetwork().getNodes()) {
            integerList.add(nodeCount);
            nodeCount++;
        }
        Node node1 = null, node2 = null;
        boolean valid = false;
        do {
            valid = true;
            int x = random.nextInt(integerList.size());
            int randomIndex1 = integerList.get(x);
            integerList.remove(x);
            int randomIndex2 = integerList.get(random.nextInt(integerList.size()));
            integerList.add(x, randomIndex1);
            int counter = 0;
            node1 = agent.getNeuralNetwork().getNodes().get(randomIndex1);
            node2 = agent.getNeuralNetwork().getNodes().get(randomIndex2);
            for (Connection connection : agent.getNeuralNetwork().getConnections()) {
                if ((connection.getInputNode().getId() == node1.getId() && connection.getOutputNode().getId() == node2.getId()) ||
                        (connection.getInputNode().getId() == node2.getId() && connection.getOutputNode().getId() == node1.getId()) ||
                        (node1.getDepth() == node2.getDepth())) {
                    valid = false;
                    break;
                }
            }
        }
        while (!valid);
        if (node1.getDepth() > node2.getDepth())
            agent.getGenome().add(new ConnectionGene(node2.getId(), node1.getId(), 1, true));
        else
            agent.getGenome().add(new ConnectionGene(node1.getId(), node2.getId(), 1, true));
        agent.setGenomeEncoding(Gene.encodeGenome(agent.getGenome()));
        agent.setNeuralNetwork(Gene.createNNFromGenome(agent.getGenome()));
    }
}
