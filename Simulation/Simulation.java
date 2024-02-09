package Simulation;

import Graphics.TestAnimation;
import NEAT.*;
import Physics.Car;
import Physics.Object;
import Physics.Vertice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation {
    public ArrayList<Agent> agents;
    public ArrayList<Species> species;
    public int generation = 0;
    
    public Simulation() {
        this.agents = new ArrayList<>();
        this.species = new ArrayList<>();
        this.generation = 0;
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
        for (int i = 0; i < 400; i++) {
            Car car = new Car(width, length, cmX, cmY, rotation);
            this.agents.add(new Agent(car, Gene.encodeGenome(initialGenes)));
        }
    }
    
    public void sortIntoSpecies() {
        agentLoop:
        for (Agent agent : this.agents) {
            for (Species species : this.species)
                if (Agent.areAgentsCompatible(agent, species.getRepresentativeAgent())) {
                    species.addAgentToSpecies(agent);
                    species.selectRepresentative();
                    continue agentLoop;
                }
            this.species.add(new Species(agent));
        }
    }

    public void simulateAgents(int timeInSeconds, ArrayList<Vertice> parkPlot) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(8);
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            Runnable runnable = () -> {
                AtomicInteger elapsedTime = new AtomicInteger();
                Timer timer = new Timer(10, e -> {
                    for (int j = finalI * 50; j < finalI * 50 + 50; j++)
                        try {
                            this.agents.get(j).drive(parkPlot);
                        } catch (Exception exception) {
                            throw new RuntimeException("! PROBLEM OCCURRED:\n" + agents.get(j).getNeuralNetwork());
                        }
                    elapsedTime.addAndGet(10);
                    if (elapsedTime.get() >= timeInSeconds * 100) {
                        ((Timer) e.getSource()).stop();
                        latch.countDown();
                        // System.out.println("Finished thread.");
                    }
                });
                timer.start();
            };
            Thread thread = new Thread(runnable);
            threads[i] = thread;
            // System.out.println("Built thread.");
        }
        for (Thread thread : threads)
            thread.start();
        latch.await();
    }

    public void calculateFitness() {
        for (Species species : species)
            for (Agent agent : agents) {
                agent.calculateFitness();
                agent.calculateAdjustedFitness(species);
            }
        for (Species species : species)
            species.calculateAverageAdjustedFitness();
        for (Species species : species)
            species.calculateOffspringClaim();
    }

    /**
     * Process will not terminate or generate new agents. Instead, agents' neural networks and genomes will be updated.
     */
    public void mateAgents() {
        ArrayList<ArrayList<Gene>> offspringGenomes = new ArrayList<>();
        for (Species species : this.species) {
            species.sortAgents();
            for (int i = 0; i < species.getOffspringClaim(); i++) {
                if (species.getAgents().size() == 1) {
                    offspringGenomes.add(species.getAgents().get(0).getGenome());
                    continue;
                }
                Agent agent1;
                Agent agent2;
                if (species.getAgents().size() <= 3) {
                    agent1 = species.getAgents().get(0);
                    agent2 = species.getAgents().get(1);
                }
                else { // if species' agent count is greater than 3 mating will occur between the fittest half
                    agent1 = species.getAgents().get(i % ((species.getAgents().size()) / 2));
                    agent2 = species.getAgents().get((i + 1) % ((species.getAgents().size()) / 2));
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
     */
    public void betweenStagesReset(float cmX, float cmY, float rotation) {
        for (Agent agent : this.agents) {
            agent.getCar().resetCar(cmX, cmY, rotation);
            agent.setFitness(0);
            agent.setAdjustedFitness(0);
        }
        this.species.clear();
        Species.species.clear();
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
     */
    public void process(ArrayList<Vertice> parkPlot, int generationLimit) {
        initiateInnovations();
        //System.out.println("0");
        initiateAgents(20, 45, 400,400, 90);
        //System.out.println("1");
        for (int i = 0; i < generationLimit; i++) {
            mutateAgents();
            //System.out.println("2");
            try {
                simulateAgents(20, parkPlot);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println("3");
            sortIntoSpecies();
            //System.out.println("4");
            calculateFitness();
            //System.out.println("5");
            mateAgents();
            //System.out.println("6");
            float bestFitness = this.agents.get(0).getFitness();
            Agent fitAgent = this.agents.get(0);
            for (Agent agent : this.agents)
                if (agent.getFitness() > bestFitness) {
                    fitAgent = agent;
                    bestFitness = agent.getFitness();
                }
            System.out.println("#==============#\nGENERATION: " + generation);
            System.out.println("Species count: " + this.species.size());
            System.out.println();
            for (Species species : species)
                System.out.print("[Size: " + species.getAgents().size() + "; Offspring Claim: " + species.getOffspringClaim() + "]");
            System.out.println("\n");
            System.out.println("Fittest agent's fitness: " + bestFitness + "\n#==============#");
            if (i < generationLimit - 1) {
                betweenStagesReset(50, 50, 0);
                //System.out.println("7");
            }
            generation++;
        }

        //System.out.println(Gene.innovations);
        for (int i = 0; i < this.species.size(); i++)
            for (Agent agent : this.species.get(i).getAgents())
                System.out.println("Species " + i + "; Fitness: " + agent.getFitness() + "; Adjusted Fitness: " + agent.getAdjustedFitness() + "; Genome Encoding: " + agent.getGenomeEncoding());
    }

    private class CarDepiction extends JComponent {
        private static final int frameHeight = 800;

        private Agent agent;
        private ArrayList<Vertice> parkPlot;

        public CarDepiction(Agent agent, ArrayList<Vertice> parkPlot) {
            this.agent = agent;
            this.parkPlot = parkPlot;
        }

        @Override
        public void paintComponent(Graphics g) {
            ((Graphics2D)g).setStroke(new BasicStroke(2));
            g.setColor(new Color(107, 102, 102));
            drawVertices(this.agent.getCar().getBody().getVertices(), g);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(250, 207, 33));
            int cmX = (int) this.agent.getCar().getBody().getCenterOfMass().x;
            int cmY = frameHeight - (int) this.agent.getCar().getBody().getCenterOfMass().y;
            for (int i = 0; i < this.agent.getCar().getVisionSensorDetections().length; i++) {
                int x2 = (int) this.agent.getCar().getVisionSensorDetections()[i].x;
                int y2 = frameHeight - (int) this.agent.getCar().getVisionSensorDetections()[i].y;
                g.drawLine(cmX, cmY, x2, y2);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(9, 182, 3));
            drawVertices(parkPlot, g);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(0, 0, 0));
            for (Object object : Object.objects)
                if (!Car.ignore.contains(object))
                    drawVertices(object.getVertices(), g);
        }

        public static void drawVertices(ArrayList<Vertice> vertices, Graphics g) {
            for (int i = 0; i < 4; i++) {
                int x1 = (int) vertices.get(i).x;
                int y1 = frameHeight - (int) vertices.get(i).y;
                int x2, y2;
                if (i == 3) {
                    x2 = (int) vertices.get(0).x;
                    y2 = frameHeight - (int) vertices.get(0).y;
                }
                else {
                    x2 = (int) vertices.get(i + 1).x;
                    y2 = frameHeight - (int) vertices.get(i + 1).y;
                }
                g.drawLine(x1, y1, x2, y2);
            }
        }

        public Agent getAgent() {
            return agent;
        }

        public void setAgent(Agent agent) {
            this.agent = agent;
        }

        public ArrayList<Vertice> getParkPlot() {
            return parkPlot;
        }

        public void setParkPlot(ArrayList<Vertice> parkPlot) {
            this.parkPlot = parkPlot;
        }
    }
}
