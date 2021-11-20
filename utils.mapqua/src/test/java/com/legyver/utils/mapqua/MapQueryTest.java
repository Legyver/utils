package com.legyver.utils.mapqua;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.jackiso.JacksonObjectMapper;
import com.legyver.utils.mapqua.MapQuery.KeyValueFilter;
import com.legyver.utils.mapqua.MapQuery.KeyValueFilter.Cond;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MapQueryTest {

	private static final String CONFIG_FIELD = "config";
	private static final String CONFIG_SETTINGS_FIELD = "settings";
	private static final String SETTINGS_KEY_KEY = "key1";
	private static final String SETTINGS_KEY_VALUE = "my value1";
	private static final String NAME_FIELD = "name";
	private static final String NAME_VALUE = "my name";
	private static final String NUMBER_FIELD = "number";
	private static final int NUMBER_INT_VALUE = 5;
	private static final String LOCAL_DATE_TIME_FIELD = "localDateTime";
	private static final String LOCAL_DATE_FIELD = "localDate";

	@Test
	public void queryJson() throws Exception {
		Map<String, Object> map = getJsonAsMap();
		Optional name = new MapQuery.Query().string(NAME_FIELD).execute(map);
		assertThat(name.get()).isEqualTo(NAME_VALUE);

		Optional number = new MapQuery.Query().integer(NUMBER_FIELD).execute(map);
		assertThat(number.get()).isEqualTo(NUMBER_INT_VALUE);

		{
			Optional settings = new MapQuery.Query().object(CONFIG_FIELD)
					.collection(CONFIG_SETTINGS_FIELD).execute(map);
			List settingsList = (List) settings.get();
			assertThat(settingsList.size()).isEqualTo(1);

			Map setting = (Map) settingsList.get(0);
			assertThat(setting).isNotNull();
			assertThat(setting).isNotEmpty();
			assertThat(setting.get(SETTINGS_KEY_KEY)).isEqualTo(SETTINGS_KEY_VALUE);
			assertThat(setting.get("key2")).isEqualTo("my value2");
		}

		{
			Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
					.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
					.execute(map);
			Map settingMap = (Map) setting.get();
			assertNotNull(settingMap);
			assertThat(settingMap.get(SETTINGS_KEY_KEY)).isEqualTo(SETTINGS_KEY_VALUE);
		}

		{
			Optional settingKey = new MapQuery.Query().object(CONFIG_FIELD)
					.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
					.string(SETTINGS_KEY_KEY)
					.execute(map);
			String settingValue = (String) settingKey.get();
			assertNotNull(settingValue);
			assertThat(settingValue).isEqualTo(SETTINGS_KEY_VALUE);
		}

		Optional<LocalDateTime> ldtOption = new MapQuery.Query().localDateTime(LOCAL_DATE_TIME_FIELD).execute(map);
		assertThat(ldtOption.isPresent()).isEqualTo(true);
		LocalDateTime ldt = ldtOption.get();
		assertThat(ldt.getYear()).isEqualTo(2018);
		assertThat(ldt.getMonth()).isEqualTo(Month.JULY);
		assertThat(ldt.getDayOfMonth()).isEqualTo(14);
		assertThat(ldt.getHour()).isEqualTo(17);
		assertThat(ldt.getMinute()).isEqualTo(10);
		assertThat(ldt.getSecond()).isEqualTo(57);

		Optional<LocalDate> ldOption = new MapQuery.Query().localDate(LOCAL_DATE_FIELD).execute(map);
		assertThat(ldOption.isPresent()).isEqualTo(true);
		LocalDate ld = ldOption.get();
		assertThat(ld.getYear()).isEqualTo(2018);
		assertThat(ld.getMonth()).isEqualTo(Month.JULY);
		assertThat(ld.getDayOfMonth()).isEqualTo(14);
	}

	@Test
	public void setJson() throws Exception {
		Map<String, Object> map = getJsonAsMap();
		new MapQuery.Query().set(NAME_FIELD, "new name").execute(map);
		Optional newName = new MapQuery.Query().string(NAME_FIELD).execute(map);
		assertThat(newName.get()).isEqualTo("new name");

		new MapQuery.Query().set(NUMBER_FIELD, 623).execute(map);
		Optional number = new MapQuery.Query().integer(NUMBER_FIELD).execute(map);
		assertThat(number.get()).isEqualTo(623);

		new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.set(SETTINGS_KEY_KEY, "new Key")
				.execute(map);
		Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.execute(map);
		assertThat(setting.isPresent()).isFalse();
		setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, "new Key"))
				.execute(map);
		Map settingMap = (Map) setting.get();
		assertThat(settingMap).isNotNull();
		assertThat(settingMap).isNotEmpty();
		assertThat(settingMap.get(SETTINGS_KEY_KEY)).isEqualTo("new Key");

		LocalDate nowDate = LocalDate.now();
		new MapQuery.Query().set(LOCAL_DATE_FIELD, nowDate).execute(map);
		Optional<LocalDate> ldOption = new MapQuery.Query().localDate(LOCAL_DATE_FIELD).execute(map);
		assertThat(ldOption.isPresent()).isEqualTo(true);
		LocalDate ld = ldOption.get();
		assertThat(ld.getYear()).isEqualTo(nowDate.getYear());
		assertThat(ld.getMonth()).isEqualTo(nowDate.getMonth());
		assertThat(ld.getDayOfMonth()).isEqualTo(nowDate.getDayOfMonth());

		LocalDateTime nowDateTime = LocalDateTime.now();
		new MapQuery.Query().set(LOCAL_DATE_TIME_FIELD, nowDateTime).execute(map);
		Optional<LocalDateTime> ldtOption = new MapQuery.Query().localDateTime(LOCAL_DATE_TIME_FIELD).execute(map);
		assertThat(ldtOption.isPresent()).isEqualTo(true);
		LocalDateTime ldt = ldtOption.get();
		assertThat(ldt.getYear()).isEqualTo(nowDateTime.getYear());
		assertThat(ldt.getMonth()).isEqualTo(nowDateTime.getMonth());
		assertThat(ldt.getDayOfMonth()).isEqualTo(nowDateTime.getDayOfMonth());
		assertThat(ldt.getHour()).isEqualTo(nowDateTime.getHour());
		assertThat(ldt.getMinute()).isEqualTo(nowDateTime.getMinute());
		assertThat(ldt.getSecond()).isEqualTo(nowDateTime.getSecond());
	}
	
	@Test
	public void mergeJson() throws Exception {
		Map<String, Object> map = getJsonAsMap();
		new MapQuery.Query().merge("new attr", "new name").execute(map);
		Optional newName = new MapQuery.Query().string("new attr").execute(map);
		assertThat(newName.get()).isEqualTo("new name");
		Optional name = new MapQuery.Query().string(NAME_FIELD).execute(map);
		assertThat(name.get()).isEqualTo(NAME_VALUE);
		
		new MapQuery.Query().merge("new num", 623).execute(map);
		Optional newNumber = new MapQuery.Query().integer("new num").execute(map);
		assertThat(newNumber.get()).isEqualTo(623);
		Optional number = new MapQuery.Query().integer(NUMBER_FIELD).execute(map);
		assertThat(number.get()).isEqualTo(NUMBER_INT_VALUE);
		
		new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.merge("new settings key", "new Key")
				.execute(map);
		Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection("new settings key").filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.execute(map);
		assertThat(setting.isPresent()).isFalse();
		setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter("new settings key", Cond.EQ, "new Key"))
				.execute(map);
		Map settingMap = (Map) setting.get();
		assertThat(settingMap).isNotNull();
		assertThat(settingMap).isNotEmpty();
		assertThat(settingMap.get(SETTINGS_KEY_KEY)).isEqualTo(SETTINGS_KEY_VALUE);
		assertThat(settingMap.get("new settings key")).isEqualTo("new Key");
		
		LocalDate nowDate = LocalDate.now();
		new MapQuery.Query().merge(LOCAL_DATE_FIELD, nowDate).execute(map);
		Optional<LocalDate> ldOption = new MapQuery.Query().localDate(LOCAL_DATE_FIELD).execute(map);
		assertThat(ldOption.isPresent()).isEqualTo(true);
		LocalDate ld = ldOption.get();
		assertThat(ld.getYear()).isEqualTo(nowDate.getYear());
		assertThat(ld.getMonth()).isEqualTo(nowDate.getMonth());
		assertThat(ld.getDayOfMonth()).isEqualTo(nowDate.getDayOfMonth());

		LocalDateTime nowDateTime = LocalDateTime.now();
		new MapQuery.Query().merge(LOCAL_DATE_TIME_FIELD, nowDateTime).execute(map);
		Optional<LocalDateTime> ldtOption = new MapQuery.Query().localDateTime(LOCAL_DATE_TIME_FIELD).execute(map);
		assertThat(ldtOption.isPresent()).isEqualTo(true);
		LocalDateTime ldt = ldtOption.get();
		assertThat(ldt.getYear()).isEqualTo(nowDateTime.getYear());
		assertThat(ldt.getMonth()).isEqualTo(nowDateTime.getMonth());
		assertThat(ldt.getDayOfMonth()).isEqualTo(nowDateTime.getDayOfMonth());
		assertThat(ldt.getHour()).isEqualTo(nowDateTime.getHour());
		assertThat(ldt.getMinute()).isEqualTo(nowDateTime.getMinute());
		assertThat(ldt.getSecond()).isEqualTo(nowDateTime.getSecond());
	}

	@Test
	public void addJson() throws Exception {
		Map<String, Object> map = getJsonAsMap();
		new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD)
				.add(new Setting("new settings key", "new value"))
				.execute(map);
		Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD)
				.execute(map);
		Collection settings = (Collection) setting.get();
		assertThat(settings).isNotNull();
		assertThat(settings.size()).isEqualTo(2);
		Optional<Map> newFound = settings.stream().filter(m -> ((Map) m).get(SETTINGS_KEY_KEY).equals("new settings key")).findFirst();
		assertThat(newFound.isPresent()).isTrue();
		Map foundValue = newFound.get();
		assertThat(foundValue.get(SETTINGS_KEY_KEY)).isEqualTo("new settings key");
	}

	private class Setting {

		public String key1;
		public String key2;

		public Setting(String key, String value) {
			this.key1 = key;
			this.key2 = value;
		}
	}

	private Map<String, Object> getJsonAsMap() throws IOException, CoreException {
		String json = IOUtils.toString(getClass().getResourceAsStream("SimpleTestModel.json"), StandardCharsets.UTF_8);
		return JacksonObjectMapper.INSTANCE.readValue(json, Map.class);
	}
}
