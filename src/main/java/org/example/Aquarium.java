package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Aquarium implements Runnable {
    private static final Random random = new Random();
    public static final int MAX_X = 40;
    public static final int MAX_Y = 40;
    public static final int MAX_Z = 40;
    private static final int MAX_CAPACITY = 100;
    private final ConcurrentHashMap<Location, Fish> fishes;
    private final ExecutorService service;
    private int capacity;

    public Aquarium(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalArgumentException();
        this.capacity = capacity;
        fishes = new ConcurrentHashMap<>();
        service = Executors.newFixedThreadPool(capacity);
    }

    public void fillTheAquarium(int males, int females) {
        if (males + females > capacity)
            throw new IllegalArgumentException();
        fillTheAquarium(males, true);
        fillTheAquarium(females, false);
    }

    private void fillTheAquarium(int count, boolean isMale) {
        for (int i = 0; i < count; i++) {
            Location location = Location.getRandomLocation(MAX_X, MAX_Y, MAX_Z);
            Fish buildFish = Fish.builder()
                    .male(isMale)
                    .staysAlive(random.nextLong(0, Fish.FISH_MAX_LIFE))
                    .location(location)
                    .aquarium(this)
                    .build();
            Fish fish = fishes.put(location, buildFish);
            while (Objects.nonNull(fish)) {
                fish.getLocation().setRandomLocation(MAX_X, MAX_Y, MAX_Z);
                fish = fishes.put(fish.getLocation(), fish);
            }
        }
    }

    @Override
    public void run() {
        for (Fish value : fishes.values()) {
            service.execute(value);
            System.out.printf("%s fish with id: %d created at location: %s, left to live %s seconds\n", (value.isMale() ? "Male" : "Female"), value.getId(), value.getLocation().toString(), value.getStaysAlive());
        }
        while (!fishes.isEmpty() || service.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
        if (fishes.isEmpty()) {
            System.out.println("All fishes died.");
            service.shutdownNow();
        }
    }
}
