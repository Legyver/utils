package com.legyver.utils.wrapadapt;

import com.legyver.core.exception.CoreException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Wrapper to execute a typed command with an un-typed value
 * Ex: new WrapAdapter(ctx, value).execute(testCommand);
 * @since 2.0
 */
public class WrapAdapter<T> {
	private final T context;
	private final Object value;

	/**
	 * Construct a new instance of a WrapAdapter with the provided context
	 * @param context Any context you wish to pass to the command
	 * @param value The un-typed value you want to run the command against
	 */
	public WrapAdapter(T context, Object value) {
		this.context = context;
		this.value = value;
	}

	/**
	 *
	 * @param typedCommand the command to execute
	 * @throws CoreException if the TypedCommand throws and exception
	 */
	public void execute(TypedCommand<T> typedCommand) throws CoreException {
		if (value instanceof String) {
			typedCommand.executeString(context, (String) value);
		} else if (value instanceof Integer) {
			typedCommand.executeInteger(context, (Integer) value);
		} else if (value instanceof Double) {
			typedCommand.executeDouble(context, (Double) value);
		} else if (value instanceof Long) {
			typedCommand.executeLong(context, (Long) value);
		} else if (value instanceof BigInteger) {
			typedCommand.executeBigInteger(context, (BigInteger) value);
		} else if (value instanceof BigDecimal) {
			typedCommand.executeBigDecimal(context, (BigDecimal) value);
		} else if (value instanceof Boolean) {
			typedCommand.executeBoolean(context, (Boolean) value);
		} else if (value instanceof LocalDate) {
			typedCommand.executeLocalDate(context, (LocalDate) value);
		} else if (value instanceof LocalDateTime) {
			typedCommand.executeLocalDateTime(context, (LocalDateTime) value);
		} else if (value instanceof LocalTime) {
			typedCommand.executeLocalTime(context, (LocalTime) value);
		} else if (value instanceof ZonedDateTime) {
			typedCommand.executeZonedDateTime(context, (ZonedDateTime) value);
		} else {
			typedCommand.executeObject(context, value);
		}
	}
}
