package com.legyver.utils.jackiso;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.legyver.core.exception.CoreException;
import com.legyver.utils.adaptex.ExceptionToCoreExceptionFunctionDecorator;
import com.legyver.utils.adaptex.ExceptionToCoreExceptionFunctionDecorator.ThrowingFunction;

import java.time.temporal.Temporal;

/**
 * Singleton for re-use of ObjectMapper per Jackson documentation.
 * By default supports the java.time API
 */
public enum JacksonObjectMapper {
	/**
	 * Singleton instance
	 */
	INSTANCE;
	private final ObjectMapper objectMapper;

	JacksonObjectMapper() {
		objectMapper = JsonMapper.builder()
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.addModule(new JavaTimeModule())
				.build();
	}

	/**
	 * Get the ObjectMapper
	 * @return the ObjectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}


	/**
	 * Register a Jackson Module
	 * @param module the module to register
	 * @return the ObjectMapper
	 */
	public ObjectMapper registerModule(Module module) {
		return objectMapper.registerModule(module);
	}

	/**
	 * Read a String into the specified POJO
	 * @param content the content to read
	 * @param valueType the class of the POJO
	 * @param <T> the type of the POJO
	 * @return the POJO
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	public <T> T readValue(String content, Class<T> valueType) throws CoreException {
		if (Temporal.class.isAssignableFrom(valueType)) {
			if (!content.startsWith("\"")) {
				content = '"' + content + '"';
			}
		}
		try {
			return objectMapper.readValue(content, valueType);
		} catch (JsonProcessingException e) {
			throw new CoreException("Error processsing json: " + e.getMessage(), e);
		}
	}

	/**
	 * Write a POJO to a String
	 * @param value the value to write
	 * @return the String value
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	public String writeValueAsString(Object value) throws CoreException {
		return writeValueAsString(value, new ExceptionToCoreExceptionFunctionDecorator((o) -> {
			return objectMapper.writeValueAsString(value);
		}));
	}

	/**
	 * Write a POJO to a String with PrettyPrint on
	 * @param value the value to write
	 * @return the pretty print string
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	public String writeValueAsStringWithPrettyPrint(Object value) throws CoreException {
		return writeValueAsString(value, new ExceptionToCoreExceptionFunctionDecorator((o) -> {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		}));
	}

	private String writeValueAsString(Object value, ExceptionToCoreExceptionFunctionDecorator<Object, String> action) throws CoreException {
		String result = action.apply(value);
		if (value instanceof Temporal) {
			//remove the extra quotes that for some reason are included
			if (result.startsWith("\"")) {
				int lastQuote = result.lastIndexOf('"');
				result = result.substring(1, lastQuote);
			}
		}
		return result;
	}

}
