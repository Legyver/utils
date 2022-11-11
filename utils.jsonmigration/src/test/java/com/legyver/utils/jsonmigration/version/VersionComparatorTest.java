package com.legyver.utils.jsonmigration.version;

import com.legyver.utils.jsonmigration.version.VersionComparator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionComparatorTest {

    @Test
    public void testBalancedMajor() {
        {
            List<String> values = Arrays.asList("1.0", "2.0", "3.0", "11.0");
            values.sort(new VersionComparator());
            assertThat(values).containsExactly("1.0", "2.0", "3.0", "11.0");
        }
        {
            List<String> values = Arrays.asList( "3.0", "1.0", "11.0", "2.0");
            values.sort(new VersionComparator());
            assertThat(values).containsExactly("1.0", "2.0", "3.0", "11.0");
        }
    }

    @Test
    public void testBalancedMinor() {
        List<String> values = Arrays.asList("1.0", "1.2", "1.9", "1.11", "1.1");
            values.sort(new VersionComparator());
        assertThat(values).containsExactly("1.0", "1.1", "1.2", "1.9", "1.11");
    }

    @Test
    public void testBalancedPatch() {
        List<String> values = Arrays.asList("1.0.0", "1.0.2", "1.0.9", "1.0.11", "1.0.1");
            values.sort(new VersionComparator());
        assertThat(values).containsExactly("1.0.0", "1.0.1", "1.0.2", "1.0.9", "1.0.11");
    }

    @Test
    public void testUnbalanced() {
        List<String> values = Arrays.asList("1", "1.0.2", "1.1", "1.11", "9.0");
        values.sort(new VersionComparator());
        assertThat(values).containsExactly("1", "1.0.2", "1.1", "1.11", "9.0");
    }




}
