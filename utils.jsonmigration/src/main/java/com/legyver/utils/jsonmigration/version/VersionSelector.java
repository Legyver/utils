package com.legyver.utils.jsonmigration.version;

import java.util.List;

/**
 * Select the version to use from a list of versions.
 * The algorithm is: choose the migration pattern of the latest version that supports the version of the json.
 * Because versions are exact and migrations operate on ranges, we don't always have a migration for every version.
 * Thus, we choose the migration supporting the range the JSON version falls within.
 *
 * @since 3.4
 */
public class VersionSelector {
    private final List<String> knownMigrationVersions;

    /**
     * Construct a version selector to select the version to use.
     * @param knownMigrationVersions migration versions
     */
    public VersionSelector(List<String> knownMigrationVersions) {
        this.knownMigrationVersions = knownMigrationVersions;
    }

    /**
     * Select the migration to use from the list of migrations
     * @param version the version of the JSON
     * @return the migration to use for reading that version.  Returns null if none is found.
     */
    public String getMatchingMigrationVersion(String version) {
        if (knownMigrationVersions == null || knownMigrationVersions.isEmpty()) {
            return null;
        }
        if (knownMigrationVersions.size() == 1) {
            return knownMigrationVersions.get(0);
        }
        knownMigrationVersions.sort(new VersionComparator());

        //sorted earliest to latest => process in reverse
        for (int i = knownMigrationVersions.size() - 1; i >= 0; i--) {
            String candidate = knownMigrationVersions.get(i);
            //choose the most recent one that handles the version
            int compared = new VersionComparator().compare(candidate, version);
            if (compared < 1) {
                return candidate;
            }
        }
        return null;
    }
}
