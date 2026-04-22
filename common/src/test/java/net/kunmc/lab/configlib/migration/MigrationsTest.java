package net.kunmc.lab.configlib.migration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MigrationsTest {
    private final Gson gson = new Gson();

    private JsonObject json(String raw) {
        return JsonParser.parseString(raw)
                         .getAsJsonObject();
    }

    // ---- latestVersion ----

    @Test
    void latestVersionReturnsZeroWhenEmpty() {
        assertEquals(0,
                     Migrations.empty()
                               .latestVersion());
    }

    @Test
    void latestVersionReturnsHighestRegisteredVersion() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.set("field", "v1"))
                                          .migrateTo(3, migration -> migration.set("field", "v3"))
                                          .migrateTo(2, migration -> migration.set("field", "v2"))
                                          .build();

        assertEquals(3, migrations.latestVersion());
    }

    // ---- execute ----

    @Test
    void returnsFalseWhenNoMigrations() {
        Migrations.MigrationResult result = Migrations.empty()
                                                      .execute(0, gson, json("{\"value\":42}"));

        assertFalse(result.migrated());
        assertEquals(42,
                     result.document()
                           .get("value")
                           .getAsInt());
    }

    @Test
    void returnsFalseWhenAlreadyLatestVersion() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.set("field", "should_not_run"))
                                          .build();

        Migrations.MigrationResult result = migrations.execute(1,
                                                               gson,
                                                               json("{\"_version_\":1,\"field\":\"original\"}"));
        assertFalse(result.migrated());
        assertEquals("original",
                     result.document()
                           .get("field")
                           .getAsString());
    }

    @Test
    void appliesAllPendingVersionBlocksInOrder() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1,
                                                     migration -> migration.convert("value",
                                                                                    Integer.class,
                                                                                    Integer.class,
                                                                                    value -> value + 1))
                                          .migrateTo(2,
                                                     migration -> migration.convert("value",
                                                                                    Integer.class,
                                                                                    Integer.class,
                                                                                    value -> value * 10))
                                          .build();

        Migrations.MigrationResult result = migrations.execute(0, gson, json("{\"value\":1}"));

        assertTrue(result.migrated());
        assertEquals(20,
                     result.document()
                           .get("value")
                           .getAsInt());
    }

    @Test
    void appliesOnlyVersionsGreaterThanStoredVersion() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.set("field", "v1"))
                                          .migrateTo(2, migration -> migration.set("field", "v2"))
                                          .migrateTo(3, migration -> migration.set("field", "v3"))
                                          .build();

        Migrations.MigrationResult result = migrations.execute(1,
                                                               gson,
                                                               json("{\"_version_\":1,\"field\":\"original\"}"));

        assertTrue(result.migrated());
        assertEquals("v3",
                     result.document()
                           .get("field")
                           .getAsString());
    }

    @Test
    void executeReturnsMigratedCopyWithoutMutatingSourceDocument() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.rename("oldName", "newName"))
                                          .build();

        JsonObject source = json("{\"oldName\":\"value\"}");
        Migrations.MigrationResult result = migrations.execute(0, gson, source);

        assertTrue(result.migrated());
        assertTrue(source.has("oldName"));
        assertFalse(source.has("newName"));
        assertFalse(result.document()
                          .has("oldName"));
        assertEquals("value",
                     result.document()
                           .get("newName")
                           .getAsString());
    }

    @Test
    void resultIncludesVersionAndOperationReports() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1,
                                                     migration -> migration.rename("oldName", "newName")
                                                                           .defaultValue("missing", 10))
                                          .migrateTo(2, migration -> migration.delete("unknown"))
                                          .build();

        Migrations.MigrationResult result = migrations.execute(0, gson, json("{\"oldName\":\"value\"}"));

        assertEquals(2,
                     result.versionReports()
                           .size());
        assertEquals(1,
                     result.versionReports()
                           .get(0)
                           .version());
        assertEquals(2,
                     result.versionReports()
                           .get(0)
                           .operations()
                           .size());
        assertTrue(result.versionReports()
                         .get(0)
                         .applied());
        assertEquals(MigrationOperationType.RENAME,
                     result.versionReports()
                           .get(0)
                           .operations()
                           .get(0)
                           .type());
        assertEquals("oldName",
                     result.versionReports()
                           .get(0)
                           .operations()
                           .get(0)
                           .path());
        assertEquals("newName",
                     result.versionReports()
                           .get(0)
                           .operations()
                           .get(0)
                           .targetPath());
        assertTrue(result.versionReports()
                         .get(0)
                         .operations()
                         .get(0)
                         .applied());
        assertEquals(MigrationOperationType.DEFAULT_VALUE,
                     result.versionReports()
                           .get(0)
                           .operations()
                           .get(1)
                           .type());
        assertTrue(result.versionReports()
                         .get(0)
                         .operations()
                         .get(1)
                         .applied());
        assertEquals(2,
                     result.versionReports()
                           .get(1)
                           .version());
        assertFalse(result.versionReports()
                          .get(1)
                          .applied());
        assertEquals(MigrationOperationType.DELETE,
                     result.versionReports()
                           .get(1)
                           .operations()
                           .get(0)
                           .type());
        assertFalse(result.versionReports()
                          .get(1)
                          .operations()
                          .get(0)
                          .applied());
        assertEquals(3,
                     result.reports()
                           .size());
    }

    @Test
    void versionBlockIsAtomicWhenLaterOperationFails() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1,
                                                     migration -> migration.rename("oldName", "newName")
                                                                           .set("broken.child", 1))
                                          .build();

        JsonObject document = json("{\"oldName\":\"value\",\"broken\":1}");
        MigrationExecutionException ex = assertThrows(MigrationExecutionException.class,
                                                      () -> migrations.execute(0, gson, document));

        assertEquals(1, ex.version());
        assertEquals(MigrationOperationType.SET, ex.operationType());
        assertEquals("broken.child", ex.path());
        assertEquals(0,
                     ex.completedVersionReports()
                       .size());
        assertEquals(0,
                     ex.completedOperationReports()
                       .size());
        assertEquals(MigrationOperationType.SET,
                     ex.failedOperationReport()
                       .type());
        assertEquals("broken.child",
                     ex.failedOperationReport()
                       .path());
        assertTrue(ex.getMessage()
                     .contains("Migration v1 failed while applying set broken.child"));
        assertTrue(ex.getMessage()
                     .contains("Cannot create nested migration path"));
        assertTrue(document.has("oldName"));
        assertFalse(document.has("newName"));
    }

    @Test
    void exceptionIncludesCompletedReportsBeforeLaterVersionFails() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.rename("oldName", "newName"))
                                          .migrateTo(2, migration -> migration.set("broken.child", 1))
                                          .build();

        MigrationExecutionException ex = assertThrows(MigrationExecutionException.class,
                                                      () -> migrations.execute(0,
                                                                               gson,
                                                                               json("{\"oldName\":\"value\",\"broken\":1}")));

        assertEquals(1,
                     ex.completedVersionReports()
                       .size());
        assertEquals(1,
                     ex.completedVersionReports()
                       .get(0)
                       .version());
        assertEquals(1,
                     ex.completedOperationReports()
                       .size());
        assertEquals(MigrationOperationType.RENAME,
                     ex.completedOperationReports()
                       .get(0)
                       .type());
        assertEquals(MigrationOperationType.SET,
                     ex.failedOperationReport()
                       .type());
        assertEquals(2, ex.version());
    }
}
