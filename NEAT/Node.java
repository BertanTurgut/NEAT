package NEAT;

public class Node {
    private int id;
    private float value;

    public Node(int id, float value) {
        this.id = id;
        this.value = value;
    }

    public Node(Node other) {
        this.id = other.getId();
        this.value = other.getValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
