package com.legyver.utils.wrapadapt;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WrapAdapterTest {

	private void runTest(Object value, TestCommand testCommand) throws CoreException {
		String testName = value == null ? "null" : value.getClass().getSimpleName();
		new WrapAdapter(testName, value).execute(testCommand);
		assertTrue(testCommand.visited);
	}

	@Test
	public void testNull() throws Exception {
		runTest(null, new TestCommand() {
			@Override
			public void executeObject(Object ctx, Object value) {
				assertEquals( "null", ctx);
				assertNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testObject() throws Exception{
		runTest(new CustomObject(), new TestCommand() {
			@Override
			public void executeObject(Object ctx, Object value) {
				assertEquals(CustomObject.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testString() throws Exception {
		runTest("string value", new TestCommand() {
			@Override
			public void executeString(Object ctx, String value) {
				assertEquals(String.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testBoolean() throws Exception {
		runTest(Boolean.TRUE, new TestCommand() {
			@Override
			public void executeBoolean(Object ctx, Boolean value) {
				assertEquals(Boolean.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testDouble() throws Exception {
		runTest(Double.MAX_VALUE, new TestCommand() {
			@Override
			public void executeDouble(Object ctx, Double value) {
				assertEquals(Double.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testLong() throws Exception {
		runTest(Long.MAX_VALUE, new TestCommand() {
			@Override
			public void executeLong(Object ctx, Long value) {
				assertEquals(Long.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testInteger() throws Exception {
		runTest(Integer.MAX_VALUE, new TestCommand() {
			@Override
			public void executeInteger(Object ctx, Integer value) {
				assertEquals(Integer.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testBigInteger() throws Exception {
		runTest(BigInteger.ONE, new TestCommand() {
			@Override
			public void executeBigInteger(Object ctx, BigInteger value) {
				assertEquals(BigInteger.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testLocalDate() throws Exception {
		runTest(LocalDate.now(), new TestCommand() {
			@Override
			public void executeLocalDate(Object ctx, LocalDate value) {
				assertEquals(LocalDate.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testLocalDateTime() throws Exception {
		runTest(LocalDateTime.now(), new TestCommand() {
			@Override
			public void executeLocalDateTime(Object ctx, LocalDateTime value) {
				assertEquals(LocalDateTime.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testLocalTime() throws Exception {
		runTest(LocalTime.now(), new TestCommand() {
			@Override
			public void executeLocalTime(Object ctx, LocalTime value) {
				assertEquals(LocalTime.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testZonedDateTime() throws Exception {
		runTest(ZonedDateTime.now(), new TestCommand() {
			@Override
			public void executeZonedDateTime(Object ctx, ZonedDateTime value) {
				assertEquals(ZonedDateTime.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}

	@Test
	public void testBigDecimal() throws Exception {
		runTest(BigDecimal.ONE, new TestCommand() {
			@Override
			public void executeBigDecimal(Object ctx, BigDecimal value) {
				assertEquals(BigDecimal.class.getSimpleName(), ctx);
				assertNotNull(value);
				visited = true;
			}
		});
	}



	private class TestCommand implements TypedCommand {
		boolean visited = false;
	}

	private static class CustomObject {
	}
}
