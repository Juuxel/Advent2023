package juuxel.advent2023;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class Day7 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(7));
        part2(Loader.lines(7));
    }

    public static void part1(Stream<String> lines) {
        run(lines, false);
    }

    public static void part2(Stream<String> lines) {
        run(lines, true);
    }

    private static void run(Stream<String> lines, boolean part2) {
        List<Game> games = lines.map(line -> parseGame(line, part2))
            .sorted(Comparator.comparing(Game::hand, part2 ? Hand.PART_2_COMPARATOR : Hand.COMPARATOR))
            .toList();
        long sum = 0;
        for (int i = 0; i < games.size(); i++) {
            sum += (i + 1) * games.get(i).bid;
        }
        System.out.println(sum);
    }

    private static Game parseGame(String line, boolean withJoker) {
        Map<Integer, Card> cardsByChar = new HashMap<>();
        for (Card value : Card.values()) {
            cardsByChar.put((int) value.symbol, value);
        }
        if (withJoker) {
            cardsByChar.put((int) 'J', Card.CARD_JOKER);
        } else {
            cardsByChar.put((int) 'J', Card.CARD_J);
        }

        String[] parts = line.split(" +");
        Card[] cards = parts[0].chars().mapToObj(cardsByChar::get).toArray(Card[]::new);
        long bid = Long.parseLong(parts[1]);
        return new Game(new Hand(cards[0], cards[1], cards[2], cards[3], cards[4]), bid);
    }

    private record Game(Hand hand, long bid) {
    }

    private record Hand(Card card1, Card card2, Card card3, Card card4, Card card5) {
        private static final Comparator<Hand> COMPARATOR =
            Comparator.comparing((Hand hand) -> hand.computeType(false))
                .thenComparing(Hand::card1)
                .thenComparing(Hand::card2)
                .thenComparing(Hand::card3)
                .thenComparing(Hand::card4)
                .thenComparing(Hand::card5);
        private static final Comparator<Hand> PART_2_COMPARATOR =
            Comparator.comparing((Hand hand) -> hand.computeType(true))
                .thenComparing(Hand::card1)
                .thenComparing(Hand::card2)
                .thenComparing(Hand::card3)
                .thenComparing(Hand::card4)
                .thenComparing(Hand::card5);

        private HandType computeType(boolean withJoker) {
            int[] cardCounts = new int[Card.values().length];
            cardCounts[card1.ordinal()]++;
            cardCounts[card2.ordinal()]++;
            cardCounts[card3.ordinal()]++;
            cardCounts[card4.ordinal()]++;
            cardCounts[card5.ordinal()]++;

            if (withJoker) {
                int jokerCount = cardCounts[Card.CARD_JOKER.ordinal()];
                cardCounts[Card.CARD_JOKER.ordinal()] = 0; // remove jokers
                Arrays.sort(cardCounts);
                cardCounts[cardCounts.length - 1] += jokerCount;
            }

            boolean hasThree = false;
            int twoCounts = 0;

            for (int cardCount : cardCounts) {
                switch (cardCount) {
                    case 5 -> {
                        return HandType.FIVE_OF_A_KIND;
                    }
                    case 4 -> {
                        return HandType.FOUR_OF_A_KIND;
                    }
                    case 3 -> hasThree = true;
                    case 2 -> twoCounts++;
                }
            }

            if (hasThree) {
                return twoCounts > 0 ? HandType.FULL_HOUSE : HandType.THREE_OF_A_KIND;
            }

            return switch (twoCounts) {
                case 2 -> HandType.TWO_PAIR;
                case 1 -> HandType.ONE_PAIR;
                default -> HandType.HIGH_CARD;
            };
        }
    }

    private enum Card {
        CARD_JOKER('J'),
        CARD_TWO('2'),
        CARD_THREE('3'),
        CARD_FOUR('4'),
        CARD_FIVE('5'),
        CARD_SIX('6'),
        CARD_SEVEN('7'),
        CARD_EIGHT('8'),
        CARD_NINE('9'),
        CARD_T('T'),
        CARD_J('J'),
        CARD_Q('Q'),
        CARD_K('K'),
        CARD_A('A');

        private final char symbol;

        Card(char symbol) {
            this.symbol = symbol;
        }
    }

    private enum HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND
    }
}
