package org.example;

import lombok.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fish implements Runnable {
    public static final int FISH_MAX_LIFE = 100;
    private static final Random random = new Random();
    private volatile static int count;
    @Builder.Default
    private int id = count;
    private boolean male;
    private long staysAlive;
    private Location location;
    private Aquarium aquarium;

    {
        count++;
    }

    @Override
    public void run() {
        System.out.printf("%s fish with id: %d created at location: %s, left to live %s seconds\n", (this.isMale() ? "Male" : "Female"), this.getId(), this.getLocation().toString(), this.getStaysAlive());
        live();
        synchronized (aquarium) {
            aquarium.setSize(aquarium.getSize() - 1);
            aquarium.getFishes().remove(this);
        }
        System.out.printf("Fish with id: %d died\n", id);
    }

    private synchronized void live() {
        try {
            while (staysAlive > 0) {
                long c = 0;
                do {
                    this.move();
                    c = aquarium.getFishes().stream().filter(value -> Objects.equals(value.location, this.location) && this.isMale() != value.isMale()).count();
                    while (c-- >= 1)
                        makeChild(this);
                } while (c > 0);
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

    public void move() {
        int x = getRandomPoint(Aquarium.MAX_X - 1, 1, location.getX(), 1);
        int y = getRandomPoint(Aquarium.MAX_Y - 1, 1, location.getY(), 1);
        int z = getRandomPoint(Aquarium.MAX_Z - 1, 1, location.getZ(), 1);
        synchronized (this) {
            location.setLocation(x, y, z);
        }
        System.out.printf("Fish with the id: %d moved to this location: %s, left to live: %d seconds\n", id, location.toString(), staysAlive);
    }

    private synchronized void makeChild(Fish fish) {
        if (Objects.nonNull(fish)) {
            if (aquarium.getSize() >= aquarium.getCapacity()) {
                System.out.println("Aquarium filled.");
                return;
            }
            Fish child = Fish.builder()
                    .aquarium(aquarium)
                    .male(random.nextBoolean())
                    .location(fish.getLocation().clone())
                    .staysAlive(random.nextLong(0, FISH_MAX_LIFE))
                    .build();
            Optional<Fish> any;
            do {
                child.move();
                any = aquarium.getFishes().stream().filter(value -> Objects.equals(child.location, value.location)).findAny();
            } while (any.isPresent());
            aquarium.getFishes().add(child);
            new Thread(child).start();
            synchronized (aquarium) {
                aquarium.setSize(aquarium.getSize() + 1);
            }
            System.out.printf("A %s child with id: %d created\n", (child.isMale() ? "male" : "female"), child.id);
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
