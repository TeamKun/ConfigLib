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
        String actual = String.join(System.lineSeparator(), lines);
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
            assertThat(actual.replace("\r\n", "\n")).isEqualTo(expected);
        } catch (IOException e) {
            throw new AssertionError("Could not read snapshot: " + path.toAbsolutePath() + System.lineSeparator() + "Actual snapshot:" + System.lineSeparator() + actual,
                                     e);
        }
    }
}
