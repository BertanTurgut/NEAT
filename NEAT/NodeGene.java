package NEAT;

public class NodeGene extends Gene {
    private int id;

    public NodeGene(int id) {
        super(false);
        this.id = id;
    }

    @Override
    public String toString() {
        return "NODE GENE:\nInnovation number: " + this.getInnovationNumber() + "\nNode ID: " + this.id + "\n";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
