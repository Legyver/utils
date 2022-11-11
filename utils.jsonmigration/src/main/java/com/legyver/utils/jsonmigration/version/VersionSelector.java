package com.legyver.utils.jsonmigration.version;

import java.util.List;

public class VersionSelector {
    private final List<String> knownMigrationVersions;

    public VersionSelector(List<String> knownMigrationVersions) {
        this.knownMigrationVersions = knownMigrationVersions;
    }

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
