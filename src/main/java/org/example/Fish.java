package org.example;

import lombok.*;

import java.util.Objects;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fish implements Runnable {
    public static final int FISH_MAX_LIFE = 1000;
    private static final Random random = new Random();
    private static int count;
    @Builder.Default
    private int id = count;
    private boolean male;
    private long staysAlive;
    private Location location;
    private Aquarium aquarium;

    {
        count++;
        id = count;
    }

    @Override
    public void run() {
        live();
        aquarium.getFishes().remove(getLocation());
        System.out.printf("Fish with id: %d died\n", id);
    }

    private synchronized void live() {
        try {
            while (staysAlive > 0) {
                Fish fish, removed = fish = null;
                removed = aquarium.getFishes().remove(getLocation());
                do {
                    removed.move();
                    fish = aquarium.getFishes().get(removed.getLocation());
                    makeChild(removed, fish);
                } while (Objects.nonNull(fish));
                aquarium.getFishes().put(removed.getLocation(), removed);
                Thread.sleep(1000);
                staysAlive--;
            }
        } catch (InterruptedException e) {
            System.out.printf("Fish with id: %d being killed", this.id);
        } catch (Exception e) {
            System.out.println(this);
            e.printStackTrace();
        }
    }

    private void move() {
        int x = getRandomPoint(Aquarium.MAX_X - 1, 1, location.getX(), 1);
        int y = getRandomPoint(Aquarium.MAX_Y - 1, 1, location.getY(), 1);
        int z = getRandomPoint(Aquarium.MAX_Z - 1, 1, location.getZ(), 1);
        synchronized (this) {
            location.setLocation(x, y, z);
        }
        System.out.printf("Fish with the id: %d moved to this location: %s, left to live: %d seconds\n", id, location.toString(), staysAlive);
    }

    private synchronized void makeChild(Fish fish1, Fish fish2) {
        if (Objects.nonNull(fish1) && Objects.nonNull(fish2) && ((fish1.male && !fish2.male) || (!fish1.male && fish2.male))) {
            if (aquarium.getFishes().size() >= aquarium.getCapacity()) {
                System.out.println("Aquarium filled.");
                aquarium.getService().shutdownNow();
            }
            Fish child = Fish.builder()
                    .aquarium(aquarium)
                    .male(random.nextBoolean())
                    .location(fish1.getLocation().clone())
                    .staysAlive(random.nextLong(0, FISH_MAX_LIFE))
                    .build();
            Fish fish = null;
            do {
                child.move();
                fish = aquarium.getFishes().get(child.getLocation());
            } while (Objects.nonNull(fish));
            aquarium.getFishes().put(child.getLocation(), child);
            aquarium.getService().execute(child);
            System.out.printf("Fishes with id : %d and %d created a %s child with id: %d\n", fish1.id, fish2.id, (child.isMale() ? "male" : "female"), child.id);
        }

    }

    private int getRandomPoint(int max, int min, int value, int addingValue) {
        int point = value;
        if (random.nextBoolean()) {
            if (value < max)
                point += addingValue;
            else point -= addingValue;
        } else {
            if (value > min)
                point -= addingValue;
            else point += addingValue;
        }
        return point;
    }

    @Override
    public String toString() {
        return "Fish{" +
                "id=" + id +
                ", male=" + male +
                ", staysAlive=" + staysAlive +
                ", location=" + location +
                ", aquarium=" + aquarium +
                '}';
    }
}
