package com.legyver.utils.jsonmigration.version;

import com.legyver.utils.jsonmigration.annotation.Migration;

import java.util.Comparator;

public class MigrationVersionComparator implements Comparator<Migration> {
    @Override
    public int compare(Migration o1, Migration o2) {
        return new VersionComparator().compare(o1.pre(), o2.pre());
    }
}
