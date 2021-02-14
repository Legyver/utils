package com.legyver.utils.mapqua;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.legyver.utils.mapqua.MapQuery.KeyValueFilter;
import com.legyver.utils.mapqua.MapQuery.KeyValueFilter.Cond;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
		LinkedTreeMap<String, Map> map = getJsonAsMap();
		Optional name = new MapQuery.Query().string(NAME_FIELD).execute(map);
		assertThat(name.get(), is(NAME_VALUE));

		Optional number = new MapQuery.Query().integer(NUMBER_FIELD).execute(map);
		assertThat("Wrong Integer", number.get(), is(NUMBER_INT_VALUE));

		{
			Optional settings = new MapQuery.Query().object(CONFIG_FIELD)
					.collection(CONFIG_SETTINGS_FIELD).execute(map);
			List settingsList = (List) settings.get();
			assertThat(settingsList.size(), is(1));

			Map setting = (Map) settingsList.get(0);
			assertNotNull(setting);
			assertThat(setting.get(SETTINGS_KEY_KEY), is(SETTINGS_KEY_VALUE));
			assertThat(setting.get("key2"), is("my value2"));
		}

		{
			Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
					.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
					.execute(map);
			Map settingMap = (Map) setting.get();
			assertNotNull(settingMap);
			assertThat(settingMap.get(SETTINGS_KEY_KEY), is(SETTINGS_KEY_VALUE));
		}

		{
			Optional settingKey = new MapQuery.Query().object(CONFIG_FIELD)
					.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
					.string(SETTINGS_KEY_KEY)
					.execute(map);
			String settingValue = (String) settingKey.get();
			assertNotNull(settingValue);
			assertThat(settingValue, is(SETTINGS_KEY_VALUE));
		}

		Optional<LocalDateTime> ldtOption = new MapQuery.Query().localDateTime(LOCAL_DATE_TIME_FIELD).execute(map);
		assertThat(ldtOption.isPresent(), is(true));
		LocalDateTime ldt = ldtOption.get();
		assertThat(ldt.getYear(), is(2018));
		assertThat(ldt.getMonth(), is(Month.JULY));
		assertThat(ldt.getDayOfMonth(), is(14));
		assertThat(ldt.getHour(), is(17));
		assertThat(ldt.getMinute(), is(10));
		assertThat(ldt.getSecond(), is(57));

		Optional<LocalDate> ldOption = new MapQuery.Query().localDate(LOCAL_DATE_FIELD).execute(map);
		assertThat(ldOption.isPresent(), is(true));
		LocalDate ld = ldOption.get();
		assertThat(ld.getYear(), is(2018));
		assertThat(ld.getMonth(), is(Month.JULY));
		assertThat(ld.getDayOfMonth(), is(14));
	}

	@Test
	public void setJson() throws Exception {
		LinkedTreeMap<String, Map> map = getJsonAsMap();
		new MapQuery.Query().set(NAME_FIELD, "new name").execute(map);
		Optional newName = new MapQuery.Query().string(NAME_FIELD).execute(map);
		assertThat(newName.get(), is("new name"));

		new MapQuery.Query().set(NUMBER_FIELD, 623).execute(map);
		Optional number = new MapQuery.Query().integer(NUMBER_FIELD).execute(map);
		assertThat("Wrong Integer", number.get(), is(623));

		new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.set(SETTINGS_KEY_KEY, "new Key")
				.execute(map);
		Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.execute(map);
		assertFalse("Old value still present", setting.isPresent());
		setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, "new Key"))
				.execute(map);
		Map settingMap = (Map) setting.get();
		assertNotNull(settingMap);
		assertThat(settingMap.get(SETTINGS_KEY_KEY), is("new Key"));

		LocalDate nowDate = LocalDate.now();
		new MapQuery.Query().set(LOCAL_DATE_FIELD, nowDate).execute(map);
		Optional<LocalDate> ldOption = new MapQuery.Query().localDate(LOCAL_DATE_FIELD).execute(map);
		assertThat(ldOption.isPresent(), is(true));
		LocalDate ld = ldOption.get();
		assertThat(ld.getYear(), is(nowDate.getYear()));
		assertThat(ld.getMonth(), is(nowDate.getMonth()));
		assertThat(ld.getDayOfMonth(), is(nowDate.getDayOfMonth()));

		LocalDateTime nowDateTime = LocalDateTime.now();
		new MapQuery.Query().set(LOCAL_DATE_TIME_FIELD, nowDateTime).execute(map);
		Optional<LocalDateTime> ldtOption = new MapQuery.Query().localDateTime(LOCAL_DATE_TIME_FIELD).execute(map);
		assertThat(ldtOption.isPresent(), is(true));
		LocalDateTime ldt = ldtOption.get();
		assertThat(ldt.getYear(), is(nowDateTime.getYear()));
		assertThat(ldt.getMonth(), is(nowDateTime.getMonth()));
		assertThat(ldt.getDayOfMonth(), is(nowDateTime.getDayOfMonth()));
		assertThat(ldt.getHour(), is(nowDateTime.getHour()));
		assertThat(ldt.getMinute(), is(nowDateTime.getMinute()));
		assertThat(ldt.getSecond(), is(nowDateTime.getSecond()));
	}
	
	@Test
	public void mergeJson() throws Exception {
		LinkedTreeMap<String, Map> map = getJsonAsMap();
		new MapQuery.Query().merge("new attr", "new name").execute(map);
		Optional newName = new MapQuery.Query().string("new attr").execute(map);
		assertThat(newName.get(), is("new name"));
		Optional name = new MapQuery.Query().string(NAME_FIELD).execute(map);
		assertThat(name.get(), is(NAME_VALUE));
		
		new MapQuery.Query().merge("new num", 623).execute(map);
		Optional newNumber = new MapQuery.Query().integer("new num").execute(map);
		assertThat("Wrong Integer", newNumber.get(), is(623));
		Optional number = new MapQuery.Query().integer(NUMBER_FIELD).execute(map);
		assertThat("Wrong Integer", number.get(), is(NUMBER_INT_VALUE));
		
		new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.merge("new settings key", "new Key")
				.execute(map);
		Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection("new settings key").filter(new KeyValueFilter(SETTINGS_KEY_KEY, Cond.EQ, SETTINGS_KEY_VALUE))
				.execute(map);
		assertFalse("Old value still present", setting.isPresent());
		setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD).filter(new KeyValueFilter("new settings key", Cond.EQ, "new Key"))
				.execute(map);
		Map settingMap = (Map) setting.get();
		assertNotNull(settingMap);
		assertThat(settingMap.get(SETTINGS_KEY_KEY), is(SETTINGS_KEY_VALUE));
		assertThat(settingMap.get("new settings key"), is("new Key"));
		
		LocalDate nowDate = LocalDate.now();
		new MapQuery.Query().merge(LOCAL_DATE_FIELD, nowDate).execute(map);
		Optional<LocalDate> ldOption = new MapQuery.Query().localDate(LOCAL_DATE_FIELD).execute(map);
		assertThat(ldOption.isPresent(), is(true));
		LocalDate ld = ldOption.get();
		assertThat(ld.getYear(), is(nowDate.getYear()));
		assertThat(ld.getMonth(), is(nowDate.getMonth()));
		assertThat(ld.getDayOfMonth(), is(nowDate.getDayOfMonth()));

		LocalDateTime nowDateTime = LocalDateTime.now();
		new MapQuery.Query().merge(LOCAL_DATE_TIME_FIELD, nowDateTime).execute(map);
		Optional<LocalDateTime> ldtOption = new MapQuery.Query().localDateTime(LOCAL_DATE_TIME_FIELD).execute(map);
		assertThat(ldtOption.isPresent(), is(true));
		LocalDateTime ldt = ldtOption.get();
		assertThat(ldt.getYear(), is(nowDateTime.getYear()));
		assertThat(ldt.getMonth(), is(nowDateTime.getMonth()));
		assertThat(ldt.getDayOfMonth(), is(nowDateTime.getDayOfMonth()));
		assertThat(ldt.getHour(), is(nowDateTime.getHour()));
		assertThat(ldt.getMinute(), is(nowDateTime.getMinute()));
		assertThat(ldt.getSecond(), is(nowDateTime.getSecond()));
	}

	@Test
	public void addJson() throws Exception {
		LinkedTreeMap<String, Map> map = getJsonAsMap();
		new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD)
				.add(new Setting("new settings key", "new value"))
				.execute(map);
		Optional setting = new MapQuery.Query().object(CONFIG_FIELD)
				.collection(CONFIG_SETTINGS_FIELD)
				.execute(map);
		Collection settings = (Collection) setting.get();
		assertNotNull(settings);
		assertThat(settings.size(), is(2));
		Optional<Map> newFound = settings.stream().filter(m -> ((Map) m).get(SETTINGS_KEY_KEY).equals("new settings key")).findFirst();
		assertTrue("Updated value not found in collection", newFound.isPresent());
		Map foundValue = newFound.get();
		assertThat(foundValue.get(SETTINGS_KEY_KEY), is("new settings key"));
	}

	private class Setting {

		private String key1;
		private String key2;

		public Setting(String key, String value) {
			this.key1 = key;
			this.key2 = value;
		}
	}

	private LinkedTreeMap<String, Map> getJsonAsMap() throws JsonSyntaxException, IOException {
		String json = IOUtils.toString(getClass().getResourceAsStream("SimpleTestModel.json"), "UTF-8");
		Type type = new TypeToken<Map>() {
		}.getType();
		LinkedTreeMap<String, Map> map = new Gson().fromJson(json, type);
		return map;
	}
}
