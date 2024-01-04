package NEAT;

public class NodeGene extends Gene {
    private int id;

    public NodeGene(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
