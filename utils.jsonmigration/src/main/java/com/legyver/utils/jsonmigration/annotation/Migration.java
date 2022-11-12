package com.legyver.utils.jsonmigration.annotation;

import java.lang.annotation.*;

/**
 * Annotate a field with the JSONPath migration pattern and version it applies to.
 * @since 3.4
 */
@Target({
        ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MultiMigration.class)
public @interface Migration {
    /**
     * The first unsupported version.
     * @return the first unsupported version
     */
    String pre() default "1.0.0";

    /**
     * The JSON Path to use for all versioned JSON earlier than version specified by pre.
     * There is no lower bound.  If there are multiple versions with multiple migrations,
     * you can annotate the field multiple times.
     * The value must be valid JSONPath.
     *
     * We choose the latest version that is not greater than or equal to the pre version.
     * @return the JSON Path.
     */
    String path();
}
