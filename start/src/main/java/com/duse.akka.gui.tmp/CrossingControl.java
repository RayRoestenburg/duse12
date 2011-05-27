package com.duse.akka.gui.tmp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

class CrossingMain {

    public static void main(String[] args) {
        Frame f = assembleFrame();
        f.pack();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static Frame assembleFrame() {
        Frame f = new Frame("Crossing");
        JPanel crossing = new ImagePanel("/crossing.png");
        crossing.setLayout(null);
        SensorListener listener = new DummySensorListener();
        TrafficLight north = new TrafficLight(HEADING.NORTH, 570, 280);
        Sensor northSens = new Sensor(north, listener, 375, 530);
        TrafficLight east = new TrafficLight(HEADING.EAST, 120, 280);
        Sensor eastSens = new Sensor(east, listener, 20, 105);
        TrafficLight west = new TrafficLight(HEADING.WEST, 570, 40);
        Sensor westSens = new Sensor(west, listener, 840, 105);
        crossing.add(new SensorRandomizer(0, 0, northSens, eastSens, westSens));
        crossing.add(northSens);
        crossing.add(north);
        crossing.add(eastSens);
        crossing.add(east);
        crossing.add(westSens);
        crossing.add(west);
        f.add(crossing);
        return f;
    }

    private static class DummySensorListener implements SensorListener {
        /**
         * Dummy listener that simulates some behaviour.
         * For the final implementation this behaviour is not wanted.
         * It is not desirable for the sensor to change the state
         * of his corresponding trafficlight.
         *
         * @param sensor sensor
         */
        public void onActivation(Sensor sensor) {
            if (sensor.getQueued() % 5 == 0) {
                sensor.getTrafficLight().switchToRed();
            } else if (sensor.getQueued() % 7 == 0) {
                sensor.getTrafficLight().switchToGreen();
            }
        }
    }
}

//==============================================
// Main Panel
//==============================================
class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel(String imagePath) {
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException ex) {
            throw new RuntimeException("Image not found for path " + imagePath);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(null),
                image.getHeight(null));
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}

enum HEADING {
    NORTH, SOUTH, EAST, WEST;
}

//==============================================
// Sensor
//==============================================

interface SensorListener {
    public void onActivation(Sensor sensor);
}

class Sensor extends JButton implements ActionListener {

    int queued = 0;
    private TrafficLight trafficLight;
    private SensorListener sensorListener;

    Sensor(TrafficLight trafficLight, SensorListener sensorListener, int x, int y) {
        super(createText("0"));
        this.trafficLight = trafficLight;
        this.sensorListener = sensorListener;
        this.trafficLight.setSensor(this);
        int width = 150;
        int height = 30;
        if (trafficLight.isVertical()) {
            width = 30;
            height = 150;
        }
        setBounds(x, y, width, height);
        addActionListener(this);
    }


    public void actionPerformed(ActionEvent e) {
        increment();
        sensorListener.onActivation(this);
    }

    private static String createText(String text) {
        return "<html>" + text + "</html>";
    }

    private synchronized void increment() {
        queued++;
        setText(createText("" + queued));
    }

    public synchronized void decrement() {
        queued--;
        setText(createText("" + queued));
    }

    public int getQueued() {
        return queued;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }
}

class SensorRandomizer extends JButton implements ActionListener {
    Sensor[] sensors;
    boolean active;
    Timer randomIncrementor = new Timer(200, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int randomIndex = Math.abs(new Random().nextInt() % sensors.length);
            sensors[randomIndex].actionPerformed(e);
        }
    });

    SensorRandomizer(int x, int y, Sensor... sensors) {
        this.sensors = sensors;
        addActionListener(this);
        setText("Start random queuing");
        setBounds(x, y, (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
    }


    public void actionPerformed(ActionEvent e) {
        if (!active) {
            randomIncrementor.start();
            active = true;
            setText("Stop random queuing");
        } else {
            randomIncrementor.stop();
            active = false;
            setText("Start random queuing");
        }
    }
}


//==============================================
// Trafficlight
//==============================================

class TrafficLight extends JPanel {
    private TrafficLightLamp green = new TrafficLightLamp(Color.green);
    private TrafficLightLamp yellow = new TrafficLightLamp(Color.yellow);
    private TrafficLightLamp red = new TrafficLightLamp(Color.red);
    private final Object lock = new Object();
    HEADING heading = HEADING.NORTH;
    Sensor sensor;
    Timer dequeueTimer;

    TrafficLight(HEADING heading) {
        super(defineLayout(heading));
        this.heading = heading;
        for (JPanel signal : defineOrder(heading)) {
            add(signal);
        }
    }

    TrafficLight(HEADING heading, int x, int y) {
        this(heading);
        setBounds(x, y, (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
    }

    public boolean isVertical() {
        return heading == HEADING.EAST || heading == HEADING.WEST;
    }


    private static GridLayout defineLayout(HEADING heading) {
        if (heading == HEADING.NORTH || heading == HEADING.SOUTH) {
            return new GridLayout(0, 1);
        }
        return new GridLayout(0, 5);
    }

    private JPanel[] defineOrder(HEADING heading) {
        if (heading == HEADING.NORTH || heading == HEADING.WEST) {
            JPanel[] order = {red, yellow, green, new TrafficLightPost(heading), new TrafficLightPost(heading)};
            return order;
        }
        JPanel[] order = {new TrafficLightPost(heading), new TrafficLightPost(heading), green, yellow, red};
        return order;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public synchronized void switchToRed() {
        new SwingWorker<Void, Object>() {
            @Override
            public Void doInBackground() {
                synchronized (lock) {
                    green.turnOff();
                    yellow.turnOn();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                    yellow.turnOff();
                    red.turnOn();
                    stopTimer();
                }
                return null;
            }
        }.execute();
    }

    private void stopTimer() {
        if (dequeueTimer != null) {
            dequeueTimer.stop();
        }

    }

    public void switchToGreen() {
        synchronized (lock) {
            red.turnOff();
            green.turnOn();
            stopTimer();
            dequeueTimer = new Timer(300, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (sensor.getQueued() <= 0) {
                        ((Timer) e.getSource()).stop();
                    } else {
                        sensor.decrement();
                    }
                }
            });
            dequeueTimer.start();
        }
    }

    class TrafficLightPost extends JPanel {
        final BasicStroke stroke = new BasicStroke(4.0f);
        HEADING heading;

        public TrafficLightPost(HEADING heading) {
            setOpaque(true);
            this.heading = heading;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            //Get the current size of this component
            Dimension d = this.getSize();
            g2.setBackground(Color.WHITE);
            g2.setColor(Color.BLACK);
            g2.setStroke(stroke);
            if (heading == HEADING.NORTH || heading == HEADING.SOUTH) {
                g2.draw(new Line2D.Double(d.width / 2, 0, d.width / 2, d.height));
            } else {
                g2.draw(new Line2D.Double(0, d.height / 2, d.width, d.height / 2));
            }
        }
    }

    class TrafficLightLamp extends JPanel {

        Color on;
        int radius = 20;
        int border = 3;
        boolean active;

        TrafficLightLamp(Color color) {
            init(color);
        }

        private void init(Color color) {
            on = color;
        }

        public Dimension getPreferredSize() {
            int size = (radius + border) * 2;
            return new Dimension(size, size);
        }

        public void turnOn() {
            active = true;
            repaint();
        }

        public void turnOff() {
            active = false;
            repaint();
        }

        public void paint(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());
            if (active) {
                g.setColor(on);
            } else {
                g.setColor(on.darker().darker().darker());
            }
            g.fillOval(border, border, 2 * radius, 2 * radius);
        }
    }

}



