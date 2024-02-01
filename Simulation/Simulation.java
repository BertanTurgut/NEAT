package Simulation;

import NEAT.*;
import Physics.Car;
import Physics.Vertice;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation {
    public ArrayList<Agent> agents;
    public ArrayList<Species> species;
    
    public Simulation() {
        this.agents = new ArrayList<>();
        this.species = new ArrayList<>();
    }

    public void initiateInnovations() {
        int i = 0;
        ArrayList<NodeGene> inputGenes = new ArrayList<>();
        inputGenes.add(new NodeGene(i++)); // car body velocity
        inputGenes.add(new NodeGene(i++)); // car body rotation
        inputGenes.add(new NodeGene(i++)); // car width
        inputGenes.add(new NodeGene(i++)); // car length
        for (int k = 0;  k < 16; k++)
            inputGenes.add(new NodeGene(i++)); // vision sensors
        inputGenes.add(new NodeGene(i++)); // target distance sensor x
        inputGenes.add(new NodeGene(i++)); // target distance sensor y
        for (int k = 0; k < 4; k++)
            inputGenes.add(new NodeGene(i++)); // park area check sensors
        inputGenes.add(new NodeGene(i++)); // collision count
        inputGenes.add(new NodeGene(i++)); // is colliding
        inputGenes.add(new NodeGene(i++)); // park plot alignment

        ArrayList<NodeGene> outputGenes = new ArrayList<>();
        outputGenes.add(new NodeGene(i++)); // gas
        outputGenes.add(new NodeGene(i++)); // reverse
        outputGenes.add(new NodeGene(i++)); // brake
        outputGenes.add(new NodeGene(i++)); // steer right
        outputGenes.add(new NodeGene(i++)); // steer left

        Random random = new Random();
        // initially there will not be any hidden nodes
        for (NodeGene inputGene : inputGenes) {
            for (NodeGene outputGene : outputGenes) {
                new ConnectionGene(inputGene.getId(), outputGene.getId(), random.nextFloat() * 2 - 1 /* -1;1 */, true);
            }
        }
    }
    
    public void initiateAgents(float width, float length, float cmX, float cmY, float rotation) {
        ArrayList<Gene> initialGenes = new ArrayList<>();
        for (Gene gene : Gene.innovations) {
            if (gene instanceof ConnectionGene && !((ConnectionGene) gene).isEnabled())
                continue;
            initialGenes.add(gene);
        }
        for (int i = 0; i < 300; i++) {
            Car car = new Car(width, length, cmX, cmY, rotation);
            this.agents.add(new Agent(car, Gene.encodeGenome(initialGenes)));
        }
    }
    
    public void sortIntoSpecies() {
        for (Agent agent : this.agents) {
            for (Species species : this.species)
                if (Agent.areAgentsCompatible(agent, species.getRepresentativeAgent())) {
                    species.addAgentToSpecies(agent);
                    species.selectRepresentative();
                    break;
                }
            this.species.add(new Species(agent));
        }
    }

    // TODO: implement simulateAgents() instance method
    public void simulateAgents(int timeInSeconds, ArrayList<Vertice> parkPlot) throws InterruptedException {
        Runnable runnable1 = () -> {
            Timer timer = new Timer(10, e -> {
                for (int i = 0; i < 50; i++)
                    this.agents.get(i).drive(parkPlot);
            });
            timer.start();
        };
        Runnable runnable2 = () -> {
            Timer timer = new Timer(10, e -> {
                for (int i = 50; i < 100; i++)
                    this.agents.get(i).drive(parkPlot);
            });
            timer.start();
        };
        Runnable runnable3 = () -> {
            Timer timer = new Timer(10, e -> {
                for (int i = 100; i < 150; i++)
                    this.agents.get(i).drive(parkPlot);
            });
            timer.start();
        };
        Runnable runnable4 = () -> {
            Timer timer = new Timer(10, e -> {
                for (int i = 150; i < 200; i++)
                    this.agents.get(i).drive(parkPlot);
            });
            timer.start();
        };
        Runnable runnable5 = () -> {
            Timer timer = new Timer(10, e -> {
                for (int i = 200; i < 250; i++)
                    this.agents.get(i).drive(parkPlot);
            });
            timer.start();
        };
        Runnable runnable6 = () -> {
            Timer timer = new Timer(10, e -> {
                for (int i = 250; i < 300; i++)
                    this.agents.get(i).drive(parkPlot);
            });
            timer.start();
        };
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);
        Thread thread4 = new Thread(runnable4);
        Thread thread5 = new Thread(runnable5);
        Thread thread6 = new Thread(runnable6);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread1.join(timeInSeconds * 1000L);
        thread2.join(timeInSeconds * 1000L);
        thread3.join(timeInSeconds * 1000L);
        thread4.join(timeInSeconds * 1000L);
        thread5.join(timeInSeconds * 1000L);
        thread6.join(timeInSeconds * 1000L);
    }

    public void calculateFitness() {
        for (Species species : species)
            for (Agent agent : agents) {
                agent.calculateFitness();
                agent.calculateAdjustedFitness(species);
            }
        Species.calculateGlobalAverage();
        for (Species species : species)
            species.calculateOffspringClaim();
    }

    /**
     * Process will not terminate or generate new agents. Instead, agents' neural networks and genomes will be updated.
     * TODO: implement mateAgents() instance method
     */
    public void mateAgents() {
        ArrayList<ArrayList<Gene>> offspringGenomes = new ArrayList<>();
        for (Species species : this.species) {
            species.sortAgents();
            for (int i = 0; i < species.getOffspringClaim(); i++) {
                Agent agent1;
                Agent agent2;
                if (i > species.getAgents().size() - 2) {
                    agent1 = species.getAgents().get(i - species.getAgents().size());
                    agent2 = species.getAgents().get(i + 1 - species.getAgents().size());
                }
                else {
                    agent1 = species.getAgents().get(i);
                    agent2 = species.getAgents().get(i + 1);
                }
                offspringGenomes.add(Gene.matchGenomes(agent1.getGenome(), agent1.getFitness(), agent2.getGenome(), agent2.getFitness()));
            }
        }
        for (int i = 0; i < this.agents.size(); i++) {
            if (i > offspringGenomes.size() || offspringGenomes.size() == 0)
                break;
            this.agents.get(i).setGenome(offspringGenomes.get(i));
            this.agents.get(i).setGenomeEncoding(Gene.encodeGenome(offspringGenomes.get(i)));
            this.agents.get(i).setNeuralNetwork(Gene.createNNFromGenome(offspringGenomes.get(i)));
        }
    }

    public void mutateAgents() {
        for (Agent agent : this.agents)
            agent.mutate();
    }

    /**
     * Resets the necessary modules of the algorithm between the stages of the process.
     * TODO: implement betweenStagesReset() instance method
     */
    public void betweenStagesReset(float cmX, float cmY, float rotation) {
        for (Agent agent : this.agents) {
            agent.getCar().resetCar(cmX, cmY, rotation);
            agent.setFitness(0);
            agent.setAdjustedFitness(0);
        }
        this.species.clear();
    }

    /**
     * Runs the whole process of the algorithm
     * 1) Initialization
     * 2) Mutation
     * 3) Simulation
     * 4) Calculate fitness
     * 5) Mate offsprings
     * 6) Reset
     * 7) Return to stage 2 until convergence
     * 8) Get final AI's neural map
     * TODO: implement process() instance method
     */
    public void process(ArrayList<Vertice> parkPlot) {
        initiateInnovations();
        System.out.println("0");
        initiateAgents(25, 40, 50,50, 0);
        System.out.println("1");
        mutateAgents();
        System.out.println("2");
        try {
            simulateAgents(10, parkPlot);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("3");
        sortIntoSpecies();
        System.out.println("4");
        calculateFitness();
        System.out.println("5");
        mateAgents();
        System.out.println("6");
        betweenStagesReset(50,50, 0);
        System.out.println("7");
    }
}
