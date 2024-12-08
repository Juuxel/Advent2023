package juuxel.advent2023;

import java.util.ArrayDeque;
import java.util.Queue;

public final class Day10 {
    private static final int DOT = -1;
    private static final int START = -2;
    private static final int VERT = -3;
    private static final int HORIZ = -4;
    private static final int F = -5;
    private static final int J = -6;
    private static final int L = -7;
    private static final int SEVEN = -8;
    private static final int CCW = -1;
    private static final int CW = 1;
    private static final int RED = 1;
    private static final int BLUE = 2;

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(10).toArray(String[]::new));
    }

    public static void part1(String[] lines) throws Exception {
        record Movement(int x, int y, int distance, Direction from) {}

        int width = lines[0].length();
        int height = lines.length;

        int max = 0;
        int[][] board = new int[width][height];
        int startX = -1;
        int startY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                board[x][y] = switch (lines[y].charAt(x)) {
                    case 'S' -> {
                        startX = x;
                        startY = y;
                        yield START;
                    }
                    case '-' -> HORIZ;
                    case '|' -> VERT;
                    case 'F' -> F;
                    case 'J' -> J;
                    case 'L' -> L;
                    case '7' -> SEVEN;
                    default -> DOT;
                };
            }
        }

        Queue<Movement> movements = new ArrayDeque<>();
        Movement current = new Movement(startX, startY, 0, null);
        do {
            int x = current.x;
            int y = current.y;
            if (x < 0 || y < 0 || x >= width || y >= height) continue;
            int dist = current.distance;
            int initial = board[x][y];
            if (initial >= DOT) {
                continue;
            } else if (current.from != null) {
                switch (current.from) {
                    case UP -> {
                        if (initial != VERT && initial != J && initial != L) continue;
                    }
                    case DOWN -> {
                        if (initial != VERT && initial != SEVEN && initial != F) continue;
                    }
                    case LEFT -> {
                        if (initial != HORIZ && initial != J && initial != SEVEN) continue;
                    }
                    case RIGHT -> {
                        if (initial != HORIZ && initial != L && initial != F) continue;
                    }
                }
            }

            board[x][y] = dist;
            max = Math.max(max, dist);

            movements.add(new Movement(x - 1, y, dist + 1, Direction.RIGHT));
            movements.add(new Movement(x + 1, y, dist + 1, Direction.LEFT));
            movements.add(new Movement(x, y - 1, dist + 1, Direction.DOWN));
            movements.add(new Movement(x, y + 1, dist + 1, Direction.UP));
        } while ((current = movements.poll()) != null);

        System.out.println(max);
    }

    public static void part2(String[] lines) throws Exception {
        enum MovementType {
            PIPE, PAINT
        }
        record Movement(int x, int y, int value, MovementType type, Direction from) {}

        int width = lines[0].length();
        int height = lines.length;

        int max = 0;
        int[][] board = new int[width][height];
        int[][] winding = new int[width][height];
        int startX = -1;
        int startY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                board[x][y] = switch (lines[y].charAt(x)) {
                    case 'S' -> {
                        startX = x;
                        startY = y;
                        yield START;
                    }
                    case '-' -> HORIZ;
                    case '|' -> VERT;
                    case 'F' -> F;
                    case 'J' -> J;
                    case 'L' -> L;
                    case '7' -> SEVEN;
                    default -> DOT;
                };
            }
        }

        Queue<Movement> movements = new ArrayDeque<>();
        Movement current = new Movement(startX, startY, 0, MovementType.PIPE, null);
        int startWinding = 0;
        do {
            int x = current.x;
            int y = current.y;
            if (x < 0 || y < 0 || x >= width || y >= height) continue;
            int initial = board[x][y];
            if (initial >= 0) {
                continue;
            } else if (current.type == MovementType.PIPE && current.from != null) {
                switch (current.from) {
                    case UP -> {
                        if (initial != VERT && initial != J && initial != L) continue;
                    }
                    case DOWN -> {
                        if (initial != VERT && initial != SEVEN && initial != F) continue;
                    }
                    case LEFT -> {
                        if (initial != HORIZ && initial != J && initial != SEVEN) continue;
                    }
                    case RIGHT -> {
                        if (initial != HORIZ && initial != L && initial != F) continue;
                    }
                }
            }

            board[x][y] = dist;
            max = Math.max(max, dist);

            movements.add(new Movement(x - 1, y, dist + 1, Direction.RIGHT));
            movements.add(new Movement(x + 1, y, dist + 1, Direction.LEFT));
            movements.add(new Movement(x, y - 1, dist + 1, Direction.DOWN));
            movements.add(new Movement(x, y + 1, dist + 1, Direction.UP));
        } while ((current = movements.poll()) != null);

        System.out.println(max);
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
