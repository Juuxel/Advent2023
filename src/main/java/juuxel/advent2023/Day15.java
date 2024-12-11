package juuxel.advent2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Day15 {
    public static void main(String[] args) throws Exception {
        part1(Loader.lines(15));
        part2(Loader.lines(15));
    }

    public static void part1(Stream<String> lines) {
        var line = lines.toList().getFirst();
        System.out.println(Arrays.stream(line.split(",")).mapToInt(Day15::hash).sum());
    }

    private static int hash(String s) {
        int hash = 0;

        for (int i = 0; i < s.length(); i++) {
            hash += s.charAt(i);
            hash *= 17;
            hash %= 256;
        }

        return hash;
    }

    public static void part2(Stream<String> lines) {
        record Lens(String name, int focalLength) {
        }

        List<List<Lens>> boxes = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) boxes.add(new ArrayList<>());

        for (String insn : lines.toList().getFirst().split(",")) {
            if (insn.indexOf('=') >= 0) {
                String[] parts = insn.split("=");
                String label = parts[0];
                var box = boxes.get(hash(label));
                int focalLength = Integer.parseInt(parts[1]);
                var lens = new Lens(label, focalLength);

                boolean replaced = false;
                for (int i = 0; i < box.size(); i++) {
                    if (box.get(i).name().equals(label)) {
                        box.set(i, lens);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) box.add(lens);
            } else {
                var label = insn.substring(0, insn.length() - 1);
                var box = boxes.get(hash(label));
                for (int i = 0; i < box.size(); i++) {
                    if (box.get(i).name().equals(label)) {
                        box.remove(i);
                        break;
                    }
                }
            }
        }

        int power = 0;
        for (int i = 0; i < boxes.size(); i++) {
            var box = boxes.get(i);
            for (int j = 0; j < box.size(); j++) {
                var lens = box.get(j);
                power += (i + 1) * (j + 1) * lens.focalLength;
            }
        }
        System.out.println(power);
    }
}
