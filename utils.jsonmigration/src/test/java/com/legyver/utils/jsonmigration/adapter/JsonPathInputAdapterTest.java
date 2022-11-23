package com.legyver.utils.jsonmigration.adapter;

import com.legyver.utils.jackiso.JacksonObjectMapper;
import com.legyver.utils.jsonmigration.annotation.Migration;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPathInputAdapterTest {

    /**
     * v1 should be able to be read by v1, v2, v3
     */
    @Test
    public void originalSimple() throws Exception {
        String spec1Json = "{"
                + "\"version\": \"1.0.0\","
                + "\"name\": \"Test name\""
                + "}";
        Map<String, Object> data = JacksonObjectMapper.INSTANCE.readValue(spec1Json, Map.class);
        {
            Spec1 spec1 = new JSONPathInputAdapter<>(Spec1.class).adapt("1.0.0", data);
            assertThat(spec1.name).isEqualTo("Test name");
        }
        {
            Spec2 spec2 = new JSONPathInputAdapter<>(Spec2.class).adapt("2.0.0", data);
            assertThat(spec2.renamed).isEqualTo("Test name");
        }
        {
            Spec2 spec2 = new JSONPathInputAdapter<>(Spec2.class).adapt("2.0.3", data);
            assertThat(spec2.renamed).isEqualTo("Test name");
        }
        {
            Spec3 spec3 = new JSONPathInputAdapter<>(Spec3.class).adapt("3.0.0", data);
            assertThat(spec3.data.name).isEqualTo("Test name");
        }
    }

    /**
     * v2 should be able to be read by v2 and v3
     */
    @Test
    public void v2Simple() throws Exception {
        String spec2Json = "{"
                + "\"version\": \"2.0.0\","
                + "\"renamed\": \"Test name\""
                + "}";
        Map<String, Object> data = JacksonObjectMapper.INSTANCE.readValue(spec2Json, Map.class);

        {
            Spec2 spec2 = new JSONPathInputAdapter<>(Spec2.class).adapt("2.0.0", data);
            assertThat(spec2.renamed).isEqualTo("Test name");
        }
        {
            Spec2 spec2 = new JSONPathInputAdapter<>(Spec2.class).adapt("2.0.3", data);
            assertThat(spec2.renamed).isEqualTo("Test name");
        }
        {
            Spec3 spec3 = new JSONPathInputAdapter<>(Spec3.class).adapt("3.0.0", data);
            assertThat(spec3.data.name).isEqualTo("Test name");
        }
    }

    /**
     * v3 should be able to be read by v3
     */
    @Test
    public void v3Simple() throws Exception {
        String spec3Json = "{"
                + "\"version\": \"3.0.0\","
                + "\"data\": {"
                    + "\"name\": \"Test name\""
                + "}"
                + "}";

        Map<String, Object> data = JacksonObjectMapper.INSTANCE.readValue(spec3Json, Map.class);

        Spec3 spec3 = new JSONPathInputAdapter<>(Spec3.class).adapt("3.0.0", data);
        assertThat(spec3.data.name).isEqualTo("Test name");
    }

    @Test
    public void missingSection() throws Exception {
        String spec3Json = "{"
                + "\"version\": \"3.0.0\","
                + "\"data\": {"
                + "\"name\": \"Test name\""
                + "}"
                + "}";
        Map<String, Object> data = JacksonObjectMapper.INSTANCE.readValue(spec3Json, Map.class);
        Spec4 spec4 = new JSONPathInputAdapter<>(Spec4.class).adapt("4.0.0", data);
        assertThat(spec4.data.name).isEqualTo("Test name");
        assertThat(spec4.data4.missingData).isNull();
    }

    @Test
    public void withListOfString() throws Exception {
        String spec5Json = "{"
                + "\"version\": \"5.0.0\","
                + "\"values\": [\"Test 1\", \"Test 2\"]"
                + "}";
        Map<String, Object> data = JacksonObjectMapper.INSTANCE.readValue(spec5Json, Map.class);
        Spec5 spec = new JSONPathInputAdapter<>(Spec5.class).adapt("5.0.0", data);
        assertThat(spec.values).isNotNull();
        assertThat(spec.values).containsExactly("Test 1", "Test 2");
    }

    @Test
    public void withListOfEntities() throws Exception {
        String spec6Json = "{\n" +
                "  \"version\": \"6.0.0\",\n" +
                "  \"config\": {\n" +
                "    \"lastOpened\": {\n" +
                "      \"filePath\": \"/temp/tmp/Name 1.ext\"\n" +
                "    },\n" +
                "    \"recentFiles\": {\n" +
                "      \"limit\": 5,\n" +
                "      \"values\": [\n" +
                "        {\n" +
                "          \"lastAccessed\": \"2018-07-14T17:10:57\",\n" +
                "          \"name\": \"Name 1\",\n" +
                "          \"path\": \"/temp/tmp/Name 1.ext\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"lastAccessed\": \"2018-07-14T13:10:57\",\n" +
                "          \"name\": \"Name 2\",\n" +
                "          \"path\": \"/temp/tmp/Name 2.ext\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                " ";
        Map<String, Object> data = JacksonObjectMapper.INSTANCE.readValue(spec6Json, Map.class);
        Spec6 spec = new JSONPathInputAdapter<>(Spec6.class).adapt("6.0.0", data);


        Config config = spec.config;
        assertThat(config).isNotNull();
        assertThat(config.lastOpened).isNotNull();
        assertThat(config.lastOpened.filePath).isEqualTo("/temp/tmp/Name 1.ext");
        assertThat(config.recentFiles).isNotNull();
        assertThat(config.recentFiles.limit).isEqualTo(5);

        RecentFiles recentFiles = config.recentFiles;
        {
            RecentFile file = recentFiles.values.get(0);
            assertThat(file.lastAccessed).isEqualTo("2018-07-14T17:10:57");
            assertThat(file.name).isEqualTo("Name 1");
            assertThat(file.path).isEqualTo("/temp/tmp/Name 1.ext");
        }
        {
            RecentFile file = recentFiles.values.get(1);
            assertThat(file.lastAccessed).isEqualTo("2018-07-14T13:10:57");
            assertThat(file.name).isEqualTo("Name 2");
            assertThat(file.path).isEqualTo("/temp/tmp/Name 2.ext");
        }

    }

    public static class Spec1 {
        private String version = "1.0.0";
        private String name;
    }


    public static class Spec2 {
        private String version = "2.0.0";
        @Migration(pre = "2.0.0", path = "$.name")
        private String renamed;
    }

    public static class Spec3 {
        private String version = "3.0.0";
        private Data3 data;
    }

    public static class Data3 {
        @Migration(pre = "3.0.0", path = "$.renamed")
        @Migration(pre = "2.0.0", path = "$.named")
        private String name;
    }

    public static class Spec4 {
        private String version = "4.0.0";

        private Data3 data;
        private Data4 data4;
    }

    public static class Data4 {
        private String missingData;
    }

    public static class Spec5 {
        private String version = "5.0.0";
        private List<String> values;
    }

    public static class Spec6 {
        private String version = "6.0.0";
        private Config config;
    }

    public static class Config {
        private RecentFiles recentFiles;
        private LastOpened lastOpened;
    }

    public static class RecentFiles {
        private List<RecentFile> values = new ArrayList<>();
        private int limit = 10;
    }

    public static class LastOpened {
        private String filePath;
    }

    public
    static class RecentFile {
        private String name;
        private String path;
        private LocalDateTime lastAccessed;
    }
}
