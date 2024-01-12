package Physics;

import java.lang.reflect.Array;
import java.util.ArrayList;

// TODO: implement collision detection
public class Object {
    public static ArrayList<Object> objects = new ArrayList<>();

    private int id;
    private ArrayList<Vertice> vertices; // vertices must be in sequantial order
    private ArrayList<Object> ignore; // TODO: objects' ignore lists will be filled when the car agents are created
    private float[] centerOfMass; // index 0: x; index 1: y
    private float rotation; // in degrees
    private float velocity;
    private float acceleration;

    public Object(ArrayList<Vertice> vertices) {
        this.id = objects.size();
        objects.add(this);
        this.vertices = new ArrayList<>(vertices);
        this.ignore = new ArrayList<>();
        this.ignore.add(this);
        this.centerOfMass = new float[] {0, 0};
        for (Vertice vertex : this.vertices) {
            this.centerOfMass[0] += vertex.x;
            this.centerOfMass[1] += vertex.y;
        }
        this.centerOfMass[0] /= this.vertices.size();
        this.centerOfMass[1] /= this.vertices.size();
        this.rotation = 0;
        this.velocity = 0;
        this.acceleration = 0;
    }

    // TODO: correct the car controls
    public void update() {
        for (Vertice vertex : this.vertices) {
            float relativeX = vertex.x - this.centerOfMass[0];
            float relativeY = vertex.y - this.centerOfMass[1];
            float relativeX2 = (float) (relativeX * Math.cos(Math.toRadians(this.rotation)) - relativeY * Math.sin(Math.toRadians(this.rotation)));
            float relativeY2 = (float) (relativeY * Math.cos(Math.toRadians(this.rotation)) + relativeX * Math.sin(Math.toRadians(this.rotation)));
            vertex.x = this.centerOfMass[0] + relativeX2;
            vertex.y = this.centerOfMass[1] + relativeY2;
        }
        for (Vertice vertex : this.vertices) {
            vertex.x += this.velocity * Math.sin(Math.toRadians(this.rotation));
            vertex.y += this.velocity * Math.cos(Math.toRadians(this.rotation));
        }
        for (Vertice vertex : this.vertices) {
            this.centerOfMass[0] += vertex.x;
            this.centerOfMass[1] += vertex.y;
        }
        this.centerOfMass[0] /= this.vertices.size();
        this.centerOfMass[1] /= this.vertices.size();
        this.velocity += this.acceleration;
        if (this.velocity > Car.speedLimit)
            this.velocity = Car.speedLimit;
        else if (this.velocity < -Car.speedLimit)
            this.velocity = -Car.speedLimit;

    }

    @Override
    public String toString() {
        return "Object " + this.id + ":\nCenter Of Mass Coordinates: {" + this.centerOfMass[0]
                + ", " + this.centerOfMass[1] + "}\nSpeed: " + this.velocity + "\nAcceleration: " + this.acceleration +
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
            this.centerOfMass[0] += vertex.x;
            this.centerOfMass[1] += vertex.y;
        }
        this.centerOfMass[0] /= this.vertices.size();
        this.centerOfMass[1] /= this.vertices.size();
    }

    public ArrayList<Object> getIgnore() {
        return ignore;
    }

    public float[] getCenterOfMass() {
        return centerOfMass;
    }

    public void setCenterOfMass(float[] centerOfMass) {
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
