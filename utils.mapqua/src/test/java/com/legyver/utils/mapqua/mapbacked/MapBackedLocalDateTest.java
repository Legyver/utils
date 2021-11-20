package com.legyver.utils.mapqua.mapbacked;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MapBackedLocalDateTest {
	@Test
	public void addValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedLocalDate mb = new MapBackedLocalDate(map, "field");
		assertThat(mb.get()).isEqualTo(LocalDate.now());
	
		LocalDate ld2 = LocalDate.now().minusDays(1).minusMonths(1).minusYears(1);
		mb.set(ld2);
		assertThat(mb.get()).isEqualTo(ld2);
	}
	
	@Test
	public void changeValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedLocalDate mb = new MapBackedLocalDate(map, "field");
		LocalDate ld1 = LocalDate.now();
		LocalDate ld2 = LocalDate.now().minusDays(1).minusMonths(1).minusYears(1);
		mb.set(ld1);
		assertThat(mb.get()).isEqualTo(ld1);
		mb.set(ld2);
		assertThat(mb.get()).isEqualTo(ld2);
	}
	
	@Test
	public void entityMap() throws Exception {
		Entity entity = new Entity();
		LocalDate ld1 = LocalDate.now();
		LocalDate ld2 = LocalDate.now().minusDays(1).minusMonths(1).minusYears(1);
		entity.setLocalDate1(ld1);
		entity.setLocalDate2(ld2);
		assertThat(entity.getLocalDate1()).isEqualTo(ld1);
		assertThat(entity.getLocalDate2()).isEqualTo(ld2);
	}

	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedLocalDate mb1 =  new MapBackedLocalDate(sourceMap, "field1");
		private MapBackedLocalDate mb2 =  new MapBackedLocalDate(sourceMap, "field2");
		
		LocalDate getLocalDate1() throws CoreException {
			return mb1.get();
		}
		
		LocalDate getLocalDate2() throws CoreException {
			return mb2.get();
		}
		
		public void setLocalDate1(LocalDate localDate) throws CoreException {
			mb1.set(localDate);
		}
		
		public void setLocalDate2(LocalDate localDate) throws CoreException {
			mb2.set(localDate);
		}
	}
}
