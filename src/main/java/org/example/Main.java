package org.example;

import java.util.Random;

public class Main {
    private static final Random random = new Random();

    public static void main(String[] args) {
        int capacity = random.nextInt(0, 80);
        int males = random.nextInt(0, (capacity * 2) / 3);
        int females = random.nextInt(0, (capacity * 2) / 3);
        Aquarium aquarium = new Aquarium(80);
        aquarium.fillTheAquarium(39, 39);
        new Thread(aquarium).start();
    }
}