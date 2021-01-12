package com.legyver.utils.wrapadapt;

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
	/**
	 * Execute the command with the value as a generic Object object.
	 * Used if no other typed methods match.
	 *
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The un-typed Object value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeObject(T ctx, Object value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a String object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed String value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeString(T ctx, String value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a Boolean object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed Boolean value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeBoolean(T ctx, Boolean value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a Integer object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed Integer value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeInteger(T ctx, Integer value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a Long object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed Long value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeLong(T ctx, Long value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a Double object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed Double value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeDouble(T ctx, Double value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a BigInteger object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed BigInteger value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeBigInteger(T ctx, BigInteger value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a BigDecimal object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed BigDecimal value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeBigDecimal(T ctx, BigDecimal value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a LocalDate object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed LocalDate value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeLocalDate(T ctx, LocalDate value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a LocalDateTime object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed LocalDateTime value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeLocalDateTime(T ctx, LocalDateTime value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a LocalTime object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed LocalTime value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeLocalTime(T ctx, LocalTime value) throws CoreException {
		//template
	}

	/**
	 * Execute the command with the value as a ZonedDateTime object
	 * @param ctx Any context you wish to pass to the TypedCommand
	 * @param value The [now] typed ZoneDateTime value
	 * @throws CoreException if the overriding method throws a CoreException
	 */
	default void executeZonedDateTime(T ctx, ZonedDateTime value) throws CoreException {
		//template
	}
}
