package Graphics;

import Physics.Car;
import Physics.Object;
import Physics.Vertice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TestAnimation {
    public static final int frameWidth = 800;
    public static final int frameHeight = 800;

    public Timer timer;
    public Car testCar;
    public CarDepiction carDepiction;
    public JPanel panel;
    public JLabel carData;
    public ArrayList<Vertice> parkPlot;
    public Object object0;

    public TestAnimation() {
        this.timer = new Timer(10, new TimerListener());
        this.testCar = new Car(20,45, 400, 400, 90);
        this.carDepiction = new CarDepiction();
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(0, 1));
        this.panel.setBackground(new Color(197, 203, 141));
        this.carData = new JLabel();
        this.parkPlot = new ArrayList<>();
        parkPlot.add(new Vertice(9.5f*10+600, 16.2f*10+600));
        parkPlot.add(new Vertice(20.3f*10+600, 12.6f*10+600));
        parkPlot.add(new Vertice(16.6f*10+600, 1.8f*10+600));
        parkPlot.add(new Vertice(5.9f*10+600, 5.4f*10+600));
        ArrayList<Vertice> object0Vertices = new ArrayList<>();
        object0Vertices.add(new Vertice(500, 300));
        object0Vertices.add(new Vertice(600, 300));
        object0Vertices.add(new Vertice(600, 200));
        object0Vertices.add(new Vertice(500, 200));
        this.object0 = new Object(object0Vertices, 55);
    }

    public void startAnimation() {
        JFrame frame = new JFrame("Test Animation");;

        frame.add(this.carDepiction);
        frame.add(this.panel, BorderLayout.NORTH);
        this.timer.start();
        this.panel.add(carData);

        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "gas");
        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "reverse");
        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "steerLeft");
        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "steerRight");
        this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), "brake");

        this.panel.getActionMap().put("gas", new Gas());
        this.panel.getActionMap().put("reverse", new Reverse());
        this.panel.getActionMap().put("steerLeft", new SteerLeft());
        this.panel.getActionMap().put("steerRight", new SteerRight());
        this.panel.getActionMap().put("brake", new Brake());

        frame.setVisible(true);
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.update(parkPlot);
            carData.setText(testCar.toStringJLabel());
            carDepiction.repaint();
        }
    }

    private class CarDepiction extends JComponent {
        @Override
        public void paintComponent(Graphics g) {
            ((Graphics2D)g).setStroke(new BasicStroke(5));
            g.setColor(new Color(107, 102, 102));
            for (int i = 0; i < 4; i++) {
                int x1 = (int) testCar.getBody().getVertices().get(i).x;
                int y1 = frameHeight - (int) testCar.getBody().getVertices().get(i).y;
                int x2, y2;
                if (i == 3) {
                    x2 = (int) testCar.getBody().getVertices().get(0).x;
                    y2 = frameHeight - (int) testCar.getBody().getVertices().get(0).y;
                }
                else {
                    x2 = (int) testCar.getBody().getVertices().get(i + 1).x;
                    y2 = frameHeight - (int) testCar.getBody().getVertices().get(i + 1).y;
                }
                g.drawLine(x1, y1, x2, y2);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(250, 207, 33));
            int cmX = (int) testCar.getBody().getCenterOfMass().x;
            int cmY = frameHeight - (int) testCar.getBody().getCenterOfMass().y;
            for (int i = 0; i < testCar.getVisionSensorDetections().length; i++) {
                int x2 = (int) testCar.getVisionSensorDetections()[i].x;
                int y2 = frameHeight - (int) testCar.getVisionSensorDetections()[i].y;
                g.drawLine(cmX, cmY, x2, y2);
            }
            g.setColor(new Color(0, 0, 0));
            for (int i = 0; i < 4; i++) {
                int x1 = (int) parkPlot.get(i).x;
                int y1 = frameHeight - (int) parkPlot.get(i).y;
                int x2, y2;
                if (i == 3) {
                    x2 = (int) parkPlot.get(0).x;
                    y2 = frameHeight - (int) parkPlot.get(0).y;
                }
                else {
                    x2 = (int) parkPlot.get(i + 1).x;
                    y2 = frameHeight - (int) parkPlot.get(i + 1).y;
                }
                g.drawLine(x1, y1, x2, y2);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(1));
            g.setColor(new Color(0, 0, 0));
            for (int i = 0; i < 4; i++) {
                int x1 = (int) object0.getVertices().get(i).x;
                int y1 = frameHeight - (int) object0.getVertices().get(i).y;
                int x2, y2;
                if (i == 3) {
                    x2 = (int) object0.getVertices().get(0).x;
                    y2 = frameHeight - (int) object0.getVertices().get(0).y;
                }
                else {
                    x2 = (int) object0.getVertices().get(i + 1).x;
                    y2 = frameHeight - (int) object0.getVertices().get(i + 1).y;
                }
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private class Gas extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.gas();
        }
    }

    private class Reverse extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.reverse();
        }
    }

    private class SteerLeft extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.steerLeft();
        }
    }

    private class SteerRight extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.steerRight();
        }
    }

    private class Brake extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.brake();
        }
    }
}
