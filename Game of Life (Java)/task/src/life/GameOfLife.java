package life;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;

public class GameOfLife extends JFrame {
    private JLabel generationLabel, aliveLabel, generationTxtLabel, aliveTxtLabel;

    private JToggleButton playToggleBtn;
    private Map<String, JLabel> boxes;
    private WorldModel model;
    private boolean running;
    private int generation;
    public GameOfLife() {
        super("Game of Life");

        model = new WorldModel();
        running = false;
        generation = 0;

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(610, 630);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel labelPanel = new JPanel();
        labelPanel.setBounds(50, 10, 510, 30);

        generationTxtLabel = new JLabel("Generation:");
        generationLabel = new JLabel("0");
        generationLabel.setName("GenerationLabel");

        aliveTxtLabel = new JLabel("Alive:");
        aliveLabel = new JLabel("0");
        aliveLabel.setName("AliveLabel");

        playToggleBtn = new JToggleButton("Start");
        playToggleBtn.setName("PlayToggleButton");
        playToggleBtn.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                playToggleBtn.setText("Stop");
                running = true;
                new GameWorker().execute();
            } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                playToggleBtn.setText("Start");
                running = false;
            }
        });

        JLabel spacer = new JLabel("                      ");

        JButton resetBtn = new JButton("Reset");
        resetBtn.setName("ResetButton");
        resetBtn.addActionListener(e -> resetGame());

        labelPanel.add(generationTxtLabel);
        labelPanel.add(generationLabel);
        labelPanel.add(aliveTxtLabel);
        labelPanel.add(aliveLabel);
        labelPanel.add(spacer);
        labelPanel.add(playToggleBtn);
        labelPanel.add(resetBtn);

        contentPane.add(labelPanel);

        JPanel boxPanel = new JPanel();

        boxes = new HashMap<>();
        int xOffset = 50;
        int yOffset = 50;

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                String key = i + "," + j;
                JLabel label = new JLabel();
                label.setBorder(new BevelBorder(BevelBorder.RAISED));
                label.setBounds(((i + 1) * 5) + xOffset, ((j + 1) * 5) + yOffset, 5, 5);
                boxes.put(key, label);
                contentPane.add(label);
            }
        }
        contentPane.add(boxPanel);

        setVisible(true);
    }

    public void refreshView(WorldModel currentWorld, int generation) {
        generationLabel.setText(String.valueOf(generation));
        aliveLabel.setText(String.valueOf(currentWorld.getAlive()));
        generationLabel.revalidate();
        aliveLabel.revalidate();
        char[][] currentWorldArray = currentWorld.getWorld();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                JLabel currentLabel = boxes.get(i + "," + j);
                if (currentWorldArray[i][j] == 'O') {
                    currentLabel.setBackground(Color.GREEN);
                    currentLabel.setText("*");
                } else {
                    currentLabel.setBackground(Color.WHITE);
                    currentLabel.setText(" ");
                }
            }
        }
    }

    private void resetGame() {
        running = false;
        playToggleBtn.setSelected(false);
        generation = 0;
        model = new WorldModel();

        generationLabel.setText("0");
        aliveLabel.setText("0");

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                JLabel currentLabel = boxes.get(i + "," + j);
                currentLabel.setBackground(Color.WHITE);
                currentLabel.setText(" ");
            }
        }
    }

    class GameWorker extends SwingWorker<String, Object> {
        @Override
        public String doInBackground() throws InterruptedException {
            runGame();
            return "";
        }

        @Override
        protected void done() {
            System.out.println("Worker done");
        }

        private void runGame() throws InterruptedException {
            System.out.println("runGame()");
            char[][] currentWorldArray = model.getWorld().clone();

            while (running) {
                //System.out.println("Running generation " + generation);
                WorldModel currentWorld = doGeneration(currentWorldArray);
                currentWorldArray = currentWorld.getWorld();
                refreshView(currentWorld, generation);

                generation++;
                Thread.sleep(150);
            }
        }

        private WorldModel doGeneration(char[][] currentWorld) {
            int alive = 0;
            char[][] newWorld = new char[currentWorld.length][currentWorld.length];
            for (int i = 0; i < currentWorld.length; i++) {
                for (int j = 0; j < currentWorld.length; j++) {
                    int neighbors = 0;
                    if (checkNorth(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkSouth(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkEast(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkWest(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkNorthWest(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkNorthEast(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkSouthWest(i, j, currentWorld)) {
                        neighbors++;
                    }
                    if (checkSouthEast(i, j, currentWorld)) {
                        neighbors++;
                    }

                    if (neighbors == 3) {
                        newWorld[i][j] = 'O';
                    } else if (neighbors < 2 || neighbors > 3) {
                        newWorld[i][j] = ' ';
                    } else {
                        newWorld[i][j] = currentWorld[i][j];
                    }
                    if (newWorld[i][j] == 'O') {
                        alive++;
                    }
                }
            }
            model.setAlive(alive);
            return new WorldModel(newWorld, alive);
        }

        public static boolean checkNorth(int x, int y, char[][] world) {
            if (x == 0) {
                return world[world.length - 1][y] == 'O';
            } else {
                return world[x - 1][y] == 'O';
            }
        }

        public static boolean checkSouth(int x, int y, char[][] world) {
            if (x == world.length - 1) {
                return world[0][y] == 'O';
            } else {
                return world[x + 1][y] == 'O';
            }
        }

        public static boolean checkEast(int x, int y, char[][] world) {
            if (y == world.length - 1) {
                return world[x][0] == 'O';
            } else {
                return world[x][y + 1] == 'O';
            }
        }

        public static boolean checkWest(int x, int y, char[][] world) {
            if (y == 0) {
                return world[x][world.length - 1] == 'O';
            } else {
                return world[x][y - 1] == 'O';
            }
        }

        public static boolean checkNorthWest(int x, int y, char[][] world) {
            if (y == 0) {
                return checkNorth(x, world.length - 1, world);
            } else {
                return checkNorth(x, y - 1, world);
            }
        }

        public static boolean checkNorthEast(int x, int y, char[][] world) {
            if (y == world.length - 1) {
                return checkNorth(x, 0, world);
            } else {
                return checkNorth(x, y + 1, world);
            }
        }

        public static boolean checkSouthWest(int x, int y, char[][] world) {
            if (y == 0) {
                return checkSouth(x, world.length - 1, world);
            } else {
                return checkSouth(x, y - 1, world);
            }
        }

        public static boolean checkSouthEast(int x, int y, char[][] world) {
            if (y == world.length - 1) {
                return checkSouth(x, 0, world);
            } else {
                return checkSouth(x, y + 1, world);
            }
        }
    }


}