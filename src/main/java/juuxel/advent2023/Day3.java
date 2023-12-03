package juuxel.advent2023;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class Day3 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(3));
        part2(Loader.lines(3));
    }

    public static void part1(Stream<String> lines) {
        File file = parseLines(lines.toList());
        List<Token.NumberToken> partNumbers = new ArrayList<>();
        Aabb gridAabb = new Aabb(0, 0, file.tokenGrid[0].length, file.tokenGrid.length);

        file.tokens().forEach((aabb, token) -> {
            if (!(token instanceof Token.NumberToken nt)) return;

            outer: for (int x = aabb.x - 1; x <= aabb.x + aabb.width; x++) {
                for (int y = aabb.y - 1; y <= aabb.y + aabb.height; y++) {
                    if (gridAabb.contains(x, y) && !aabb.contains(x, y)) {
                        if (file.tokenGrid[x][y] instanceof Token.SymbolToken) {
                            partNumbers.add(nt);
                            break outer;
                        }
                    }
                }
            }
        });

        int sum = partNumbers.stream().mapToInt(Token.NumberToken::value).sum();
        System.out.println(sum);
    }

    private static File parseLines(List<String> lines) {
        TokenReader reader = new TokenReader();
        lines.forEach(reader::readLine);
        Map<Aabb, Token> tokens = reader.tokens;
        int width = lines.getFirst().length();
        int height = lines.size();

        final Token[][] tokenGrid = new Token[width][height];
        tokens.forEach((aabb, token) -> {
            for (int xo = 0; xo < aabb.width; xo++) {
                for (int yo = 0; yo < aabb.height; yo++) {
                    tokenGrid[aabb.x + xo][aabb.y + yo] = token;
                }
            }
        });

        return new File(tokens, tokenGrid);
    }

    public static void part2(Stream<String> lines) {
        File file = parseLines(lines.toList());
        List<Integer> gearRatios = new ArrayList<>();
        Aabb gridAabb = new Aabb(0, 0, file.tokenGrid[0].length, file.tokenGrid.length);

        file.tokens().forEach((aabb, token) -> {
            if (!(token instanceof Token.SymbolToken st) || st.symbol != '*') return;

            Set<Token.NumberToken> adjacent = Collections.newSetFromMap(new IdentityHashMap<>());

            for (int x = aabb.x - 1; x <= aabb.x + aabb.width; x++) {
                for (int y = aabb.y - 1; y <= aabb.y + aabb.height; y++) {
                    if (gridAabb.contains(x, y) && !aabb.contains(x, y)) {
                        if (file.tokenGrid[x][y] instanceof Token.NumberToken nt) {
                            adjacent.add(nt);
                        }
                    }
                }
            }

            if (adjacent.size() == 2) {
                gearRatios.add(adjacent.stream().mapToInt(Token.NumberToken::value).reduce(1, (a, b) -> a * b));
            }
        });

        int sum = gearRatios.stream().mapToInt(x -> x).sum();
        System.out.println(sum);
    }

    private record File(Map<Aabb, Token> tokens, Token[][] tokenGrid) {
    }

    private static final class TokenReader {
        private final Map<Aabb, Token> tokens = new HashMap<>();
        private final StringBuilder currentToken = new StringBuilder();
        private int tokenStart = -1;
        private int y = 0;

        private void readLine(String line) {
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);

                if (tokenStart >= 0) {
                    if ('0' <= c && c <= '9') {
                        currentToken.append(c);
                        continue;
                    } else {
                        pushNum();
                    }
                }

                switch (c) {
                    case '.' -> {}

                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                        tokenStart = x;
                        currentToken.append(c);
                    }

                    default -> tokens.put(
                        new Aabb(x, y, 1, 1),
                        new Token.SymbolToken(c)
                    );
                }
            }

            if (tokenStart >= 0) {
                pushNum();
            }

            y++;
        }

        private void pushNum() {
            tokens.put(new Aabb(tokenStart, y, currentToken.length(), 1), new Token.NumberToken(Integer.parseInt(currentToken.toString())));
            reset();
        }

        private void reset() {
            currentToken.setLength(0);
            tokenStart = -1;
        }
    }

    private sealed interface Token {
        record NumberToken(int value) implements Token {
        }

        record SymbolToken(char symbol) implements Token {
        }
    }

    private record Aabb(int x, int y, int width, int height) {
        private boolean contains(int x, int y) {
            return this.x <= x && this.y <= y && x < this.x + width && y < this.y + height;
        }
    }
}
