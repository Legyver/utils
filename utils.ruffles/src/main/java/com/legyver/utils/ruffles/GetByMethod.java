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

/**
 * Utility class to retrieve a value by reflection.
 * The main purpose of this class is to accommodate various module "opens" statements.
 * If the module of the class being reflected has opened the package to com.legyver.utils.ruffles,
 * the direct reflection method will be used.
 * However, if this is not the case, the Apache reflection utils module (org.apache.commons.lang3) is also an acceptable alternative.
 * If there is no accessor method at all, the field will be attempted to be read via reflection.
 */
public class GetByMethod {
    private static final Logger logger = LogManager.getLogger(GetByMethod.class);

    private final Field field;

    /**
     * Construct a getter utility to retrieve the value from a field
     * @param field the field to retrieve the value of
     */
    public GetByMethod(Field field) {
        this.field = field;
    }

    /**
     * Retrieve the value of a field set on an object
     * @param object the object to retrieve the field value from
     * @return the value of the field
     * @throws CoreException if there is an error accessing the method or field
     */
    public Object get(Object object) throws CoreException {
        Object result = null;
        String accessorName = "get" + StringUtils.capitalize(field.getName());
        try {
            logger.trace("Attempting to get value with method: {}", accessorName);
            result = getWithMethodName(object, accessorName);
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException exception) {
            logger.debug("Failed to retrieve value by method", exception);
            logger.debug("Attempting to read the value of field [{}] directly", field.getName());
            return getByField(field);
        }
        return result;
    }

    private static Object getWithMethodName(Object object, String methodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = object.getClass().getMethod(methodName);
        if (method.canAccess(object)) {
            return method.invoke(object);
        } else {
            //Check if Apache has access
            return MethodUtils.invokeMethod(object, methodName);
        }
    }

    private Object getByField(Object object) throws CoreException {
        Object result = null;
        try {
            if (field.canAccess(object)) {
                result = field.get(object);
            } else {
                //Check if Apache has access
                result = FieldUtils.readField(field, object, true);
            }
        } catch (NoSuchFieldError | IllegalAccessException fieldError) {
            logger.error(fieldError);
            throw new CoreException(fieldError);
        }
        return result;
    }
}
