package NEAT;

public class NodeGene extends Gene {
    private int type; // 0->sensor 1->hidden 2->output
    private int id;

    public NodeGene(int type, int id) {
        super();
        this.setGeneType(0);
        this.type = type;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
