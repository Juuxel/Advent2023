package juuxel.advent2023;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day2 {
    private static final Pattern GAME_PATTERN = Pattern.compile("^Game (.+): (.+)$");

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(2));
        part2(Loader.lines(2));
    }

    public static void part1(Stream<String> lines) {
        int idSum = lines.map(Day2::parseGame)
            .filter(Game::isValid)
            .mapToInt(Game::id)
            .sum();
        System.out.println(idSum);
    }

    private static Game parseGame(String line) {
        var gameMatcher = GAME_PATTERN.matcher(line);

        if (gameMatcher.matches()) {
            int gameId = Integer.parseInt(gameMatcher.group(1));
            String roundsStr = gameMatcher.group(2);

            List<Round> rounds = Arrays.stream(roundsStr.split("; *"))
                .map(roundStr -> {
                    String[] roundParts = roundStr.split(", *");
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for (String roundPart : roundParts) {
                        int spaceIndex = roundPart.indexOf(' ');
                        int count = Integer.parseInt(roundPart.substring(0, spaceIndex));
                        switch (roundPart.substring(spaceIndex + 1)) {
                            case "red" -> red = count;
                            case "green" -> green = count;
                            case "blue" -> blue = count;
                        }
                    }

                    return new Round(red, green, blue);
                })
                .toList();

            return new Game(gameId, rounds);
        }

        throw new IllegalArgumentException();
    }

    public static void part2(Stream<String> lines) {
        int powerSum = lines.map(Day2::parseGame)
            .mapToInt(Game::power)
            .sum();
        System.out.println(powerSum);
    }

    private record Game(int id, List<Round> rounds) {
        boolean isValid() {
            return rounds.stream().allMatch(Round::isValid);
        }

        int power() {
            int maxRed = rounds.stream().mapToInt(Round::red).max().orElse(0);
            int maxGreen = rounds.stream().mapToInt(Round::green).max().orElse(0);
            int maxBlue = rounds.stream().mapToInt(Round::blue).max().orElse(0);
            return maxRed * maxGreen * maxBlue;
        }
    }

    private record Round(int red, int green, int blue) {
        boolean isValid() {
            return red <= 12 && green <= 13 && blue <= 14;
        }
    }
}
