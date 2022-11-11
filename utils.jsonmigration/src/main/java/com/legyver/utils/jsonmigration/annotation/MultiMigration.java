package com.legyver.utils.jsonmigration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiMigration {
    Migration[] value();
}
