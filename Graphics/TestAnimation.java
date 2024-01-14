package Graphics;

import Physics.Car;
import Physics.MathService;
import Physics.Vertice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventListener;

public class TestAnimation {
    public static final int frameWidth = 800;
    public static final int frameHeight = 800;

    public Timer timer;
    public Car testCar;
    public CarDepiction carDepiction;
    public JPanel panel;
    public ArrayList<JLabel> vertexCoordinates;
    public JLabel centerOfMass;
    public JLabel carData;
    public JButton startTimer;
    public JButton stopTimer;

    public TestAnimation() {
        this.timer = new Timer(10, new TimerListener());
        this.testCar = new Car(20,45, 400, 400, 90);
        this.carDepiction = new CarDepiction();
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(0, 1));
        this.panel.setBackground(new Color(197, 203, 141));
        this.vertexCoordinates = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            this.vertexCoordinates.add(new JLabel());
        this.centerOfMass = new JLabel();
        this.carData = new JLabel();
        this.startTimer = new JButton("Start Timer");
        this.startTimer.addActionListener(new StartButtonListener());
        this.stopTimer = new JButton("Stop Timer");
        this.stopTimer.addActionListener(new StopButtonListener());
    }

    public void startAnimation() {
        JFrame frame = new JFrame("Test Animation");;

        frame.add(this.carDepiction);
        //frame.add(this.panel, BorderLayout.NORTH);
        this.timer.start();
        for (JLabel label : this.vertexCoordinates)
            this.panel.add(label);
        this.panel.add(this.centerOfMass);
        this.panel.add(this.startTimer);
        this.panel.add(this.stopTimer);

        for (int i = 0; i < this.vertexCoordinates.size(); i++)
            this.vertexCoordinates.get(i).setText(" Vertex " + i + ": {" + this.testCar.getBody().getVertices().get(i).x
                    + ", " + this.testCar.getBody().getVertices().get(i).y + "}");

        this.centerOfMass.setText(" Center Of Mass: {" + this.testCar.getBody().getCenterOfMass().x + ", " +
                this.testCar.getBody().getCenterOfMass().y + "}");

        frame.addKeyListener(new KeyListener());

        System.out.println(this.testCar);

        frame.setVisible(true);
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.getBody().update();
            for (int i = 0; i < vertexCoordinates.size(); i++)
                vertexCoordinates.get(i).setText(" Vertex " + i + ": {" + testCar.getBody().getVertices().get(i).x
                        + ", " + testCar.getBody().getVertices().get(i).y + "}");
            centerOfMass.setText(" Center Of Mass: {" + testCar.getBody().getCenterOfMass().x + ", " +
                    testCar.getBody().getCenterOfMass().y + "}");
            carDepiction.repaint();
        }
    }

    private class CarDepiction extends JComponent {
        @Override
        public void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0));
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
        }
    }

    private class KeyListener implements java.awt.event.KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_W -> testCar.gas();
                case KeyEvent.VK_S -> testCar.reverse();
                case KeyEvent.VK_D -> testCar.steerRight();
                case KeyEvent.VK_A -> testCar.steerLeft();
                case KeyEvent.VK_Z -> testCar.brake();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!timer.isRunning())
                timer.start();
        }
    }

    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning())
                timer.stop();
        }
    }
}
