package com.legyver.utils.ruffles;

import com.legyver.core.exception.CoreException;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Instantiate the specified class using the declared constructor first by direct reflection,
 * using Apache as a fallback if in a modular application the package has been opened to it.
 * @param <T> the class of the object to be instantiated
 */
public class ClassInstantiator<T> {
    private static Logger logger = LogManager.getLogger(ClassInstantiator.class);

    private final Class<T> klass;

    /**
     * Construct an instantiator for the given class
     * @param klass the class to instantiate
     */
    public ClassInstantiator(Class<T> klass) {
        this.klass = klass;
    }

    /**
     * Get a new instance of the class using the specified parameters
     * @param parameters the arguments to be passed to the constructor.
     * @return the new instance
     * @throws CoreException if the constructor does not exist, or, in the case of a modular application,
     * the hosting package has not been exported to com.legyver.utils.ruffles or org.apache.commons.lang3
     */
    public T getNewInstance(Object...parameters) throws CoreException {
        T result;

        if (parameters != null) {
            Class<?>[] parameterTypes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[0];
                parameterTypes[i] = parameter.getClass();
            }
            try {
                result = klass.getDeclaredConstructor(parameterTypes).newInstance(parameters);
            } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
                logger.debug(e);
                try {
                    result = ConstructorUtils.invokeConstructor(klass, parameters, parameterTypes);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                    throw new CoreException(ex);
                }
            }
        } else {
            try {
                result = klass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                logger.debug(ex);
                try {
                    result = ConstructorUtils.invokeConstructor(klass);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex2) {
                    throw new CoreException(ex2);
                }
            }
        }
        return result;
    }
}
