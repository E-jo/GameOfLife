package life;

import java.util.Random;

public class WorldModel {
    private char[][] world = new char[100][100];
    private int alive;

    public WorldModel() {
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (random.nextBoolean()) {
                    world[i][j] = 'O';
                    alive++;
                } else {
                    world[i][j] = ' ';
                }
            }
        }
    }

    public WorldModel(char[][] currentWorldArray, int alive) {
        world = currentWorldArray;
        this.alive = alive;
    }

    public char[][] getWorld() {
        return world;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

}
