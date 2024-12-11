package juuxel.advent2023;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Day13 {
    public static void main(String[] args) throws Exception {
        run(Loader.lines(13));
    }

    public static void run(Stream<String> lines) {
        var grids = readGrids(lines);
        int[] part1ReflectionLines = part1(grids);
        var smudgedGrids = grids.stream().map(Day13::smudge).toList();
        long horizontal = 0;
        long vertical = 0;

        int gridIndex = -1;
        outer: for (List<Grid<Character>> alternatives : smudgedGrids) {
            gridIndex++;
            for (Grid<Character> grid : alternatives) {
                // try vertical mirroring first, columns are optimised in ArrayGrid
                var columns = grid.columns();
                for (int x = 1; x < grid.width(); x++) {
                    int mirroredSize = Math.min(x, grid.width() - x);
                    if (part1ReflectionLines[gridIndex] == -x) continue;
                    if (columns.subList(x - mirroredSize, x).equals(columns.subList(x, x + mirroredSize).reversed())) {
                        vertical += x;
                        continue outer;
                    }
                }

                var rows = grid.rows();
                for (int y = 1; y < grid.height(); y++) {
                    int mirroredSize = Math.min(y, grid.height() - y);
                    if (part1ReflectionLines[gridIndex] == y) continue;
                    if (rows.subList(y - mirroredSize, y).equals(rows.subList(y, y + mirroredSize).reversed())) {
                        horizontal += y;
                        continue outer;
                    }
                }
            }

            throw new IllegalStateException("huh!?");
        }

        System.out.println(100 * horizontal + vertical);
    }

    private static int[] part1(List<CharGrid> grids) {
        int[] reflectionLines = new int[grids.size()];
        int gridIndex = 0;
        outer: for (CharGrid grid : grids) {
            // try horizontal mirroring first, rows are optimised in CharGrid
            var rows = grid.rows();
            for (int y = 1; y < grid.height(); y++) {
                int mirroredSize = Math.min(y, grid.height() - y);
                if (rows.subList(y - mirroredSize, y).equals(rows.subList(y, y + mirroredSize).reversed())) {
                    reflectionLines[gridIndex++] = y;
                    continue outer;
                }
            }

            var columns = grid.columns();
            for (int x = 1; x < grid.width(); x++) {
                int mirroredSize = Math.min(x, grid.width() - x);
                if (columns.subList(x - mirroredSize, x).equals(columns.subList(x, x + mirroredSize).reversed())) {
                    reflectionLines[gridIndex++] = -x;
                    continue outer;
                }
            }

            throw new IllegalStateException("huh!?");
        }

        int score = 0;
        for (int reflectionLine : reflectionLines) {
            score += reflectionLine * (reflectionLine > 0 ? 100 : -1);
        }
        System.out.println(score);
        return reflectionLines;
    }

    private static List<CharGrid> readGrids(Stream<String> lines) {
        List<CharGrid> grids = new ArrayList<>();
        List<String> buffer = new ArrayList<>();

        var iter = lines.iterator();
        while (iter.hasNext()) {
            var line = iter.next();
            if (line.isEmpty()) {
                grids.add(new CharGrid(List.copyOf(buffer)));
                buffer.clear();
            } else {
                buffer.add(line);
            }
        }

        if (!buffer.isEmpty()) {
            grids.add(new CharGrid(List.copyOf(buffer)));
        }

        return grids;
    }

    private static List<Grid<Character>> smudge(CharGrid grid) {
        List<Grid<Character>> output = new ArrayList<>(grid.width() * grid.height());
        for (int y = 0; y < grid.height(); y++) {
            for (int x = 0; x < grid.width(); x++) {
                var newGrid = new ArrayGrid<Character>(grid.width(), grid.height());
                for (int nx = 0; nx < grid.width(); nx++) {
                    for (int ny = 0; ny < grid.height(); ny++) {
                        char c = grid.getChar(nx, ny);
                        if (x == nx && y == ny) c = (c == '#') ? '.' : '#';
                        newGrid.set(nx, ny, c);
                    }
                }
                output.add(newGrid);
            }
        }
        return output;
    }
}
