package NEAT;

import NEAT.Gene;
import NEAT.NeuralNetwork;
import Physics.Car;

import java.util.ArrayList;

public class Agent {
    private Car car;
    private String genomeEncoding;
    private ArrayList<Gene> genome;
    private NeuralNetwork neuralNetwork;

    public Agent(Car car, String genomeEncoding) {
        this.car = car;
        this.genomeEncoding = genomeEncoding;
        this.genome = Gene.decodeGenome(this.genomeEncoding);
        this.neuralNetwork = Gene.createNNFromGenome(this.genome);
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

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }
}
