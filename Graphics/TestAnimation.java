package Graphics;

import NEAT.Agent;
import NEAT.ConnectionGene;
import NEAT.Gene;
import Physics.Car;
import Physics.Object;
import Physics.Vertice;
import Simulation.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// TODO: test simultaneously runnable agent limit
public class TestAnimation {
    public static final int frameWidth = 800;
    public static final int frameHeight = 800;

    public Timer timer;
    public Simulation simulation;
    public Car testCar;
    public Agent agent;
    public CarDepiction carDepiction;
    public JPanel panel;
    public JLabel carData;
    public ArrayList<Vertice> parkPlot;
    public Object object0;
    public Object object1;

    public TestAnimation() {
        this.timer = new Timer(10, new TimerListener());
        this.simulation = new Simulation();
        simulation.initiateInnovations();
        this.testCar = new Car(20,45, 400, 400, 90);
        ArrayList<Gene> initialGenes = new ArrayList<>();
        for (Gene gene : Gene.innovations) {
            if (gene instanceof ConnectionGene && !((ConnectionGene) gene).isEnabled())
                continue;
            initialGenes.add(gene);
        }
        this.agent = new Agent(testCar, Gene.encodeGenome(initialGenes));
        this.carDepiction = new CarDepiction();
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(0, 1));
        this.panel.setBackground(new Color(197, 203, 141));
        this.carData = new JLabel();
        this.parkPlot = new ArrayList<>();
        parkPlot.add(new Vertice(9.5f*10+300, 16.2f*10+600));
        parkPlot.add(new Vertice(20.3f*10+300, 12.6f*10+600));
        parkPlot.add(new Vertice(16.6f*10+300, 1.8f*10+600));
        parkPlot.add(new Vertice(5.9f*10+300, 5.4f*10+600));
        ArrayList<Vertice> object0Vertices = new ArrayList<>();
        object0Vertices.add(new Vertice(550, 400));
        object0Vertices.add(new Vertice(650, 400));
        object0Vertices.add(new Vertice(650, 300));
        object0Vertices.add(new Vertice(550, 300));
        this.object0 = new Object(object0Vertices, 55);
        ArrayList<Vertice> object1Vertices = new ArrayList<>();
        object1Vertices.add(new Vertice(-0.4f*50+300, -2.3f*50+400));
        object1Vertices.add(new Vertice(1.8f*50+300, -1.3f*50+400));
        object1Vertices.add(new Vertice(0.4f*50+300, 2.3f*50+400));
        object1Vertices.add(new Vertice(-1.8f*50+300, 1.3f*50+400));
        this.object1 = new Object(object1Vertices, 55);
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
            //testCar.update(parkPlot);
            agent.drive(parkPlot);
            carData.setText(testCar.toStringJLabel());
            carDepiction.repaint();
            //System.out.println(agent.getNeuralNetwork());
        }
    }

    private class CarDepiction extends JComponent {
        @Override
        public void paintComponent(Graphics g) {
            ((Graphics2D)g).setStroke(new BasicStroke(2));
            g.setColor(new Color(107, 102, 102));
            drawVertices(testCar.getBody().getVertices(), g);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(250, 207, 33));
            int cmX = (int) testCar.getBody().getCenterOfMass().x;
            int cmY = frameHeight - (int) testCar.getBody().getCenterOfMass().y;
            for (int i = 0; i < testCar.getVisionSensorDetections().length; i++) {
                int x2 = (int) testCar.getVisionSensorDetections()[i].x;
                int y2 = frameHeight - (int) testCar.getVisionSensorDetections()[i].y;
                g.drawLine(cmX, cmY, x2, y2);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(9, 182, 3));
            drawVertices(parkPlot, g);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(0, 0, 0));
            drawVertices(object0.getVertices(), g);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(0, 0, 0));
            drawVertices(object1.getVertices(), g);
        }
    }

    public static void drawVertices(ArrayList<Vertice> vertices, Graphics g) {
        for (int i = 0; i < 4; i++) {
            int x1 = (int) vertices.get(i).x;
            int y1 = frameHeight - (int) vertices.get(i).y;
            int x2, y2;
            if (i == 3) {
                x2 = (int) vertices.get(0).x;
                y2 = frameHeight - (int) vertices.get(0).y;
            }
            else {
                x2 = (int) vertices.get(i + 1).x;
                y2 = frameHeight - (int) vertices.get(i + 1).y;
            }
            g.drawLine(x1, y1, x2, y2);
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
