package com.legyver.utils.jsonmigration.version;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        String[] version1 = o1.split("\\.");
        String[] version2 = o2.split("\\.");

        int limit = version1.length > version2.length ? version2.length : version1.length;
        for (int i = 0; i < limit; i++) {
            String part1 = version1[i];
            String part2 = version2[i];
            Integer number1 = Integer.parseInt(part1);
            Integer number2 = Integer.parseInt(part2);
            int comparison = number1.compareTo(number2);
            if (comparison != 0) {
                return comparison;
            }
        }
        //if we're still here's they are equal for the first limit bits => return the bigger one
        Integer len1 = version1.length;
        Integer len2 = version2.length;
        return len1.compareTo(len2);
    }
}
