package NEAT;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class NEAT {
    private ArrayList<NeuralNetwork> networks;
    private int agentCount;
    private float mutationProb = 0.01f; // default
    private float weightMutationProb = 0.25f; // default
    private float nodeAddMutationProb = 0.25f; // default
    private float connectionSwitchMutationProb = 0.25f; // default
    private float connectionAddMutationProb = 0.25f; // default

    public NEAT(int agentCount, NeuralNetwork initialBrain) {
        this.networks = new ArrayList<>();
        for (int i = 0; i < agentCount; i++)
            networks.add(new NeuralNetwork(initialBrain));
    }
}
