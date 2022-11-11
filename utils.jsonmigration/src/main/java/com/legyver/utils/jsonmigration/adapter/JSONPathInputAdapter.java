package com.legyver.utils.jsonmigration.adapter;

import com.jayway.jsonpath.JsonPath;
import com.legyver.core.exception.CoreException;
import com.legyver.utils.jsonmigration.annotation.MultiMigration;
import com.legyver.utils.jsonmigration.annotation.Migration;
import com.legyver.utils.jsonmigration.version.VersionSelector;
import com.legyver.utils.mapadapt.TypedMapAdapter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONPathInputAdapter<T> {
    private static final Logger logger = LogManager.getLogger(JSONPathInputAdapter.class);
    private final Class<T> klass;
    public JSONPathInputAdapter(Class<T> klass) {
        this.klass = klass;
    }

    public T adapt(String version, Map<String, Object> data) throws CoreException {
        try {
            T result = klass.getDeclaredConstructor().newInstance();
            for (Field field: FieldUtils.getAllFields(klass)) {
                Boolean valueReady = Boolean.FALSE;
                String fieldName = field.getName();
                Object value = data.get(fieldName);

                boolean skipMigration = false;

                if (value != null) {
                    //assume latest spec
                    valueReady = Boolean.TRUE;
                    skipMigration = Boolean.TRUE;
                } else {
                    //try to get the value from migration path
                    MultiMigration multiMigration = field.getAnnotation(MultiMigration.class);
                    Migration migration = null;
                    if (multiMigration != null) {
                        migration = selectMigration(version, multiMigration);
                    }
                    if (migration == null) {
                        migration = field.getAnnotation(Migration.class);
                    }
                    if (migration != null) {
                        value = getValueByJsonPath(migration, data);
                        valueReady = Boolean.TRUE;
                    }
                }

                if (!valueReady) {
                    //handle objects that may contain variables and migrations
                    value = new JSONPathInputAdapter<>(field.getType()).adapt(version, data);
                }
                if (value != null) {
                    value = getAdaptedValue(version, field, Map.of(fieldName, value));
                    FieldUtils.writeField(field, result, value, true);
                }
            }
            return result;
        } catch (Exception e) {
            throw new CoreException(e);
        }
    }

    private Migration selectMigration(String version, MultiMigration multiMigration) {
        Map<String, Migration> migrationMap = new HashMap<>();
        List<String> versions = new ArrayList<>(multiMigration.value().length);
        for (int i = 0; i < multiMigration.value().length; i++) {
            Migration migration = multiMigration.value()[i];
            migrationMap.put(migration.pre(), migration);
            versions.add(migration.pre());
        }

        String migrationVersion = new VersionSelector(versions).getMatchingMigrationVersion(version);
        return migrationMap.get(migrationVersion);
    }

    private Object getValueByJsonPath(Migration jsonPath, Map<String, Object> data) {
        String path = jsonPath.path();
        String pathVersion = jsonPath.pre();
        logger.debug("Migrating path [{}] version [{}]", path, pathVersion);
        return JsonPath.read(data, jsonPath.path());
    }

    private Object getAdaptedValue(String version, Field field, Map<String, Object> data) throws CoreException {
        String fieldName = field.getName();
        TypedMapAdapter adapter = new TypedMapAdapter(data);
        Object value = data.get(fieldName);
        if (field.getType().isAssignableFrom(String.class)) {
            value = adapter.getString(fieldName);
        } else if (field.getType().isAssignableFrom(Integer.class)) {
            value = adapter.getInteger(fieldName);
        } else if (field.getType().isAssignableFrom(Double.class)) {
            value = adapter.getDouble(fieldName);
        } else if (field.getType().isAssignableFrom(Long.class)) {
            value = adapter.getLong(fieldName);
        } else if (field.getType().isAssignableFrom(BigInteger.class)) {
            value = adapter.getBigInteger(fieldName);
        } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
            value = adapter.getBigDecimal(fieldName);
        } else if (field.getType().isAssignableFrom(Boolean.class)) {
            value = adapter.getBoolean(fieldName);
        } else if (field.getType().isAssignableFrom(LocalDate.class)) {
            value = adapter.getLocalDate(fieldName);
        } else if (field.getType().isAssignableFrom(LocalTime.class)) {
            value = adapter.getLocalTime(fieldName);
        } else if (field.getType().isAssignableFrom(LocalDateTime.class)) {
            value = adapter.getLocalDateTime(fieldName);
        } else if (value instanceof Map) {
            value = new JSONPathInputAdapter<>(field.getType()).adapt(version, (Map<String, Object>) value);
        }

        return value;
    }


}
