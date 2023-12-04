package juuxel.advent2023;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day4 {
    private static final Pattern CARD_REGEX = Pattern.compile("^Card (.+): (.+) \\| (.+)$");

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(4));
        part2(Loader.lines(4));
    }

    public static void part1(Stream<String> lines) {
        int totalScore = lines.map(Day4::readCard)
            .mapToInt(Card::score)
            .sum();

        System.out.println(totalScore);
    }

    private static Card readCard(String line) {
        var matcher = CARD_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }

        var cardId = Integer.parseInt(matcher.group(1).trim());
        var winningNumbers = parseNumberList(matcher.group(2));
        var ourNumbers = parseNumberList(matcher.group(3));
        return new Card(cardId, winningNumbers, ourNumbers);
    }

    private static List<Integer> parseNumberList(String line) {
        return Arrays.stream(line.trim().split(" +"))
            .map(Integer::parseInt)
            .toList();
    }

    public static void part2(Stream<String> lines) {
        List<Card> cards = lines.map(Day4::readCard).toList();
        Queue<Card> cardQueue = new ArrayDeque<>(cards);

        int cardCount = 0;

        Card current;
        while ((current = cardQueue.poll()) != null) {
            cardCount++;
            int winning = current.countWinningNumbers();

            for (int i = 0; i < winning; i++) {
                cardQueue.add(cards.get(current.id() + i));
            }
        }

        System.out.println(cardCount);
    }

    private record Card(int id, List<Integer> winningNumbers, List<Integer> ourNumbers) {
        private int countWinningNumbers() {
            int count = 0;

            for (int winning : winningNumbers) {
                if (ourNumbers.contains(winning)) {
                    count++;
                }
            }

            return count;
        }

        private int score() {
            int exponent = countWinningNumbers() - 1;
            return exponent >= 0 ? (1 << exponent) : 0;
        }
    }
}
