package juuxel.advent2023;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day1 {
    private static final Pattern NUMBER_REGEX = Pattern.compile("[0-9]|one|two|three|four|five|six|seven|eight|nine");

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(1));
        part2(Loader.lines(1));
    }

    public static void part1(Stream<String> lines) {
        System.out.println(lines.mapToInt(Day1::extractNumber).sum());
    }

    private static int extractNumber(String line) {
        char first = 0, second = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (isDigit(c)) {
                if (first == 0) {
                    first = c;
                }

                second = c;
            }
        }

        return Integer.parseInt("" + first + second);
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    public static void part2(Stream<String> lineStream) {
        var result = lineStream.map(Day1::extractNumbers)
            .map(numbers -> numbers.map(Day1::parseNumber))
            .mapToInt(numbers -> {
                var list = numbers.toList();
                return 10 * list.getFirst() + list.getLast();
            })
            .sum();
        System.out.println(result);
    }

    private static Stream<String> extractNumbers(String line) {
        var matcher = NUMBER_REGEX.matcher(line);
        int nextStart = 0;
        var builder = Stream.<String>builder();
        while (matcher.find(nextStart)) {
            builder.add(matcher.group());
            nextStart = matcher.start() + 1;
        }
        return builder.build();
    }

    private static int parseNumber(String s) {
        return switch (s) {
            case "one" -> 1;
            case "two" -> 2;
            case "three" -> 3;
            case "four" -> 4;
            case "five" -> 5;
            case "six" -> 6;
            case "seven" -> 7;
            case "eight" -> 8;
            case "nine" -> 9;
            default -> Integer.parseInt(s);
        };
    }
}
