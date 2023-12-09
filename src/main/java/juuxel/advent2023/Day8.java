package juuxel.advent2023;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Day8 {
    private static final Pattern NODE_REGEX = Pattern.compile("^(.+) = \\((.+), (.+)\\)$");

    public static void main(String[] args) throws Exception {
        part1(Loader.lines(8));
        part2(Loader.lines(8));
    }

    public static void part1(Stream<String> lines) {
        Iterator<String> lineIter = lines.iterator();
        List<Direction> directions = lineIter.next().chars().mapToObj(c -> Direction.of((char) c)).toList();
        lineIter.next(); // empty line
        Map<String, Node> nodeMap = new HashMap<>();
        lineIter.forEachRemaining(line -> readNode(line, nodeMap::put));

        int step = 0;

        String currentNode = "AAA";
        while (!currentNode.equals("ZZZ")) {
            Direction direction = directions.get(step % directions.size());
            Node node = nodeMap.get(currentNode);

            currentNode = switch (direction) {
                case LEFT -> node.left();
                case RIGHT -> node.right();
            };

            step++;
        }

        System.out.println(step);
    }

    @Deprecated
    public static void part2(Stream<String> lines) {
        Iterator<String> lineIter = lines.iterator();
        List<Direction> directions = lineIter.next().chars().mapToObj(c -> Direction.of((char) c)).toList();
        lineIter.next(); // empty line
        Map<String, Node> nodeMap = new HashMap<>();
        lineIter.forEachRemaining(line -> readNode(line, nodeMap::put));

        record LoopData(String startNode, long startIndex, long length) {
        }

        List<LoopData> loops = nodeMap.keySet()
            .parallelStream()
            .filter(it -> it.endsWith("A"))
            .map(start -> {
                String currentNode = start;
                long step = 0;
                while (!currentNode.endsWith("Z")) {
                    Direction direction = directions.get((int) (step % directions.size()));
                    Node node = nodeMap.get(currentNode);

                    currentNode = switch (direction) {
                        case LEFT -> node.left();
                        case RIGHT -> node.right();
                    };

                    step++;
                }

                long startIndex = step;

                while (step == startIndex || !currentNode.endsWith("Z")) {
                    Direction direction = directions.get((int) (step % directions.size()));
                    Node node = nodeMap.get(currentNode);

                    currentNode = switch (direction) {
                        case LEFT -> node.left();
                        case RIGHT -> node.right();
                    };

                    step++;
                }

                long length = step - startIndex;
                return new LoopData(start, startIndex, length);
            })
            .toList();

        // max() is too high, min() is too low
        long start = loops.stream().mapToLong(LoopData::startIndex).max().orElseThrow();
        long increment = loops.stream().mapToLong(loop -> loop.length).reduce(Mth::lcm).orElseThrow();
        System.out.println(start + increment);
    }

    private static void readNode(String line, BiConsumer<String, Node> sink) {
        Matcher matcher = NODE_REGEX.matcher(line);
        if (!matcher.matches()) throw new IllegalArgumentException();

        sink.accept(matcher.group(1), new Node(matcher.group(2), matcher.group(3)));
    }

    private record Node(String left, String right) {
    }

    private enum Direction {
        LEFT,
        RIGHT;

        public static Direction of(char c) {
            return switch (c) {
                case 'L' -> LEFT;
                case 'R' -> RIGHT;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
