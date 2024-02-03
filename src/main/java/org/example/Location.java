package org.example;

import lombok.*;

import java.util.Objects;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location implements Cloneable {
    private static final Random random = new Random();
    // height
    private volatile int x;
    // length
    private volatile int y;
    // width
    private volatile int z;

    public static Location getRandomLocation(int maxX, int maxY, int maxZ) {
        return Location.builder()
                .x(random.nextInt(0, maxX))
                .y(random.nextInt(0, maxY))
                .z(random.nextInt(0, maxZ))
                .build();
    }

    public void setRandomLocation(int maxX, int maxY, int maxZ) {
        this.x = random.nextInt(0, maxX);
        this.y = random.nextInt(0, maxY);
        this.z = random.nextInt(0, maxZ);
    }

    public void setLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && y == location.y && z == location.z;
    }

    @Override
    protected Location clone() {
        return new Location(this.x, this.y, this.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(x: %d, y: %d, z: %d)", x, y, z);
    }
}
