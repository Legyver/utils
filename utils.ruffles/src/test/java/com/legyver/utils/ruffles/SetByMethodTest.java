package com.legyver.utils.ruffles;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SetByMethodTest {

    @Test
    public void emptyCollectionFallsThroughToDirectFieldWrite() throws Exception {
        //regression test: setting an empty collection used to mark success=true without ever
        //writing anything, silently leaving the field at its previous value
        Field field = CollectionHolder.class.getDeclaredField("items");
        CollectionHolder holder = new CollectionHolder();
        assertThat(holder.getItems()).containsExactly("initial");

        new SetByMethod(field).set(holder, new ArrayList<String>());

        assertThat(holder.getItems()).isEmpty();
    }

    public static class CollectionHolder {
        private List<String> items = new ArrayList<>(Arrays.asList("initial"));

        public List<String> getItems() {
            return items;
        }
    }
}
