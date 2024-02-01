package NEAT;

import java.util.ArrayList;
import java.util.Random;

public class Species {
    private static float globalAverage = 0;
    private static ArrayList<Species> species = new ArrayList<>();

    public static void calculateGlobalAverage() {
        int totalAgentCount = 0;
        float totalFitness = 0;
        for (Species species : species) {
            totalAgentCount += species.agents.size();
            for (Agent agent : species.agents)
                totalFitness += agent.getFitness();
        }
        globalAverage = totalFitness / totalAgentCount;
    }

    private ArrayList<Agent> agents;
    private Agent representativeAgent;
    private int offspringClaim;

    public Species(Agent agent) {
        this.agents = new ArrayList<>();
        this.agents.add(agent);
        this.representativeAgent = agent;
        this.offspringClaim = 0;
        species.add(this);
    }

    public void selectRepresentative() {
        Random random = new Random();
        this.representativeAgent = this.agents.get(random.nextInt(this.agents.size()));
    }

    public void calculateOffspringClaim() {
        float totalAdjustedFitness = 0;
        for (Agent agent : this.agents)
            totalAdjustedFitness *= agent.getAdjustedFitness();
        this.offspringClaim = (int) (this.agents.size() * totalAdjustedFitness / globalAverage);
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
}
