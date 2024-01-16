package Physics;

import java.util.ArrayList;
import java.util.Arrays;

public class Car {
    public static final float visionSensorPrecision = 4f; // vision sensor rays cannot make continuous measurement
    public static final float visionSensorRange = 100;
    public static final float acceleration = 0.2f;
    public static final float speedLimit = 0.75f;
    public static final float rotationDegreeDelta = 2;

    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<Object> ignore = new ArrayList<>(); // ignore other cars physically during simulations

    private int carId;
    private Object body;
    private float width;
    private float length;
    private float[] visionSensors; // index n: nth sensor, counterclockwise increment starting from 0 degrees
    private Vertice[] visionSensorDetections;
    private float[] targetDistanceSensor; // index 0: x; index 1: y
    private boolean[] parkAreaCheckSensor; // index n: nth sensor, clockwise iteration starting from top left corner
    private int collisionCount;
    private boolean isColliding;
    private float parkPlotAlignment;

    public Car(float width, float length, float cmX, float cmY, float rotation) {
        this.carId = cars.size();
        cars.add(this);
        this.width = width;
        this.length = length;
        this.visionSensors = new float[16]; // 16 sensors, 22.5 degrees between each ray, sequantial order starting from 0 degrees iterating counterclockwise
        this.visionSensorDetections = new Vertice[16];
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
        this.collisionCount = 0;
        this.isColliding = false;
        this.parkPlotAlignment = 0;
        ignore.add(this.body);
    }

    public void update(ArrayList<Vertice> parkPlot) {
        this.body.update();
        this.updateVisionSensors();
        this.updateTargetDistanceSensor(parkPlot);
        this.updateAreaCheckSensor(parkPlot);
        this.collisionDetection();
        if (MathService.getDistanceBetweenPoints(parkPlot.get(0), parkPlot.get(1)) >=
        MathService.getDistanceBetweenPoints(parkPlot.get(1), parkPlot.get(2))) {
            this.parkPlotAlignment = (float) Math.toDegrees(Math.atan((parkPlot.get(0).y - parkPlot.get(1).y) / (parkPlot.get(0).x - parkPlot.get(1).x)));
            if (parkPlot.get(0).x - parkPlot.get(1).x < 0)
                this.parkPlotAlignment += 180;
        }
        else {
            this.parkPlotAlignment = (float) Math.toDegrees(Math.atan((parkPlot.get(1).y - parkPlot.get(2).y) / (parkPlot.get(1).x - parkPlot.get(2).x)));
            if (parkPlot.get(1).x - parkPlot.get(2).x < 0)
                this.parkPlotAlignment += 180;
        }
    }

    // TODO: correct collisionDetection() method
    public void collisionDetection() {
        for (Object object : Object.objects) {
            if (!ignore.contains(object)) {
                int axisCollisionCount = 0;
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 4; j++) {
                        Vertice vertex1;
                        Vertice vertex2;
                        if (j == 3) {
                            vertex1 = (i == 0) ? this.body.getVertices().get(3) : object.getVertices().get(3);
                            vertex2 = (i == 0) ? this.body.getVertices().get(0) : object.getVertices().get(0);
                        } else {
                            vertex1 = (i == 0) ? this.body.getVertices().get(j) : object.getVertices().get(j);
                            vertex2 = (i == 0) ? this.body.getVertices().get(j + 1) : object.getVertices().get(j + 1);
                        }
                        float slope = (vertex2.y - vertex1.y) / (vertex2.x - vertex1.x);
                        float perpendicular = -1 / slope;
                        float axisRotation = (float) Math.atan(perpendicular);
                        float[][] vertexTable = new float[2][2]; // {car: {min, max}, object: {min, max}}
                        vertexTable[0][0] = (float) (this.body.getVertices().get(0).x * Math.cos(axisRotation) +
                                this.body.getVertices().get(0).y * Math.sin(axisRotation));
                        vertexTable[0][1] = vertexTable[0][0];
                        for (Vertice vertex : this.body.getVertices()) {
                            float rotatedX = (float) (vertex.x * Math.cos(axisRotation) + vertex.y * Math.sin(axisRotation));
                            if (rotatedX < vertexTable[0][0])
                                vertexTable[0][0] = rotatedX;
                            if (rotatedX > vertexTable[0][1])
                                vertexTable[0][1] = rotatedX;
                        }
                        vertexTable[1][0] = (float) (object.getVertices().get(0).x * Math.cos(axisRotation) +
                                object.getVertices().get(0).y * Math.sin(axisRotation));
                        vertexTable[1][1] = vertexTable[1][0];
                        for (Vertice vertex : object.getVertices()) {
                            float rotatedX = (float) (vertex.x * Math.cos(axisRotation) + vertex.y * Math.sin(axisRotation));
                            if (rotatedX < vertexTable[1][0])
                                vertexTable[1][0] = rotatedX;
                            if (rotatedX > vertexTable[1][1])
                                vertexTable[1][1] = rotatedX;
                        }
                        float carMin = vertexTable[0][0];
                        float carMax = vertexTable[0][1];
                        float objectMin = vertexTable[1][0];
                        float objectMax = vertexTable[1][1];
                        if (carMax >= objectMin && objectMax >= carMin)
                            axisCollisionCount++;
                    }
                }
                // if collides
                if (axisCollisionCount == 8) {
                    this.collisionCount++;
                    this.isColliding = true;
                }
                else
                    this.isColliding = false;
            }
        }
    }

    public void updateVisionSensors() {
        float degreeIterator = this.body.getRotation();
        for (int i = 0; i < this.visionSensors.length; i++) {
            float x = (float) (this.body.getCenterOfMass().x + Math.cos(Math.toRadians(degreeIterator)) * visionSensorRange);
            float y = (float) (this.body.getCenterOfMass().y + Math.sin(Math.toRadians(degreeIterator)) * visionSensorRange);
            Vertice rayEnd = new Vertice(x, y);
            this.visionSensors[i] = visionSensorRange;
            this.visionSensorDetections[i] = rayEnd;
            degreeIterator += 360 / (float) this.visionSensors.length;
            for (Object object : Object.objects) {
                if (!ignore.contains(object)) {
                    Vertice intersection = MathService.getFirstIntersectionPoint(this.body.getCenterOfMass(), rayEnd, object, visionSensorPrecision);
                    if (intersection != null && MathService.getDistanceBetweenPoints(this.body.getCenterOfMass(), intersection) < this.visionSensors[i]) {
                        this.visionSensors[i] = MathService.getDistanceBetweenPoints(this.body.getCenterOfMass(), intersection);
                        this.visionSensorDetections[i] = intersection;
                    }
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
    }

    public void steerRight() {
        if (this.body.getVelocity() > 0)
            this.body.setRotation(this.body.getRotation() - rotationDegreeDelta *
                    (Math.abs(this.getBody().getVelocity())) / speedLimit);
        else if (this.body.getVelocity() < 0)
            this.body.setRotation(this.body.getRotation() + rotationDegreeDelta *
                    (Math.abs(this.getBody().getVelocity())) / speedLimit);
    }

    public void steerLeft() {
        if (this.body.getVelocity() > 0)
            this.body.setRotation(this.body.getRotation() + rotationDegreeDelta *
                    (Math.abs(this.getBody().getVelocity())) / speedLimit);
        else if (this.body.getVelocity() < 0)
            this.body.setRotation(this.body.getRotation() - rotationDegreeDelta *
                    (Math.abs(this.getBody().getVelocity())) / speedLimit);
    }

    @Override
    public String toString() {
        String str =  "Car " + this.carId + ":\nBody Object: " + this.body.getId() + "\nCenter Of Mass Coordinates: {" + String.format("%.02f", this.body.getCenterOfMass().x)
                + "; " + String.format("%.02f", this.body.getCenterOfMass().y) + "}\nSpeed: " + String.format("%.02f", this.body.getVelocity()) + "\nRotation: " +
                String.format("%.01f", this.body.getRotation()) + "\nWidth: " + this.width + "\nLength: " + this.length + "\nCollision Count: " + this.collisionCount + "\nCollision: " +
                this.isColliding + "\nVision Sensors: {";
        for (int i = 0; i < this.visionSensors.length; i++) {
            if (i < this.visionSensors.length - 1)
                str += this.visionSensors[i] + ", ";
            else
                str += this.visionSensors[i];
        }
        str += "}\nTarget Distance Sensor: {" + String.format("%.02f", this.targetDistanceSensor[0]) + "; " + String.format("%.02f", this.targetDistanceSensor[1]) + "}\nPark Area Check Sensor: {";
        for (int i = 0; i < this.parkAreaCheckSensor.length; i++) {
            if (i < this.parkAreaCheckSensor.length - 1)
                str += String.format("%.02f", this.visionSensors[i]) + "; ";
            else
                str += String.format("%.02f", this.visionSensors[i]);
        }
        str += "}\nTarget Alignment Degree: " + String.format("%.02f", this.parkPlotAlignment);
        return str;
    }

    public String toStringJLabel() {
        String str =  "<html>Car " + this.carId + ":<br/>Body Object: " + this.body.getId() + "<br/>Center Of Mass Coordinates: {" + String.format("%.02f", this.body.getCenterOfMass().x)
                + "; " + String.format("%.02f", this.body.getCenterOfMass().y) + "}<br/>Speed: " + String.format("%.02f", this.body.getVelocity()) + "<br/>Rotation: " +
                String.format("%.01f", this.body.getRotation()) + "<br/>Width: " + this.width + "<br/>Length: " + this.length + "<br/>Collision Count: " + this.collisionCount + "<br/>Collision: " +
                this.isColliding + "<br/>Vision Sensors: {";
        for (int i = 0; i < this.visionSensors.length; i++) {
            if (i < this.visionSensors.length - 1)
                str += String.format("%.02f", this.visionSensors[i]) + "; ";
            else
                str += String.format("%.02f", this.visionSensors[i]);
        }
        str += "}<br/>Target Distance Sensor: {" + String.format("%.02f", this.targetDistanceSensor[0]) + "; " + String.format("%.02f", this.targetDistanceSensor[1]) + "}<br/>Park Area Check Sensor: {";
        for (int i = 0; i < this.parkAreaCheckSensor.length; i++) {
            if (i < this.parkAreaCheckSensor.length - 1)
                str += this.parkAreaCheckSensor[i] + ", ";
            else
                str += this.parkAreaCheckSensor[i];
        }
        str += "}<br/>Target Alignment Degree: " + String.format("%.02f", this.parkPlotAlignment) + "</html>";
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

    public Vertice[] getVisionSensorDetections() {
        return visionSensorDetections;
    }

    public float[] getTargetDistanceSensor() {
        return targetDistanceSensor;
    }

    public boolean[] getParkAreaCheckSensor() {
        return parkAreaCheckSensor;
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public boolean isColliding() {
        return isColliding;
    }

    public float getParkPlotAlignment() {
        return parkPlotAlignment;
    }
}
