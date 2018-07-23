package com.legyver.util.mapqua.mapbacked;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MapBackedLocalDateTest {
	@Test
	public void addValue() {
		Map map = new LinkedHashMap();
		MapBackedLocalDate mb = new MapBackedLocalDate(map, "field");
		assertThat(mb.get(), is(LocalDate.now()));
	
		LocalDate ld2 = LocalDate.now().minusDays(1).minusMonths(1).minusYears(1);
		mb.set(ld2);
		assertThat(mb.get(), is(ld2));
	}
	
	@Test
	public void changeValue() {
		Map map = new LinkedHashMap();
		MapBackedLocalDate mb = new MapBackedLocalDate(map, "field");
		LocalDate ld1 = LocalDate.now();
		LocalDate ld2 = LocalDate.now().minusDays(1).minusMonths(1).minusYears(1);
		mb.set(ld1);
		assertThat(mb.get(), is(ld1));
		mb.set(ld2);
		assertThat(mb.get(), is(ld2));
	}
	
	@Test
	public void entityMap() {
		Entity entity = new Entity();
		LocalDate ld1 = LocalDate.now();
		LocalDate ld2 = LocalDate.now().minusDays(1).minusMonths(1).minusYears(1);
		entity.setLocalDate1(ld1);
		entity.setLocalDate2(ld2);
		assertThat(entity.getLocalDate1(), is(ld1));
		assertThat(entity.getLocalDate2(), is(ld2));
	}

	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedLocalDate mb1 =  new MapBackedLocalDate(sourceMap, "field1");
		private MapBackedLocalDate mb2 =  new MapBackedLocalDate(sourceMap, "field2");
		
		LocalDate getLocalDate1() {
			return mb1.get();
		}
		
		LocalDate getLocalDate2() {
			return mb2.get();
		}
		
		public void setLocalDate1(LocalDate localDate) {
			mb1.set(localDate);
		}
		
		public void setLocalDate2(LocalDate localDate) {
			mb2.set(localDate);
		}
	}
}
