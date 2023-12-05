package juuxel.advent2023;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class Day5 {
    private static final String SEEDS_PREFIX = "seeds: ";

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(5));
        part2(Loader.lines(5));
    }

    public static void part1(Stream<String> lines) {
        run(lines, longs -> longs.stream().mapToLong(x -> x));
    }

    private static List<Mapper> readMapper(Queue<String> lineQueue) {
        lineQueue.remove(); // space
        lineQueue.remove(); // title

        List<Mapper> result = new ArrayList<>();

        String line;
        while ((line = lineQueue.peek()) != null && !line.isEmpty()) {
            lineQueue.remove();

            String[] parts = line.split(" ");
            var destStart = Long.parseLong(parts[0]);
            var sourceStart = Long.parseLong(parts[1]);
            var length = Long.parseLong(parts[2]);
            result.add(new Mapper(destStart, sourceStart, length));
        }

        return result;
    }

    public static void part2(Stream<String> lines) {
        run(lines, seedPairs -> {
            LongStream seeds = LongStream.empty();

            for (int i = 0; i < seedPairs.size(); i += 2) {
                var start = seedPairs.get(i);
                var length = seedPairs.get(i + 1);
                seeds = LongStream.concat(seeds, LongStream.range(start, start + length));
            }

            return seeds.unordered().parallel();
        });
    }

    private static void run(Stream<String> lines, Function<List<Long>, LongStream> streamer) {
        Queue<String> lineQueue = new ArrayDeque<>(lines.toList());
        String[] seedList = lineQueue.remove().substring(SEEDS_PREFIX.length()).split(" +");
        List<Long> seeds = Arrays.stream(seedList).map(Long::parseLong).toList();

        var seedToSoil = readMapper(lineQueue);
        var soilToFert = readMapper(lineQueue);
        var fertToWater = readMapper(lineQueue);
        var waterToLight = readMapper(lineQueue);
        var lightToTemp = readMapper(lineQueue);
        var tempToHumidity = readMapper(lineQueue);
        var humidityToLocation = readMapper(lineQueue);

        var result = streamer.apply(seeds)
            .map(applyMappers(seedToSoil))
            .map(applyMappers(soilToFert))
            .map(applyMappers(fertToWater))
            .map(applyMappers(waterToLight))
            .map(applyMappers(lightToTemp))
            .map(applyMappers(tempToHumidity))
            .map(applyMappers(humidityToLocation))
            .min()
            .orElseThrow();
        System.out.println(result);
    }

    private static LongUnaryOperator applyMappers(List<Mapper> mappers) {
        return input -> {
            for (Mapper mapper : mappers) {
                if (mapper.sourceStart <= input && input < mapper.sourceStart + mapper.length) {
                    return mapper.destStart + (input - mapper.sourceStart);
                }
            }

            return input;
        };
    }

    private record Mapper(long destStart, long sourceStart, long length) {
    }
}
