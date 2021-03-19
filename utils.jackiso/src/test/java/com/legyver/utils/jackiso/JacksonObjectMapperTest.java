package com.legyver.utils.jackiso;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonObjectMapperTest {
	private static final String LOCAL_DATE_TIME = "2018-07-15T17:10:57";
	private static final String LOCAL_DATE = "2018-07-15";
	private static final String LOCAL_TIME = "17:10:57";

	@Test
	public void localDateTime() throws Exception {
		LocalDateTime localDateTime = LocalDateTime.of(
				2018, 7, 15, 17, 10, 57, 0
		);
		String converted = JacksonObjectMapper.INSTANCE.writeValueAsString(localDateTime);
		assertThat(converted).isEqualTo(LOCAL_DATE_TIME);

		LocalDateTime back = JacksonObjectMapper.INSTANCE.readValue(converted, LocalDateTime.class);
		assertThat(back).isEqualTo(localDateTime);
	}

	@Test
	public void localDate() throws Exception {
		LocalDate localDate = LocalDate.of(
				2018, 7, 15
		);
		String converted = JacksonObjectMapper.INSTANCE.writeValueAsString(localDate);
		assertThat(converted).isEqualTo(LOCAL_DATE);

		LocalDate back = JacksonObjectMapper.INSTANCE.readValue(converted, LocalDate.class);
		assertThat(back).isEqualTo(localDate);
	}

	@Test
	public void localTime() throws Exception {
		LocalTime localTime = LocalTime.of(
				17, 10, 57
		);
		String converted = JacksonObjectMapper.INSTANCE.writeValueAsString(localTime);
		assertThat(converted).isEqualTo(LOCAL_TIME);

		LocalTime back = JacksonObjectMapper.INSTANCE.readValue(converted, LocalTime.class);
		assertThat(back).isEqualTo(localTime);
	}

}
