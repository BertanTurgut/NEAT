package Physics;

import java.util.ArrayList;
import java.util.Arrays;

public class Car {
    public static final float visionSensorPrecision = 0.1f; // vision sensor rays cannot make continuous measurement
    public static final float visionSensorRange = 80;
    public static final float acceleration = 0.25f;
    public static final float speedLimit = 0.75f;
    public static final float rotationDegreeDelta = 2;

    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<Object> ignore = new ArrayList<>(); // ignore other cars physically during simulations

    private int carId;
    private Object body;
    private float width;
    private float length;
    private float[] visionSensors; // index n: nth sensor, counterclockwise increment starting from 0 degrees
    private float[] targetDistanceSensor; // index 0: x; index 1: y
    private boolean[] parkAreaCheckSensor; // index n: nth sensor, clockwise iteration starting from top left corner

    public Car(float width, float length, float cmX, float cmY, float rotation) {
        this.carId = cars.size();
        cars.add(this);
        this.width = width;
        this.length = length;
        this.visionSensors = new float[16]; // 16 sensors, 22.5 degrees between each ray, sequantial order starting from 0 degrees iterating counterclockwise
        Arrays.fill(this.visionSensors, visionSensorRange);
        this.targetDistanceSensor = new float[] {0, 0};
        this.parkAreaCheckSensor = new boolean[] {false, false, false, false};
        Vertice forwardLeft = new Vertice(cmX + length / 2, cmY + width / 2);
        Vertice forwardRight = new Vertice(cmX + length / 2, cmY - width / 2);
        Vertice backwardRight = new Vertice(cmX - length / 2, cmY - width / 2);
        Vertice backwardLeft = new Vertice(cmX - length / 2, cmY + width / 2);
        Vertice tempCM = new Vertice(cmX, cmY);
        float structuralDegree = (float) Math.toDegrees(Math.atan(width / length));
        MathService.setRotationAroundPoint(tempCM, forwardLeft, rotation + structuralDegree);
        MathService.setRotationAroundPoint(tempCM, forwardRight, rotation - structuralDegree);
        MathService.setRotationAroundPoint(tempCM, backwardRight, rotation + 180 + structuralDegree);
        MathService.setRotationAroundPoint(tempCM, backwardLeft, rotation + 180 - structuralDegree);
        ArrayList<Vertice> vertices = new ArrayList<>();
        vertices.add(forwardLeft);
        vertices.add(forwardRight);
        vertices.add(backwardRight);
        vertices.add(backwardLeft);
        this.body = new Object(vertices, rotation);
        ignore.add(this.body);
    }

    public void updateVisionSensors() {
        float degreeIterator = this.body.getRotation();
        for (int i = 0; i < this.visionSensors.length; i++) {
            float x = (float) (this.body.getCenterOfMass().x + Math.cos(Math.toRadians(degreeIterator)) * visionSensorRange);
            float y = (float) (this.body.getCenterOfMass().y + Math.sin(Math.toRadians(degreeIterator)) * visionSensorRange);
            Vertice rayEnd = new Vertice(x, y);
            degreeIterator += 22.5f;
            for (Object object : Object.objects) {
                if (!ignore.contains(object)) {
                    Vertice intersection = MathService.getFirstIntersectionPoint(this.body.getCenterOfMass(), rayEnd, object, visionSensorPrecision);
                    if (intersection != null)
                        this.visionSensors[i] = MathService.getDistanceBetweenPoints(this.body.getCenterOfMass(), rayEnd);
                    else
                        this.visionSensors[i] = visionSensorRange;
                }
            }
        }
    }

    public void updateTargetDistanceSensor(ArrayList<Vertice> parkingPlot) {
        float parkingPlotCM_X = 0, parkingPlotCM_Y = 0;
        for (Vertice vertex : parkingPlot) {
            parkingPlotCM_X += vertex.x;
            parkingPlotCM_Y += vertex.y;
        }
        parkingPlotCM_X /= parkingPlot.size();
        parkingPlotCM_Y /= parkingPlot.size();
        this.targetDistanceSensor[0] = parkingPlotCM_X - this.body.getCenterOfMass().x;
        this.targetDistanceSensor[1] = parkingPlotCM_Y - this.body.getCenterOfMass().y;
    }

    public void updateAreaCheckSensor(ArrayList<Vertice> parkingPlot) {
        for (int i = 0; i < this.body.getVertices().size(); i++)
            this.parkAreaCheckSensor[i] = MathService.isVertexInsideBox(this.body.getVertices().get(i), parkingPlot);
    }

    public void gas() {
        this.body.setVelocity(this.body.getVelocity() + acceleration);
        if (this.body.getVelocity() > speedLimit)
            this.body.setVelocity(speedLimit);
    }

    public void reverse() {
        this.body.setVelocity(this.body.getVelocity() - acceleration);
        if (this.body.getVelocity() < -speedLimit)
            this.body.setVelocity(-speedLimit);
    }

    public void brake() {
        this.body.setVelocity(0);
        this.body.setAcceleration(0);
    }

    public void steerRight() {
        if (this.body.getVelocity() > 0)
            this.body.setRotation(this.body.getRotation() - rotationDegreeDelta);
        else if (this.body.getVelocity() < 0)
            this.body.setRotation(this.body.getRotation() + rotationDegreeDelta);
    }

    public void steerLeft() {
        if (this.body.getVelocity() > 0)
            this.body.setRotation(this.body.getRotation() + rotationDegreeDelta);
        else if (this.body.getVelocity() < 0)
            this.body.setRotation(this.body.getRotation() - rotationDegreeDelta);
    }

    @Override
    public String toString() {
        String str =  "Car " + this.carId + ":\nBody Object: " + this.body.getId() + "\nCenter Of Mass Coordinates: {" + this.body.getCenterOfMass().x
                + ", " + this.body.getCenterOfMass().y + "}\nSpeed: " + this.body.getVelocity() + "\nAcceleration: " + this.body.getAcceleration() +
                "\nRotation: " + this.body.getRotation() + "\nWidth: " + this.width + "\nLength: " + this.length + "\nVision Sensors: {";
        for (int i = 0; i < this.visionSensors.length; i++) {
            if (i < this.visionSensors.length - 1)
                str += this.visionSensors[i] + ", ";
            else
                str += this.visionSensors[i];
        }
        str += "}\nTarget Distance Sensor: {" + this.targetDistanceSensor[0] + ", " + this.targetDistanceSensor[1] + "}\nPark Area Check Sensor: {";
        for (int i = 0; i < this.parkAreaCheckSensor.length; i++) {
            if (i < this.parkAreaCheckSensor.length - 1)
                str += this.parkAreaCheckSensor[i] + ", ";
            else
                str += this.parkAreaCheckSensor[i];
        }
        str += "}";
        return str;
    }

    public int getCarId() {
        return carId;
    }

    public Object getBody() {
        return body;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float[] getVisionSensors() {
        return visionSensors;
    }

    public float[] getTargetDistanceSensor() {
        return targetDistanceSensor;
    }

    public boolean[] getParkAreaCheckSensor() {
        return parkAreaCheckSensor;
    }
}
