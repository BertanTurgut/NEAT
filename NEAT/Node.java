package NEAT;

public class Node {
    private String name;
    private float value;

    public Node(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public Node(Node other) {
        this.name = other.getName();
        this.value = other.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
