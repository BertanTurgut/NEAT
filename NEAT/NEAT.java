package NEAT;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class NEAT {
    private ArrayList<NeuralNetwork> networks;
    private int agentCount;
    private final float mutationProb = 0.01f; // default
    private final float weightMutationProb = 0.25f; // default
    private final float nodeAddMutationProb = 0.25f; // default
    private final float connectionSwitchMutationProb = 0.25f; // default
    private final float connectionAddMutationProb = 0.25f; // default
}
