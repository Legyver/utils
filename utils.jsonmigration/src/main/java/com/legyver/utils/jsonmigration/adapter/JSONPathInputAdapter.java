package com.legyver.utils.jsonmigration.adapter;

import com.jayway.jsonpath.JsonPath;
import com.legyver.core.exception.CoreException;
import com.legyver.utils.jsonmigration.annotation.MultiMigration;
import com.legyver.utils.jsonmigration.annotation.Migration;
import com.legyver.utils.jsonmigration.version.VersionSelector;
import com.legyver.utils.mapadapt.TypedMapAdapter;
import com.legyver.utils.ruffles.SetByMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * Adapt a Map to a POJO.
 * Migration is taken into account if the field value is not already known.
 *
 * For example, if you have version 1 JSON and the POJO has changed since then but you want those values to be migrated,
 * then the value will be null when reading the field name from the map.  The field is then examined for possible migrations.
 * If there are migrations, the value is read from the JSONPath identified on the @Migration.
 *
 * Once the value has been saved in the new format to json, when re-reading this JSON, the migration will not be needed.
 *
 * @param <T> the type of the class the JSON is to be mapped on to.
 *
 * @since 3.4
 */
public class JSONPathInputAdapter<T> {
    private static final Logger logger = LogManager.getLogger(JSONPathInputAdapter.class);
    private Class<T> klass;

    /**
     * Construct an adapter for a specified POJO class
     * @param klass the class of the POJO the JSON will be mapped onto
     */
    public JSONPathInputAdapter(Class<T> klass) {
        this.klass = klass;
    }

    /**
     * Map the values specified in a map onto a POJO taking into account any identified migration.
     * @param version the version of the input JSON (as read to the Map)
     * @param data the mapped data
     * @return the POJO
     * @throws CoreException if there is an error accessing or writing to the fields.
     */
    public T adapt(String version, Map<String, Object> data) throws CoreException {
        try {
            T result = klass.getDeclaredConstructor().newInstance();

            for (Field field : FieldUtils.getAllFields(klass)) {
                Boolean valueReady = Boolean.FALSE;
                String fieldName = field.getName();
                Object value = data.get(fieldName);

                if (value != null) {
                    //assume latest spec
                    valueReady = Boolean.TRUE;
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

                if (!valueReady && fieldIsEntity(field)) {
                    JSONPathInputAdapter<?> adapter = new JSONPathInputAdapter<>(field.getType());
                    //handle objects that may contain variables and migrations
                    value = adapter.adapt(version, data);
                }
                if (value != null) {
                    value = getAdaptedValue(version, field, Map.of(fieldName, value));
                    String mutatorName = "set" + StringUtils.capitalize(fieldName);
                    new SetByMethod(field).set(result, value);
                }
            }
            return result;
        } catch (CoreException coreException) {
            throw coreException;
        } catch (Exception e) {
            throw new CoreException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean fieldIsEntity(Field field) {
        Class fieldType = field.getType();
        boolean result;
        if (fieldType.isPrimitive()
                || String.class.isAssignableFrom(fieldType)
                || Number.class.isAssignableFrom(fieldType)
                || Temporal.class.isAssignableFrom(fieldType)
                || Boolean.class.isAssignableFrom(fieldType)
                || Collection.class.isAssignableFrom(fieldType)
                || Map.class.isAssignableFrom(fieldType)
        ) {
            result = false;
        } else {
            result = true;
        }
        return result;
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

    @SuppressWarnings(value = "unchecked")
    private Object getAdaptedValue(String version, Field field, Map<String, Object> data) throws CoreException, IllegalAccessException, ClassNotFoundException {
        String fieldName = field.getName();
        TypedMapAdapter adapter = new TypedMapAdapter(data);
        Object value = data.get(fieldName);
        if (String.class.isAssignableFrom(field.getType())) {
            value = adapter.getString(fieldName);
        } else if (Integer.class.isAssignableFrom(field.getType())) {
            value = adapter.getInteger(fieldName);
        } else if (Double.class.isAssignableFrom(field.getType())) {
            value = adapter.getDouble(fieldName);
        } else if (Long.class.isAssignableFrom(field.getType())) {
            value = adapter.getLong(fieldName);
        } else if (BigInteger.class.isAssignableFrom(field.getType())) {
            value = adapter.getBigInteger(fieldName);
        } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
            value = adapter.getBigDecimal(fieldName);
        } else if (Boolean.class.isAssignableFrom(field.getType())) {
            value = adapter.getBoolean(fieldName);
        } else if (LocalDate.class.isAssignableFrom(field.getType())) {
            value = adapter.getLocalDate(fieldName);
        } else if (LocalTime.class.isAssignableFrom(field.getType())) {
            value = adapter.getLocalTime(fieldName);
        } else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
            value = adapter.getLocalDateTime(fieldName);
        } else if (value instanceof Map) {
            if (!Map.class.isAssignableFrom(field.getType())) {
                //convert to entity
                JSONPathInputAdapter<?> inputAdapter = new JSONPathInputAdapter<>(field.getType());
                value = inputAdapter.adapt(version, (Map<String, Object>) value);
            } else {
                //Rely on Jackson converting the map values correctly.
            }
        } else if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (!collection.isEmpty()) {
                Collection newCollection = null;
                Class klass = null;
                //below assumes collection does not mix native types with entities
                for (Iterator collIt = collection.iterator(); collIt.hasNext(); ) {
                    Object collectionObject = collIt.next();
                    if (collectionObject instanceof Map) {
                        if (newCollection == null) {
                            //just do this once
                            newCollection = instantiateNewCollection(field);
                            value = newCollection;
                            klass = getParameterizedClass(field);
                        }
                        //convert to entity
                        JSONPathInputAdapter<?> inputAdapter = new JSONPathInputAdapter<>(klass);
                        Object convertedObject = inputAdapter.adapt(version, (Map<String, Object>) collectionObject);
                        newCollection.add(convertedObject);
                    } else {
                        break;
                    }
                }
            }
        }
        return value;
    }

    private static Class getParameterizedClass(Field field) throws ClassNotFoundException {
        Class klass;
        Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        Type type = types[0];
        String typeName = type.getTypeName();
        klass = Class.forName(typeName);
        return klass;
    }

    private static Collection instantiateNewCollection(Field field) throws CoreException {
        Collection newCollection;
        if (List.class.isAssignableFrom(field.getType())) {
            newCollection = new ArrayList();
        } else if (Set.class.isAssignableFrom(field.getType())) {
            newCollection = new HashSet();
        } else {
            throw new CoreException("Unsupported collection type: " + field.getType());
        }
        return newCollection;
    }
}
