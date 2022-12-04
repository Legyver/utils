package com.legyver.utils.ruffles;

import com.legyver.core.exception.CoreException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Utility to set a value on an object
 */
public class SetByMethod {
    private static final Logger logger = LogManager.getLogger(SetByMethod.class);

    private final Field field;
    private final String fieldName;

    /**
     * Construct a utility to set the value of a field
     * @param field the field thats value must be set.
     */
    public SetByMethod(Field field) {
        this.field = field;
        this.fieldName = field.getName();
    }

    /**
     * Attempt to construct the public method name before resulting to reflection
     * @param object the object to set the value on
     * @param value the value to set on the object
     * @throws CoreException if no guessed method names exist with the appropriate types and the package is not opened to org.apache.commons.lang3
     */
    public void set(Object object, Object value) throws CoreException {
        String mutatorName = "set" + StringUtils.capitalize(fieldName);
        boolean success = false;
        try {
            setWithMethodName(object, mutatorName, value);
            success = true;
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException exception) {
            logger.debug(exception);
            mutatorName = null;
            boolean mutatorNameFinalized = false;
            try {
                if (value instanceof Collection && Collection.class.isAssignableFrom(field.getType())) {
                    Collection collection = (Collection) value;
                    if (collection.isEmpty()) {
                        success = true;
                    } else {
                        for (Object v : collection) {
                            try {
                                if (!mutatorNameFinalized) {
                                    mutatorName = "add" + StringUtils.capitalize(fieldName);
                                }
                                setWithMethodName(object, mutatorName, v);
                                mutatorNameFinalized = true;
                                success = true;
                            } catch (InvocationTargetException | NoSuchMethodException exception1) {
                                logger.debug(exception1);
                                if (!mutatorNameFinalized && mutatorName.endsWith("s")) {
                                    mutatorName = mutatorName.substring(0, mutatorName.length() - 1);
                                }
                                setWithMethodName(object, mutatorName, v);
                                mutatorNameFinalized = true;
                                success = true;
                            }
                        }
                    }
                }
            } catch (Exception exception1) {
                logger.error(exception1);
            }
        } finally {
            if (!success) {
                try {
                    FieldUtils.writeField(field, object, value, true);
                } catch (IllegalAccessException e) {
                    throw new CoreException(e);
                }
            }
        }
    }

    private static void setWithMethodName(Object object, String mutatorName, Object v) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = object.getClass().getMethod(mutatorName, v.getClass());
        if (method.canAccess(object)) {
            method.invoke(object, v);
        } else {
            //Check if Apache has access
            MethodUtils.invokeMethod(object, mutatorName, v);
        }
    }
}
