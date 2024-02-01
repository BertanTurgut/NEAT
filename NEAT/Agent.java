package NEAT;

import NEAT.Gene;
import NEAT.NeuralNetwork;
import Physics.Car;
import Physics.Vertice;

import java.util.ArrayList;
import java.util.Random;

public class Agent {
    public static final float mutationProb = 0.025f; // CALIBRATE: calibrate mutation probability (should not exceed 0.025 [%2.5])
    public static final float weightMutationProb = 0.25f; // default
    public static final float nodeAddMutationProb = 0.25f; // default
    public static final float connectionSwitchMutationProb = 0.25f; // default
    public static final float connectionAddMutationProb = 0.25f; // default
    public static float compatibilityThreshold = 1f;

    // CALIBRATE: calibrate the compatibility threshold and the coefficients of the equation
    public static boolean areAgentsCompatible(Agent agent1, Agent agent2) {
        int disjointGeneCount = 0;
        int excessGeneCount = 0;
        float averageMatchingGeneWeightDifference = 0;
        int maxInnovation1 = 0;
        int maxInnovation2 = 0;
        for (Gene gene : agent1.getGenome())
            if (gene.getInnovationNumber() > maxInnovation1)
                maxInnovation1 = gene.getInnovationNumber();
        for (Gene gene : agent2.getGenome())
            if (gene.getInnovationNumber() > maxInnovation2)
                maxInnovation2 = gene.getInnovationNumber();
        int matchingConnectionGeneCount = 0;
        for (Gene gene1 : agent1.getGenome()) {
            boolean find = false;
            for (Gene gene2 : agent2.getGenome()) {
                if (gene1.getInnovationNumber() == gene2.getInnovationNumber()) {
                    if (gene1 instanceof ConnectionGene) {
                        matchingConnectionGeneCount++;
                        averageMatchingGeneWeightDifference += ((ConnectionGene) gene1).getWeight();
                    }
                    find = true;
                    break;
                }
            }
            if (!find) {
                if (gene1.getInnovationNumber() > maxInnovation2)
                    excessGeneCount++;
                else
                    disjointGeneCount++;
            }
        }
        averageMatchingGeneWeightDifference /= matchingConnectionGeneCount;
        for (Gene gene2 : agent2.getGenome()) {
            boolean find = false;
            for (Gene gene1 : agent1.getGenome()) {
                if (gene2.getInnovationNumber() == gene1.getInnovationNumber()) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                if (gene2.getInnovationNumber() > maxInnovation1)
                    excessGeneCount++;
                else
                    disjointGeneCount++;
            }
        }
        float coefficient1 = 1;
        float coefficient2 = 1;
        float coefficient3 = 1;
        int largerGenomeSize = Math.max(agent1.getGenome().size(), agent2.getGenome().size());
        float nonCompatibility = (coefficient1 * disjointGeneCount + coefficient2 * excessGeneCount) / largerGenomeSize +
                coefficient3 * averageMatchingGeneWeightDifference;
        return nonCompatibility <= compatibilityThreshold;
    }

    private Car car;
    private String genomeEncoding;
    private ArrayList<Gene> genome;
    private NeuralNetwork neuralNetwork;
    private float fitness;
    private float adjustedFitness;

    public Agent(Car car, String genomeEncoding) {
        this.car = car;
        this.genomeEncoding = genomeEncoding;
        this.genome = Gene.decodeGenome(this.genomeEncoding);
        this.neuralNetwork = Gene.createNNFromGenome(this.genome);
        this.fitness = 0;
        this.adjustedFitness = 0;
    }

    // CALIBRATE: calibrate drive() instance method
    public void drive(ArrayList<Vertice> parkPlot) {
        this.car.update(parkPlot);

        int i = 0;
        if (this.car.getBody().getVelocity() < 0)
            this.neuralNetwork.getInputNodes().get(i++).setValue(NeuralNetwork.sigmoid((float) -Math.log(-this.car.getBody().getVelocity())));
        else
            this.neuralNetwork.getInputNodes().get(i++).setValue(NeuralNetwork.sigmoid((float) Math.log(this.car.getBody().getVelocity())));
        this.neuralNetwork.getInputNodes().get(i++).setValue(NeuralNetwork.sigmoid(this.car.getBody().getRotation() / 360));
        this.neuralNetwork.getInputNodes().get(i++).setValue(NeuralNetwork.sigmoid(this.car.getWidth()));
        this.neuralNetwork.getInputNodes().get(i++).setValue(NeuralNetwork.sigmoid(this.car.getLength()));
        for (int k = 0; k < 16; k++)
            this.neuralNetwork.getInputNodes().get(i++).setValue(this.car.getVisionSensors()[k] / Car.visionSensorRange);
        if (this.car.getTargetDistanceSensor()[0] < 0)
            this.neuralNetwork.getInputNodes().get(i++).setValue((NeuralNetwork.sigmoid((float) -Math.log(-this.car.getTargetDistanceSensor()[0]))));
        else
            this.neuralNetwork.getInputNodes().get(i++).setValue((NeuralNetwork.sigmoid((float) Math.log(this.car.getTargetDistanceSensor()[0]))));
        if (this.car.getTargetDistanceSensor()[1] < 0)
            this.neuralNetwork.getInputNodes().get(i++).setValue((NeuralNetwork.sigmoid((float) -Math.log(-this.car.getTargetDistanceSensor()[1]))));
        else
            this.neuralNetwork.getInputNodes().get(i++).setValue((NeuralNetwork.sigmoid((float) Math.log(this.car.getTargetDistanceSensor()[1]))));
        for (int k = 0; k < 4; k++) {
            if (this.car.getParkAreaCheckSensor()[k])
                this.neuralNetwork.getInputNodes().get(i++).setValue(1);
            else
                this.neuralNetwork.getInputNodes().get(i++).setValue(0);
        }
        this.neuralNetwork.getInputNodes().get(i++).setValue(NeuralNetwork.sigmoid(this.car.getCollisionCount() / (float) 100));
        if (this.car.isColliding())
            this.neuralNetwork.getInputNodes().get(i++).setValue(1);
        else
            this.neuralNetwork.getInputNodes().get(i++).setValue(0);
        this.neuralNetwork.getInputNodes().get(i++).setValue(this.car.getParkPlotAlignment() / 360);

        this.neuralNetwork.forwardFeed();

        if (this.neuralNetwork.getOutputNodes().get(0).getValue() >= 0.5f)
            this.car.gas();
        if (this.neuralNetwork.getOutputNodes().get(1).getValue() >= 0.5f)
            this.car.reverse();
        if (this.neuralNetwork.getOutputNodes().get(2).getValue() >= 0.5f)
            this.car.brake();
        if (this.neuralNetwork.getOutputNodes().get(3).getValue() >= 0.5f)
            this.car.steerRight();
        if (this.neuralNetwork.getOutputNodes().get(4).getValue() >= 0.5f)
            this.car.steerLeft();
    }

    // CALIBRATE: calibrate calculateFitness() instance method's coefficients
    public void calculateFitness() {
        float fitness = 0;
        fitness -= Math.abs(this.car.getTargetDistanceSensor()[0]) + Math.abs(this.car.getTargetDistanceSensor()[1]);
        for (boolean bool : this.car.getParkAreaCheckSensor()) if (bool) fitness += 50;
        fitness -= Math.log(this.car.getCollisionCount()) * 20;
        if (this.car.isColliding()) fitness -= 100;
        fitness -= Math.abs(this.car.getBody().getVelocity() * 10);
        this.fitness = fitness;
    }

    public void calculateAdjustedFitness(Species species) {
        this.adjustedFitness = this.fitness / species.getAgents().size();
    }

    /**
     * Randomly selects a connection gene and produces a new connection gene by manipulating the initial weight.
     */
    public void weightMutation() {
        Random random = new Random();
        int connectionGeneCount = 0;
        for (Gene gene : this.getGenome())
            if (gene instanceof ConnectionGene)
                connectionGeneCount++;
        int randomIndex = random.nextInt(connectionGeneCount + 1);
        ConnectionGene initialGene = null;
        int iterator = 1;
        for (Gene gene : this.getGenome()) {
            if (gene instanceof ConnectionGene && iterator < randomIndex)
                iterator++;
            else if (gene instanceof ConnectionGene) {
                initialGene = (ConnectionGene) gene;
                this.getGenome().remove(gene);
                break;
            }
        }
        int inId = initialGene.getInId();
        int outId = initialGene.getOutId();
        boolean enabled = initialGene.isEnabled();
        float randomWeight = random.nextFloat() * 2 - 1;
        this.getGenome().add(new ConnectionGene(inId, outId, randomWeight, enabled));
        this.setGenomeEncoding(Gene.encodeGenome(this.getGenome()));
        this.setNeuralNetwork(Gene.createNNFromGenome(this.getGenome()));
    }

    /**
     * Removes a connection gene from the genome of the agent and replaces it with 1 node genes and 2 connection genes.
     */
    public void nodeAddMutation() {
        Random random = new Random();
        int connectionGeneCount = 0;
        int biggestNodeId = 0;
        for (Gene gene : this.getGenome())
            if (gene instanceof ConnectionGene)
                connectionGeneCount++;
            else if (gene instanceof NodeGene && ((NodeGene) gene).getId() > biggestNodeId)
                biggestNodeId = ((NodeGene) gene).getId();
        int randomIndex = random.nextInt(connectionGeneCount + 1);
        ConnectionGene initialGene = null;
        int iterator = 1;
        for (Gene gene : this.getGenome()) {
            if (gene instanceof ConnectionGene && iterator < randomIndex)
                iterator++;
            else if (gene instanceof ConnectionGene) {
                initialGene = (ConnectionGene) gene;
                if (initialGene.isEnabled())
                    this.getGenome().remove(gene);
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
                        this.getGenome().add(gene);
                        break;
                    }
        }
        this.getGenome().add(new NodeGene(biggestNodeId + 1));
        this.getGenome().add(new ConnectionGene(inId, biggestNodeId + 1, 1, enabled));
        this.getGenome().add(new ConnectionGene(biggestNodeId + 1, outId, initialWeight, enabled));
        this.setGenomeEncoding(Gene.encodeGenome(this.getGenome()));
        this.setNeuralNetwork(Gene.createNNFromGenome(this.getGenome()));
    }

    /**
     * Randomly selects a connection gene and disables/enables it.
     */
    public void connectionSwitchMutation() {
        Random random = new Random();
        int connectionGeneCount = 0;
        for (Gene gene : this.getGenome())
            if (gene instanceof ConnectionGene)
                connectionGeneCount++;
        int randomIndex = random.nextInt(connectionGeneCount + 1);
        ConnectionGene initialGene = null;
        int iterator = 1;
        for (Gene gene : this.getGenome()) {
            if (gene instanceof ConnectionGene && iterator < randomIndex)
                iterator++;
            else if (gene instanceof ConnectionGene) {
                initialGene = (ConnectionGene) gene;
                this.getGenome().remove(gene);
                break;
            }
        }
        for (Gene gene : Gene.innovations)
            if (gene.getInnovationNumber() == initialGene.getInnovationNumber() &&
                    ((ConnectionGene) gene).isEnabled() != initialGene.isEnabled()) {
                this.getGenome().add(gene);
                this.setGenomeEncoding(Gene.encodeGenome(this.getGenome()));
                this.setNeuralNetwork(Gene.createNNFromGenome(this.getGenome()));
                break;
            }
    }

    /**
     * Selects two nodes that are not connected and adds a new gene that connects them.
     */
    public void connectionAddMutation() {
        Random random = new Random();
        ArrayList<Integer> integerList = new ArrayList<>();
        int nodeCount = 0;
        for (Node node : this.getNeuralNetwork().getNodes()) {
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
            node1 = this.getNeuralNetwork().getNodes().get(randomIndex1);
            node2 = this.getNeuralNetwork().getNodes().get(randomIndex2);
            for (Connection connection : this.getNeuralNetwork().getConnections()) {
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
            this.getGenome().add(new ConnectionGene(node2.getId(), node1.getId(), 1, true));
        else
            this.getGenome().add(new ConnectionGene(node1.getId(), node2.getId(), 1, true));
        this.setGenomeEncoding(Gene.encodeGenome(this.getGenome()));
        this.setNeuralNetwork(Gene.createNNFromGenome(this.getGenome()));
    }

    /**
     * Agent's genome, genome encoding and neural network is updated inside this method.
     * Mutation probability is included in method implementation.
     */
    public void mutate() {
        Random random = new Random();
        if (random.nextFloat() <= mutationProb) {
            float randomFloat = random.nextFloat();
            if (randomFloat <= weightMutationProb)
                weightMutation();
            else if (randomFloat <= weightMutationProb + nodeAddMutationProb)
                nodeAddMutation();
            else if (randomFloat <= weightMutationProb + nodeAddMutationProb + connectionSwitchMutationProb)
                connectionSwitchMutation();
            else if (this.neuralNetwork.getLayers().size() > 2)
                connectionAddMutation();
        }
    }

    public Car getCar() {
        return car;
    }

    public String getGenomeEncoding() {
        return genomeEncoding;
    }

    public void setGenomeEncoding(String genomeEncoding) {
        this.genomeEncoding = genomeEncoding;
    }

    public ArrayList<Gene> getGenome() {
        return genome;
    }

    public void setGenome(ArrayList<Gene> genome) {
        this.genome = genome;
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public float getAdjustedFitness() {
        return adjustedFitness;
    }

    public void setAdjustedFitness(float adjustedFitness) {
        this.adjustedFitness = adjustedFitness;
    }
}
