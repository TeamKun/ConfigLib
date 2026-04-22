package net.kunmc.lab.configlib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class SnapshotAssertions {
    private SnapshotAssertions() {
    }

    static void assertMatchesSnapshot(String snapshotName, List<String> lines) {
        Path path = Path.of("src/test/resources/snapshots", snapshotName);
        String actual = renderSnapshot(lines);
        if (Boolean.parseBoolean(System.getenv("UPDATE_SNAPSHOTS"))) {
            try {
                Files.createDirectories(path.getParent());
                Files.writeString(path, actual, StandardCharsets.UTF_8);
                return;
            } catch (IOException e) {
                throw new AssertionError("Could not write snapshot: " + path.toAbsolutePath(), e);
            }
        }

        try {
            String expected = Files.readString(path, StandardCharsets.UTF_8)
                                   .replace("\r\n", "\n")
                                   .stripTrailing();
            assertThat(actual).isEqualTo(expected);
        } catch (IOException e) {
            throw new AssertionError("Could not read snapshot: " + path.toAbsolutePath() + System.lineSeparator() + "Actual snapshot:" + System.lineSeparator() + actual,
                                     e);
        }
    }

    private static String renderSnapshot(List<String> lines) {
        return lines.stream()
                    .map(SnapshotAssertions::escapeLine)
                    .collect(java.util.stream.Collectors.joining("\n"));
    }

    private static String escapeLine(String line) {
        StringBuilder sb = new StringBuilder();
        line.codePoints()
            .forEach(codePoint -> appendEscaped(sb, codePoint));
        return sb.toString();
    }

    private static void appendEscaped(StringBuilder sb, int codePoint) {
        if (codePoint == '\\') {
            sb.append("\\\\");
            return;
        }
        if (codePoint == '\t') {
            sb.append("\\t");
            return;
        }
        if (codePoint >= 0x20 && codePoint <= 0x7E) {
            sb.append((char) codePoint);
            return;
        }
        if (codePoint <= 0xFFFF) {
            sb.append(String.format("\\u%04X", codePoint));
            return;
        }
        for (char c : Character.toChars(codePoint)) {
            sb.append(String.format("\\u%04X", (int) c));
        }
    }
}
