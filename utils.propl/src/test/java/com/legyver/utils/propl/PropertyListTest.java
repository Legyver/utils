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

    @Test
    public void overrideProperties() {
        PropertyList modified = new PropertyList();
        for (String s : propertyList.stringPropertyNames()) {
            modified.put(s, propertyList.getProperty(s));
        }
        assertThat(modified.size()).isEqualTo(4);
        modified.put( "three.value", "thrice");
        assertThat(modified.size()).isEqualTo(4);//it should have replaced the value
        assertThat(modified.getProperty("three.value")).isEqualTo("thrice");

        modified.put("two.value", "twice");
        assertThat(modified.size()).isEqualTo(4);//it should have replaced the value
        assertThat(modified.stringPropertyNames()).containsExactly(
                "one.value",
                "two.value",//it should have replaced in same position
                "generic.comment.followup",
                "three.value"
        );
        assertThat(modified.getProperty("two.value")).isEqualTo("twice");
    }

    @Test
    public void removeProperties() {
        PropertyList modified = new PropertyList();
        for (String s : propertyList.stringPropertyNames()) {
            modified.put(s, propertyList.getProperty(s));
        }
        assertThat(modified.size()).isEqualTo(4);
        modified.remove("three.value");
        assertThat(modified.size()).isEqualTo(3);//it should have removed the value
        assertThat(modified.stringPropertyNames()).containsExactlyElementsOf(Arrays.asList(
                "one.value",
                "two.value",
                "generic.comment.followup"
        ));
    }
}
