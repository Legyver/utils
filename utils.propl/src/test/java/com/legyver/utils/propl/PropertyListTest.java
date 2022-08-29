package com.legyver.utils.propl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyListTest {
    private static PropertyList propertyList = new PropertyList();

    @BeforeAll
    public static void init() throws IOException {
        try (InputStream inputStream = PropertyListTest.class.getResourceAsStream("sample.properties")) {
            propertyList.load(inputStream);
            assertThat(propertyList.size()).isEqualTo(5);//4 properties and 1 comment
        }
    }

    @Test
    public void copyPropertiesWithComments() {
        PropertyList copied = new PropertyList();
        for (String s : propertyList.stringPropertyNames(true)) {
            copied.put(s, propertyList.getProperty(s));
        }

        assertThat(copied.size()).isEqualTo(5);//4 properties and 1 comment

        assertThat(copied.stringPropertyNames()).containsExactlyElementsOf(Arrays.asList(
                "one.value",
                "two.value",
                "## preserve this comment",
                "generic.comment.followup",
                "three.value"
        ));
    }

    @Test
    public void copyPropertiesWithoutComments() {
        PropertyList copied = new PropertyList();
        for (String s : propertyList.stringPropertyNames()) {
            copied.put(s, propertyList.getProperty(s));
        }

        assertThat(copied.size()).isEqualTo(4);//4 properties, no comments

        assertThat(copied.stringPropertyNames()).containsExactlyElementsOf(Arrays.asList(
                "one.value",
                "two.value",
                "generic.comment.followup",
                "three.value"
        ));
    }
}
