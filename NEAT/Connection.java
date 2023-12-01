package NEAT;

public class Connection {
    private Node inputNode;
    private Node outputNode;
    private float weight;
    private boolean enabled;

    public void feedOutputNode() {
        this.outputNode.setValue(this.outputNode.getValue() + this.inputNode.getValue() * this.weight);
    }

    public Connection(Node input, Node output, float weight, boolean enabled) {
        this.inputNode = input;
        this.outputNode = output;
        this.weight = weight;
        this.enabled = enabled;
    }

    public Connection(Connection other) {
        this.inputNode = other.getInputNode();
        this.outputNode = other.outputNode;
        this.weight = other.weight;
        this.enabled = other.enabled;
    }

    public Node getInputNode() {
        return inputNode;
    }
    public Node getOutputNode() {
        return outputNode;
    }
    public float getWeight() {
        return weight;
    }
    public boolean getEnabled() {
        return enabled;
    }

    public void setInputNode(Node inputNode) {
        this.inputNode = inputNode;
    }
    public void setOutputNode(Node outputNode) {
        this.outputNode = outputNode;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
