package NEAT;

public class ConnectionGene extends Gene {
    private int inId;
    private int outId;
    private float weight;
    private boolean enabled;

    public ConnectionGene(int inId, int outId, float weight, boolean enabled) {
        super();
        this.setGeneType(1);
        this.inId = inId;
        this.outId = outId;
        this.weight = weight;
        this.enabled = enabled;
    }

    public int getInId() {
        return inId;
    }

    public void setInId(int inId) {
        this.inId = inId;
    }

    public int getOutId() {
        return outId;
    }

    public void setOutId(int outId) {
        this.outId = outId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
