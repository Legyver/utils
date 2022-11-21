package com.legyver.utils.jsonmigration.adapter;

import com.legyver.utils.jackiso.JacksonObjectMapper;
import com.legyver.utils.jsonmigration.annotation.Migration;
import org.junit.jupiter.api.Test;

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
}
