package com.legyver.utils.mapqua.mapbacked;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import com.legyver.core.exception.CoreException;
import org.exparity.hamcrest.date.LocalDateTimeMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MapBackedLocalDateTimeTest {

	@Test
	public void addValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedLocalDateTime mb = new MapBackedLocalDateTime(map, "field");
		assertThat(mb.get(), LocalDateTimeMatchers.within(1, ChronoUnit.SECONDS, LocalDateTime.now()));
	
		LocalDateTime ld2 = LocalDateTime.now().minusDays(1).minusMonths(1).minusYears(1);
		mb.set(ld2);
		assertThat(mb.get(), is(ld2));
	}
	
	@Test
	public void changeValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedLocalDateTime mb = new MapBackedLocalDateTime(map, "field");
		LocalDateTime ld1 = LocalDateTime.now();
		LocalDateTime ld2 = LocalDateTime.now().minusDays(1).minusMonths(1).minusYears(1);
		mb.set(ld1);
		assertThat(mb.get(), is(ld1));
		mb.set(ld2);
		assertThat(mb.get(), is(ld2));
	}
	
	@Test
	public void entityMap() throws Exception {
		Entity entity = new Entity();
		LocalDateTime ld1 = LocalDateTime.now();
		LocalDateTime ld2 = LocalDateTime.now().minusDays(1).minusMonths(1).minusYears(1);
		entity.setLocalDateTime1(ld1);
		entity.setLocalDateTime2(ld2);
		assertThat(entity.getLocalDateTime1(), is(ld1));
		assertThat(entity.getLocalDateTime2(), is(ld2));
	}

	

	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedLocalDateTime mb1 =  new MapBackedLocalDateTime(sourceMap, "field1");
		private MapBackedLocalDateTime mb2 =  new MapBackedLocalDateTime(sourceMap, "field2");
		
		LocalDateTime getLocalDateTime1() throws CoreException {
			return mb1.get();
		}
		
		LocalDateTime getLocalDateTime2() throws CoreException {
			return mb2.get();
		}
		
		public void setLocalDateTime1(LocalDateTime localDate) throws CoreException {
			mb1.set(localDate);
		}
		
		public void setLocalDateTime2(LocalDateTime localDate) throws CoreException {
			mb2.set(localDate);
		}
	}
}
