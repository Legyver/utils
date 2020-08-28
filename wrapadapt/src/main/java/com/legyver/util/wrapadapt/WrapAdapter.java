package com.legyver.util.wrapadapt;

import com.legyver.core.exception.CoreException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class WrapAdapter {
	private final String nodeName;
	private final Object value;

	public WrapAdapter(String nodeName, Object value) {
		this.nodeName = nodeName;
		this.value = value;
	}

	public void execute(TypedCommand typedCommand) throws CoreException {
		if (value instanceof String) {
			typedCommand.executeString(nodeName, (String) value);
		} else if (value instanceof Integer) {
			typedCommand.executeInteger(nodeName, (Integer) value);
		} else if (value instanceof Double) {
			typedCommand.executeDouble(nodeName, (Double) value);
		} else if (value instanceof Long) {
			typedCommand.executeLong(nodeName, (Long) value);
		} else if (value instanceof BigInteger) {
			typedCommand.executeBigInteger(nodeName, (BigInteger) value);
		} else if (value instanceof BigDecimal) {
			typedCommand.executeBigDecimal(nodeName, (BigDecimal) value);
		} else if (value instanceof Boolean) {
			typedCommand.executeBoolean(nodeName, (Boolean) value);
		} else if (value instanceof LocalDate) {
			typedCommand.executeLocalDate(nodeName, (LocalDate) value);
		} else if (value instanceof LocalDateTime) {
			typedCommand.executeLocalDateTime(nodeName, (LocalDateTime) value);
		} else if (value instanceof LocalTime) {
			typedCommand.executeLocalTime(nodeName, (LocalTime) value);
		} else if (value instanceof ZonedDateTime) {
			typedCommand.executeZonedDateTime(nodeName, (ZonedDateTime) value);
		} else {
			typedCommand.executeObject(nodeName, value);
		}
	}
}
