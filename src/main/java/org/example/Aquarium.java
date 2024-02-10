package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Aquarium implements Runnable {
    private static final Random random = new Random();
    public static final int MAX_X = 40;
    public static final int MAX_Y = 40;
    public static final int MAX_Z = 40;
    private static final int MAX_CAPACITY = 100;
    private final int capacity;
    private volatile int size;
    private final CopyOnWriteArrayList<Fish> fishes;

    public Aquarium(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalArgumentException();
        this.capacity = capacity;
        fishes = new CopyOnWriteArrayList<>();
    }

    public void fillTheAquarium(int males, int females) {
        if (males + females > capacity)
            throw new IllegalArgumentException();
        fillTheAquarium(males, true);
        fillTheAquarium(females, false);
    }

    private void fillTheAquarium(int count, boolean isMale) {
        for (int i = 0; i < count; i++) {
            Fish buildFish = Fish.builder()
                    .male(isMale)
                    .staysAlive(random.nextLong(0, Fish.FISH_MAX_LIFE))
                    .aquarium(this)
                    .build();
            long c = 0;
            do {
                buildFish.setLocation(Location.getRandomLocation(MAX_X, MAX_Y, MAX_Z));
                c = fishes.stream().filter(fish -> Objects.equals(fish.getLocation(), buildFish.getLocation())).count();
            } while (c != 0);
            fishes.add(buildFish);
        }
    }

    @Override
    public void run() {
        try {
            for (Fish value : fishes) {
                new Thread(value).start();
                size++;
            }
            while (size > 0) {
                Thread.sleep(1000);
            }
            System.out.println("All fishes died.");
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
}
