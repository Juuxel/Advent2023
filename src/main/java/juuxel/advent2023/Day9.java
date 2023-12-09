package juuxel.advent2023;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Day9 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(9));
        part2(Loader.lines(9));
    }

    public static void part1(Stream<String> lines) {
        long extrapolatedSum = lines.map(Day9::readSequence)
            .mapToLong(seq -> seq.extrapolate(false))
            .sum();
        System.out.println(extrapolatedSum);
    }

    public static void part2(Stream<String> lines) {
        long extrapolatedSum = lines.map(Day9::readSequence)
            .mapToLong(seq -> seq.extrapolate(true))
            .sum();
        System.out.println(extrapolatedSum);
    }

    private static Sequence readSequence(String line) {
        String[] split = line.split(" +");
        long[] numbers = new long[split.length];

        for (int i = 0; i < split.length; i++) {
            numbers[i] = Long.parseLong(split[i]);
        }

        return new Sequence(numbers);
    }

    private record Sequence(long[] values) {
        private long extrapolate(boolean backwards) {
            List<Sequence> sequences = new ArrayList<>();
            Sequence current = this;

            while (!current.isZero()) {
                sequences.add(current);
                current = current.successor();
            }

            long nextTerm = 0;

            for (Sequence sequence : sequences.reversed()) {
                nextTerm = sequence.extrapolate(nextTerm, backwards);
            }

            return nextTerm;
        }

        private long extrapolate(long increment, boolean backwards) {
            if (backwards) {
                return values[0] - increment;
            } else {
                return values[values.length - 1] + increment;
            }
        }

        private boolean isZero() {
            for (long value : values) {
                if (value != 0) {
                    return false;
                }
            }

            return true;
        }

        private Sequence successor() {
            long[] nextValues = new long[values.length - 1];

            for (int i = 0; i < values.length - 1; i++) {
                nextValues[i] = values[i + 1] - values[i];
            }

            return new Sequence(nextValues);
        }
    }
}
