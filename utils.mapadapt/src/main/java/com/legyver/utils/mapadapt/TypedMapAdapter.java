package com.legyver.utils.mapadapt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Map adapter that converts values to requested type on retrieval
 */
public class TypedMapAdapter {
    private static final Logger logger = LogManager.getLogger(TypedMapAdapter.class);

    private final Map<String, Object> map;
    private Map<Class, Function<Object, ? extends Object>> transformers = new HashMap<>();

    /**
     * Construct an adapter for a map
     * @param map the map the contents of which you want to adapt
     */
    public TypedMapAdapter(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * Lookup a transformer for a particular class
     * @param klass the class to lookup the transformer for
     * @param <T> the type of the class.  This class is specific exactly to the defined class, so no inheritance is taken into account
     * @return the transformer if any is registered for the requested class
     */
    protected <T> Function<Object, T> lookupTransformer(Class<T> klass) {
        return (Function<Object, T>) transformers.get(klass);
    }

    /**
     * Register a transformer
     * @param klass the class of object to transform to
     * @param transformer the transformer that will perform this transformation
     * @param <T> type of transformed value
     */
    protected <T> void registerTransformer(Class<T> klass, Function<Object, T> transformer) {
        transformers.put(klass, transformer);
    }

    /**
     * Register all transformers.
     * This method is protected to allow for extension.  Override if you need to add additional transformers
     */
    protected void registerTransformers() {
        registerTransformer(String.class, o -> String.valueOf(o));
        registerTransformer(Boolean.class, o -> {
            if ("true".equalsIgnoreCase(o.toString())) {
                return true;
            }
            if ("false".equalsIgnoreCase(o.toString())) {
                return false;
            }
            return null;
        });
        registerTransformer(Integer.class, new NumberFormatCatchingClosure<>(Integer.class, o -> Integer.valueOf(o.toString())));
        registerTransformer(Long.class, new NumberFormatCatchingClosure<>(Long.class, o -> Long.valueOf(o.toString())));
        registerTransformer(Double.class, new NumberFormatCatchingClosure<>(Double.class, o -> Double.valueOf(o.toString())));
        registerTransformer(BigInteger.class, o -> {
            Long longValue;
            if (o instanceof Long) {
                longValue = (Long) o;
            } else if (o instanceof Integer) {
                longValue = ((Integer) o).longValue();
            } else {
                longValue = ((NumberFormatCatchingClosure<Long>) lookupTransformer(Long.class)).apply(o, true);
            }
            if (longValue != null) {
                return BigInteger.valueOf(longValue);
            }
            return null;
        });
        registerTransformer(BigDecimal.class, o -> {
            if (o instanceof Double) {
                return BigDecimal.valueOf((Double) o);
            }

            if (o instanceof Long) {
                return BigDecimal.valueOf((Long) o);
            }
            Double doubleVal = ((NumberFormatCatchingClosure<Double>) lookupTransformer(Double.class)).apply(o, true);
            if (doubleVal != null) {
                return BigDecimal.valueOf(doubleVal);
            }
            return null;
        });
        registerTransformer(LocalDate.class, o -> {
            if (o instanceof LocalDateTime) {
                return ((LocalDateTime) o).toLocalDate();
            } else {
                String sDate = o.toString();
                try {
                    return LocalDate.parse(sDate);
                } catch (DateTimeParseException e) {
                    logger.warn("Unable to parse {} as LocalDate", o);
                }
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(sDate);
                    return localDateTime.toLocalDate();
                } catch (DateTimeParseException e) {
                    logger.warn("Unable to parse {} as LocalDateTime", o);
                }
            }
            return null;
        });
        registerTransformer(LocalTime.class, o -> {
            if (o instanceof LocalDateTime) {
                return ((LocalDateTime) o).toLocalTime();
            } else {
                String sDate = o.toString();
                try {
                    return LocalTime.parse(sDate);
                } catch (DateTimeParseException e) {
                    logger.warn("Unable to parse {} as LocalTime", o);
                }
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(sDate);
                    return localDateTime.toLocalTime();
                } catch (DateTimeParseException e) {
                    logger.warn("Unable to parse {} as LocalDateTime", o);
                }
            }
            return null;
        });
        registerTransformer(LocalDateTime.class, o -> {
            if (o instanceof LocalDate) {
                return ((LocalDate) o).atStartOfDay();
            } else {
                String sDate = o.toString();
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(sDate);
                    return localDateTime;
                } catch (DateTimeParseException e) {
                    logger.warn("Unable to parse {} as LocalDateTime", o);
                }
                try {
                    LocalDate localDate = LocalDate.parse(sDate);
                    return localDate.atStartOfDay();
                } catch (DateTimeParseException e) {
                    logger.warn("Unable to parse {} as LocalDateTime", o);
                }
            }
            return null;
        });
    }

    /**
     * Retrieve the value as an object
     * @param name the key of the value in the map
     * @return the value.  No conversion is performed.
     */
    public Object getObject(String name) {
        return map.get(name);
    }

    /**
     * Get the value identified by the key, converted to the specified class.
     * If no conversion is possible, returns null;
     * @param name the name of the key
     * @param klass the class to convert it to
     * @param <T> the type of the class
     * @return the converted value or null
     */
    public <T> T get(String name, Class<T> klass) {
        Object value = map.get(name);
        if (value == null) {
            return null;
        }
        if (klass.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        if (transformers.isEmpty()) {
            registerTransformers();
        }
        Function<Object, T> adapter = lookupTransformer(klass);
        if (adapter == null) {
            return null;
        }
        return adapter.apply(value);
    }

    /**
     * Get the value as a string.
     * @param name the key identifying the value
     * @return the value converted to a string
     */
    public String getString(String name) {
        return get(name, String.class);
    }

    /**
     * Get the value as a boolean
     * @param name the key identifying the value
     * @return the value converted to a boolean
     */
    public Boolean getBoolean(String name) {
        return get(name, Boolean.class);
    }

    /**
     * Get the value as an integer
     * @param name the key identifying the value
     * @return the value converted to an integer
     */
    public Integer getInteger(String name) {
        return get(name, Integer.class);
    }

    /**
     * Get the value as a long
     * @param name the key identifying the value
     * @return the value converted to a long
     */
    public Long getLong(String name) {
        return get(name, Long.class);
    }

    /**
     * Get the value as a double
     * @param name the key identifying the value
     * @return the value converted to a double
     */
    public Double getDouble(String name) {
        return get(name, Double.class);
    }

    /**
     * Get the value as a BigInteger
     * @param name the key identifying the value
     * @return the value as a big integer
     */
    public BigInteger getBigInteger(String name) {
        return get(name, BigInteger.class);
    }

    /**
     * Get the value as a BigDecimal
     * @param name the key identifying the value
     * @return the value as a big decimal
     */
    public BigDecimal getBigDecimal(String name) {
        return get(name, BigDecimal.class);
    }

    /**
     * Get the value as a local date
     * @param name the key identifying the value
     * @return the value as a local date
     */
    public LocalDate getLocalDate(String name) {
        return get(name, LocalDate.class);
    }

    /**
     * Get the value as a LocalDateTime
     * @param name the key identifying the value
     * @return the value as a local date time
     */
    public LocalDateTime getLocalDateTime(String name) {
        return get(name, LocalDateTime.class);
    }

    /**
     * Get the value as a LocalTime
     * @param name the key identifying the value
     * @return the value as a LocalTime
     */
    public LocalTime getLocalTime(String name) {
        return get(name, LocalTime.class);
    }

    private static class NumberFormatCatchingClosure<T> implements Function<Object, T> {
        private final Class<T> type;
        private final Function<Object, T> parser;

        private NumberFormatCatchingClosure(Class<T> type, Function<Object, T> parser) {
            this.type = type;
            this.parser = parser;
        }

        @Override
        public T apply(Object o) {
            return apply(o, false);
        }

        private T apply(Object o, boolean suppressLogging) {
            try {
                return parser.apply(o);
            } catch (NumberFormatException exception) {
                if (!suppressLogging) {
                    logger.error("Unbale to parse {} as type: {}", o, type);
                }
            }
            return null;
        }
    }
}
