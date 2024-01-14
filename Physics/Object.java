package Physics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

// TODO: implement collision detection
public class Object {
    public static ArrayList<Object> objects = new ArrayList<>();

    private int id;
    private ArrayList<Vertice> vertices; // vertices must be in sequantial order
    private Vertice centerOfMass; // index 0: x; index 1: y
    private float rotation; // in degrees
    private float velocity;
    private float acceleration;

    public Object(ArrayList<Vertice> vertices, float rotation) {
        this.id = objects.size();
        objects.add(this);
        this.vertices = new ArrayList<>(vertices);
        this.centerOfMass = new Vertice(0, 0);
        for (Vertice vertex : this.vertices) {
            this.centerOfMass.x += vertex.x;
            this.centerOfMass.y += vertex.y;
        }
        this.centerOfMass.x /= this.vertices.size();
        this.centerOfMass.y /= this.vertices.size();
        this.rotation = rotation;
        this.velocity = 0;
        this.acceleration = 0;
    }

    public void update() {
        float width = MathService.getDistanceBetweenPoints(this.vertices.get(0), this.vertices.get(1));
        float length = MathService.getDistanceBetweenPoints(this.vertices.get(1), this.vertices.get(2));
        float structuralDegree = (float) Math.toDegrees(Math.atan(width / length));
        MathService.setRotationAroundPoint(this.centerOfMass, this.vertices.get(0), rotation + structuralDegree);
        MathService.setRotationAroundPoint(this.centerOfMass, this.vertices.get(1), rotation - structuralDegree);
        MathService.setRotationAroundPoint(this.centerOfMass, this.vertices.get(2), rotation + 180 + structuralDegree);
        MathService.setRotationAroundPoint(this.centerOfMass, this.vertices.get(3), rotation + 180 - structuralDegree);
        for (Vertice vertex : this.vertices) {
            vertex.x += this.velocity * Math.cos(Math.toRadians(this.rotation));
            vertex.y += this.velocity * Math.sin(Math.toRadians(this.rotation));
        }
        this.centerOfMass.x = 0;
        this.centerOfMass.y = 0;
        for (Vertice vertex : this.vertices) {
            this.centerOfMass.x += vertex.x;
            this.centerOfMass.y += vertex.y;
        }
        this.centerOfMass.x /= this.getVertices().size();
        this.centerOfMass.y /= this.getVertices().size();
        this.acceleration = 0;
    }

    @Override
    public String toString() {
        return "Object " + this.id + ":\nCenter Of Mass Coordinates: {" + this.centerOfMass.x
                + ", " + this.centerOfMass.y + "}\nSpeed: " + this.velocity + "\nAcceleration: " + this.acceleration +
                "\nRotation: " + this.rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vertice> vertices) {
        this.vertices = vertices;
        for (Vertice vertex : this.vertices) {
            this.centerOfMass.x += vertex.x;
            this.centerOfMass.y += vertex.y;
        }
        this.centerOfMass.x /= this.vertices.size();
        this.centerOfMass.y /= this.vertices.size();
    }

    public Vertice getCenterOfMass() {
        return centerOfMass;
    }

    public void setCenterOfMass(Vertice centerOfMass) {
        this.centerOfMass = centerOfMass;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }
}
