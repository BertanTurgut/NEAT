package NEAT;

import java.util.ArrayList;
import java.util.Random;

public class Species {
    public static ArrayList<Species> species = new ArrayList<>();

    private ArrayList<Agent> agents;
    private Agent representativeAgent;
    private int offspringClaim;
    private float averageAdjustedFitness;

    public Species(Agent agent) {
        this.agents = new ArrayList<>();
        this.agents.add(agent);
        this.representativeAgent = agent;
        this.offspringClaim = 0;
        this.averageAdjustedFitness = 0;
        species.add(this);
    }

    public void selectRepresentative() {
        Random random = new Random();
        this.representativeAgent = this.agents.get(random.nextInt(this.agents.size()));
    }

    public void calculateAverageAdjustedFitness() {
        float totalAdjustedFitness = 0;
        for (Agent agent : this.agents)
            totalAdjustedFitness += agent.getAdjustedFitness();
        this.averageAdjustedFitness = totalAdjustedFitness / this.agents.size();
    }

    public void calculateOffspringClaim() {
        int populationSize = 0;
        float speciesAverageAdjustedFitnessSum = 0;
        for (Species species : species) {
            populationSize += species.agents.size();
            speciesAverageAdjustedFitnessSum += species.averageAdjustedFitness;
        }
        this.offspringClaim = (int) ((this.averageAdjustedFitness / speciesAverageAdjustedFitnessSum) * populationSize);
    }

    /**
     * Sort agents in descending order according to their adjusted fitness's
     */
    public void sortAgents() {
        this.agents.sort((Agent a1, Agent a2) -> {
            if (a1.getAdjustedFitness() - a2.getAdjustedFitness() > 0)
                return -1;
            else if (a1.getAdjustedFitness() - a2.getAdjustedFitness() < 0)
                return 1;
            else
                return 0;
        });
    }

    public void addAgentToSpecies(Agent agent) {
        this.agents.add(agent);
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public Agent getRepresentativeAgent() {
        return representativeAgent;
    }

    public int getOffspringClaim() {
        return offspringClaim;
    }

    public float getAverageAdjustedFitness() {
        return averageAdjustedFitness;
    }
}
