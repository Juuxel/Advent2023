package juuxel.advent2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public final class Day6Bruteforce {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(6).toArray(String[]::new));
        part2(Loader.lines(6).toArray(String[]::new));
    }

    public static void part1(String[] lines) {
        List<Race> races = readRaces(lines[0], lines[1]);
        long result = races.parallelStream()
            .mapToLong(Race::waysToBeat)
            .reduce(1, (a, b) -> a * b);
        System.out.println(result);
    }

    public static void part2(String[] lines) {
        Race race = readSingleRace(lines[0], lines[1]);
        System.out.println(race.waysToBeat());
    }

    private static List<Race> readRaces(String times, String distances) {
        String[] timeParts = times.split(" +");
        String[] distanceParts = distances.split(" +");

        List<Race> races = new ArrayList<>(timeParts.length - 1);

        for (int i = 1; i < timeParts.length; i++) {
            races.add(new Race(Long.parseLong(timeParts[i]), Long.parseLong(distanceParts[i])));
        }

        return races;
    }

    private static Race readSingleRace(String times, String distances) {
        String[] timeParts = times.split(" +");
        String[] distanceParts = distances.split(" +");

        String singleTime = Arrays.stream(timeParts).skip(1).collect(Collectors.joining());
        String singleDistance = Arrays.stream(distanceParts).skip(1).collect(Collectors.joining());
        return new Race(Long.parseLong(singleTime), Long.parseLong(singleDistance));
    }

    private record Race(long time, long bestDistance) {
        private long waysToBeat() {
            return LongStream.range(1, time)
                .parallel()
                .map(speed -> speed * (time - speed))
                .filter(distance -> distance > bestDistance)
                .count();
        }
    }
}
