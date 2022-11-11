package com.legyver.utils.jsonmigration.version;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionSelectorTest {

    @Test
    public void noVersionsKnown() {
        {
            VersionSelector versionSelector = new VersionSelector(null);
            assertThat(versionSelector.getMatchingMigrationVersion("1.0.0")).isNull();
        }
        {
            VersionSelector versionSelector = new VersionSelector(Collections.emptyList());
            assertThat(versionSelector.getMatchingMigrationVersion("1.0.0")).isNull();
        }
    }

    @Test
    public void oneMigrationVersionKnown() {
        VersionSelector versionSelector = new VersionSelector(Arrays.asList("1.0.0"));
        assertThat(versionSelector.getMatchingMigrationVersion("2.0.0")).isEqualTo("1.0.0");
    }

    @Test
    public void multipleMigrationVersionKnown() {
        VersionSelector versionSelector = new VersionSelector(Arrays.asList("1.0.0", "1.2.0", "3.0.0"));
        assertThat(versionSelector.getMatchingMigrationVersion("3.0.1")).isEqualTo("3.0.0");
        assertThat(versionSelector.getMatchingMigrationVersion("3.0.0")).isEqualTo("3.0.0");
        assertThat(versionSelector.getMatchingMigrationVersion("2.0.0")).isEqualTo("1.2.0");
        assertThat(versionSelector.getMatchingMigrationVersion("1.3.0")).isEqualTo("1.2.0");
        assertThat(versionSelector.getMatchingMigrationVersion("1.2.0")).isEqualTo("1.2.0");
        assertThat(versionSelector.getMatchingMigrationVersion("1.1.0")).isEqualTo("1.0.0");
        assertThat(versionSelector.getMatchingMigrationVersion("1.0.0")).isEqualTo("1.0.0");
    }
}
