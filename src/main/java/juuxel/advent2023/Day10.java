package juuxel.advent2023;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Stream;

public final class Day10 {
    private static final int DOT = -2;
    private static final int VERT = -3;
    private static final int HORIZ = -4;
    private static final int F = -5;
    private static final int J = -6;
    private static final int L = -7;
    private static final int SEVEN = -8;

    public static void main(String[] args) throws Exception {
        run(Loader.lines(10));
    }

    public static void run(Stream<String> lines) {
        var grid = new CharGrid(lines);

        // Part 1
        record Movement(int x, int y, int distance, Direction from) {}

        int width = grid.width();
        int height = grid.height();

        int max = 0;
        int[][] board = new int[width][height];
        int startX = -1;
        int startY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = grid.getChar(x, y);
                if (c == 'S') {
                    startX = x;
                    startY = y;
                    c = discoverS(grid, x, y);
                }

                board[x][y] = switch (c) {
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
            if (initial >= 0 || initial == DOT) {
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

            if (initial == HORIZ || initial == SEVEN || initial == J) movements.add(new Movement(x - 1, y, dist + 1, Direction.RIGHT));
            if (initial == HORIZ || initial == L || initial == F) movements.add(new Movement(x + 1, y, dist + 1, Direction.LEFT));
            if (initial == VERT || initial == L || initial == J) movements.add(new Movement(x, y - 1, dist + 1, Direction.DOWN));
            if (initial == VERT || initial == SEVEN || initial == F) movements.add(new Movement(x, y + 1, dist + 1, Direction.UP));
        } while ((current = movements.poll()) != null);

        System.out.println(max);

        // Part 2
        var simulation = new Part2(expand(grid, board));
        simulation.run();

        int part2 = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                if (board[x][y] < 0 && !simulation.visited.get(3 * x + 1, 3 * y + 1)) {
                    part2++;
                }
            }
        }
        System.out.println(part2);
    }

    private static char discoverS(CharGrid grid, int x, int y) {
        boolean connectedLeft = grid.contains(x - 1, y) && "-FL".indexOf(grid.getChar(x - 1, y)) >= 0;
        boolean connectedRight = grid.contains(x + 1, y) && "-7J".indexOf(grid.getChar(x + 1, y)) >= 0;
        boolean connectedUp = grid.contains(x, y - 1) && "|7F".indexOf(grid.getChar(x, y - 1)) >= 0;
        boolean connectedDown = grid.contains(x, y + 1) && "|JL".indexOf(grid.getChar(x, y + 1)) >= 0;

        if (connectedUp && connectedDown) {
            return '|';
        } else if (connectedLeft && connectedRight) {
            return '-';
        } else if (connectedUp && connectedLeft) {
            return 'J';
        } else if (connectedDown && connectedLeft) {
            return '7';
        } else if (connectedUp && connectedRight) {
            return 'L';
        } else if (connectedDown && connectedRight) {
            return 'F';
        } else {
            throw new RuntimeException("cannot figure out S");
        }
    }

    private static BooleanGrid expand(CharGrid grid, int[][] part1Board) {
        BooleanGrid obstacles = new BooleanGrid(3 * grid.width(), 3 * grid.height());
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                int ox = 3 * x;
                int oy = 3 * y;

                char c = grid.getChar(x, y);
                if (c == 'S') {
                    c = discoverS(grid, x, y);
                } else if (part1Board[x][y] < 0) {
                    continue;
                }

                switch (c) {
                    case '|' -> {
                        obstacles.mark(ox + 1, oy);
                        obstacles.mark(ox + 1, oy + 1);
                        obstacles.mark(ox + 1, oy + 2);
                    }
                    case '-' -> {
                        obstacles.mark(ox, oy + 1);
                        obstacles.mark(ox + 1, oy + 1);
                        obstacles.mark(ox + 2, oy + 1);
                    }
                    case 'L' -> {
                        obstacles.mark(ox + 1, oy);
                        obstacles.mark(ox + 1, oy + 1);
                        obstacles.mark(ox + 2, oy + 1);
                    }
                    case 'J' -> {
                        obstacles.mark(ox + 1, oy);
                        obstacles.mark(ox + 1, oy + 1);
                        obstacles.mark(ox, oy + 1);
                    }
                    case '7' -> {
                        obstacles.mark(ox + 1, oy + 2);
                        obstacles.mark(ox + 1, oy + 1);
                        obstacles.mark(ox, oy + 1);
                    }
                    case 'F' -> {
                        obstacles.mark(ox + 1, oy + 2);
                        obstacles.mark(ox + 1, oy + 1);
                        obstacles.mark(ox + 2, oy + 1);
                    }
                }
            }
        }
        return obstacles;
    }

    private static final class Part2 {
        private final BooleanGrid obstacles;
        private final BooleanGrid visited;
        private final Queue<Point> destinations = new ArrayDeque<>();

        private Part2(BooleanGrid obstacles) {
            this.obstacles = obstacles;
            this.visited = new BooleanGrid(obstacles.width(), obstacles.height());
        }

        public void run() {
            // Add outsides
            for (int x = 0; x < obstacles.width(); x++) {
                addNeighbours(x, -1);
                addNeighbours(x, obstacles.height());
            }

            for (int y = 0; y < obstacles.height(); y++) {
                addNeighbours(-1, y);
                addNeighbours(obstacles.width(), y);
            }

            Point destination;
            while ((destination = destinations.poll()) != null) {
                // Skip out-of-bounds destinations
                if (!obstacles.contains(destination.x, destination.y)) continue;
                if (obstacles.get(destination.x, destination.y)) continue;

                if (visited.mark(destination.x, destination.y)) {
                    addNeighbours(destination.x, destination.y);
                }
            }
        }

        private void addNeighbours(int x, int y) {
            destinations.add(new Point(x - 1, y));
            destinations.add(new Point(x + 1, y));
            destinations.add(new Point(x, y - 1));
            destinations.add(new Point(x, y + 1));
        }
    }

    private record Point(int x, int y) {
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
