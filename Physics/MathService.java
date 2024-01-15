package Physics;

import java.util.ArrayList;

public class MathService {
    public static float getDistanceBetweenPoints(Vertice vertex1, Vertice vertex2) {
        return (float) Math.sqrt((vertex2.x - vertex1.x)*(vertex2.x - vertex1.x) + (vertex2.y - vertex1.y)*(vertex2.y - vertex1.y));
    }

    // box vertices must be in sequential order and have the shape of rectangle
    public static boolean isVertexInsideBox(Vertice vertex, ArrayList<Vertice> box) {
        float slope = (box.get(1).y - box.get(0).y) / (box.get(1).x - box.get(0).x);
        double radian = Math.atan(slope);
        float relativeX = vertex.x - box.get(0).x;
        float relativeY = vertex.y - box.get(0).y;
        float rotatedX = (float) (relativeX * Math.cos(radian) + relativeY * Math.sin(radian));
        float rotatedY = (float) (relativeY * Math.cos(radian) - relativeX * Math.sin(radian));
        ArrayList<Vertice> rotatedBox = new ArrayList<>();
        for (Vertice vertex2 : box) {
            float tempX = (float) ((vertex2.x - box.get(0).x) * Math.cos(radian) + (vertex2.y - box.get(0).y) * Math.sin(radian));
            float tempY = (float) ((vertex2.y - box.get(0).y) * Math.cos(radian) - (vertex2.x - box.get(0).x) * Math.sin(radian));
            rotatedBox.add(new Vertice(tempX, tempY));
        }
        float minX = rotatedBox.get(0).x;
        float minY = rotatedBox.get(0).y;
        float maxX = rotatedBox.get(0).x;
        float maxY = rotatedBox.get(0).y;
        for (Vertice vertex2 : rotatedBox) {
            if (vertex2.x < minX)
                minX = vertex2.x;
            if (vertex2.x > maxX)
                maxX = vertex2.x;
            if (vertex2.y < minY)
                minY = vertex2.y;
            if (vertex2.y > maxY)
                maxY = vertex2.y;
        }
        return rotatedX >= minX && rotatedX <= maxX && rotatedY >= minY && rotatedY <= maxY;
    }

    public static Vertice getFirstIntersectionPoint(Vertice start, Vertice end, Object object, float precision) {
        Vertice iteratorVertex = new Vertice(start.x, start.y);
        float radian = (float) Math.atan((end.y - start.y) / (end.x - start.x));
        while (!isVertexInsideBox(iteratorVertex, object.getVertices()) && Math.abs(iteratorVertex.x - start.x) < Math.abs(end.x - start.x)) {
            iteratorVertex.x += Math.cos(radian) * precision;
            iteratorVertex.y += Math.sin(radian) * precision;
        }
        if (isVertexInsideBox(iteratorVertex, object.getVertices()))
            return iteratorVertex;
        else
            return null;
    }

    public static void setRotationAroundPoint(Vertice center, Vertice rotated, float rotationDegree) {
        float radius = getDistanceBetweenPoints(center, rotated);
        float relativeX = (float) (Math.cos(Math.toRadians(rotationDegree)) * radius);
        float relativeY = (float) (Math.sin(Math.toRadians(rotationDegree)) * radius);
        rotated.x = center.x + relativeX;
        rotated.y = center.y + relativeY;
    }
}
