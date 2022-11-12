package com.legyver.utils.jsonmigration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Collect multiple {@link Migration} patterns for a field.
 * @since 3.4
 */
@Target({
        ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiMigration {

    /**
     * Migration patterns for the field
     * @return the migration patterns
     */
    Migration[] value();
}
