package com.legyver.utils.jsonmigration.annotation;

import java.lang.annotation.*;

@Target({
        ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MultiMigration.class)
public @interface Migration {
    String pre() default "1.0.0";
    String path();
}
