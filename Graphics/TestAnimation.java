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
import java.util.EventListener;

public class TestAnimation {
    public static final int frameWidth = 800;
    public static final int frameHeight = 800;

    public Car testCar;
    public CarDepiction carDepiction;

    public TestAnimation() {
        this.testCar = new Car(20,45, 400, 400, 30);
        this.carDepiction = new CarDepiction();
    }

    public void startAnimation() {
        JFrame frame = new JFrame("Test Animation");
        frame.add(carDepiction);

        Timer timer = new Timer(10, new TimerListener());
        timer.start();

        frame.addKeyListener(new KeyListener());

        System.out.println(this.testCar);
        for (Vertice vertex : this.testCar.getBody().getVertices())
            System.out.println("{" + vertex.x + ", " + vertex.y + "}\n");

        frame.setVisible(true);
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            testCar.getBody().update();
            carDepiction.repaint();
        }
    }

    private class CarDepiction extends JComponent {
        @Override
        public void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0));
            for (int i = 0; i < 4; i++) {
                int x1 = (int) testCar.getBody().getVertices().get(i).x;
                int y1 = (int) testCar.getBody().getVertices().get(i).y;
                int x2, y2;
                if (i == 3) {
                    x2 = (int) testCar.getBody().getVertices().get(0).x;
                    y2 = (int) testCar.getBody().getVertices().get(0).y;
                }
                else {
                    x2 = (int) testCar.getBody().getVertices().get(i + 1).x;
                    y2 = (int) testCar.getBody().getVertices().get(i + 1).y;
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
}
