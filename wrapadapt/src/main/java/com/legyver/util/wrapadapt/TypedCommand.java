package com.legyver.util.wrapadapt;

import com.legyver.core.exception.CoreException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Usage: Override the method you want to implement
 */
public interface TypedCommand<T> {
	default void executeObject(T ctx, Object value) throws CoreException {
		//template
	}

	default void executeString(T ctx, String value) throws CoreException {
		//template
	}

	default void executeBoolean(T ctx, Boolean value) throws CoreException {
		//template
	}

	default void executeInteger(T ctx, Integer value) throws CoreException {
		//template
	}

	default void executeLong(T ctx, Long value) throws CoreException {
		//template
	}

	default void executeDouble(T ctx, Double value) throws CoreException {
		//template
	}

	default void executeBigInteger(T ctx, BigInteger value) throws CoreException {
		//template
	}

	default void executeBigDecimal(T ctx, BigDecimal value) throws CoreException {
		//template
	}

	default void executeLocalDate(T ctx, LocalDate value) throws CoreException {
		//template
	}

	default void executeLocalDateTime(T ctx, LocalDateTime value) throws CoreException {
		//template
	}

	default void executeLocalTime(T ctx, LocalTime value) throws CoreException {
		//template
	}

	default void executeZonedDateTime(T ctx, ZonedDateTime value) throws CoreException {
		//template
	}
}
