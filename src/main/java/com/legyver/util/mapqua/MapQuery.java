package com.legyver.util.mapqua;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Query get/set/add abstraction for Maps. The intent is to provide a less
 * painful means of reading/persisting JSON to allow for backward and forward
 * compatibility.
 *
 * The down side of this is that setting an object on a map converts it to a
 * Map, thereby losing all reference to original POJO, so any changes to the
 * POJO after that add are not automatically synced to the map;
 */
public class MapQuery {

	private final List<Ctx> keys;
	private final boolean nullable;

	private MapQuery(List<Ctx> keys, boolean nullable) {
		this.keys = keys;
		this.nullable = nullable;
	}

	protected Optional execute(Map map) {
		return execute(map, keys.size());
	}

	private Optional execute(Map map, int stop) {
		Object val = null;

		int i = 0;
		for (; i < stop; i++) {
			Ctx ctx = keys.get(i);
			if (i == 0) {
				val = map.get(ctx.key);
			} else if (val != null) {
				val = ((Map) val).get(ctx.key);
			}
			if (ctx.filter != null && val instanceof Collection) {
				Collection cVal = (Collection) val;
				Optional option = cVal.stream().filter(ctx.filter).findFirst();
				if (option.isPresent()) {
					val = option.get();
				} else {
					return returnNull();
				}
			}
		}
		if (i == stop && val != null) {
			Ctx ctx = keys.get(i - 1);
			if (!val.getClass().isInstance(ctx.fromClass)) {
				val = adjustForType(val, ctx.toClass);
			}
			return Optional.of(val);
		} else {
			return returnNull();
		}
	}

	private Optional returnNull() {
		Optional result;
		if (nullable) {
			result = Optional.ofNullable(null);
		} else {
			result = Optional.of(null);
		}
		return result;
	}

	//FIXME: encapsulate in a strategy
	private Object adjustForType(Object value, Class toClass) {
		Object result = value;
		if (toClass.equals(int.class) || toClass.equals(Integer.class)) {
			if (value instanceof Double) {
				Double doubleValue = (Double) value;
				result = doubleValue.intValue();
			}
		} else if (toClass.equals(LocalDateTime.class)) {
			result = mapToType(value, new TypeToken<LocalDateTime>() {
			});
		} else if (toClass.equals(LocalDate.class)) {
			result = mapToType(value, new TypeToken<LocalDate>() {
			});
		}
		return result;
	}

	private Object mapToType(Object value, TypeToken typeToken) throws JsonSyntaxException {
		Object result;
		String json = new Gson().toJson(value);
		result = new Gson().fromJson(json, typeToken.getType());
		return result;
	}
	
	private Map getObjectMap(Map map) {
		Map objectMap;
		if (keys.size() == 1) {
			objectMap = map;
		} else {
			objectMap = (Map) execute(map, keys.size() - 1).get();
		}
		return objectMap;
	}

	protected void executeSet(Map map, Object value) {
		Map objectMap = getObjectMap(map);
		objectMap.put(keys.get(keys.size() - 1).key, mapPojo(value));
	}
	
	protected void executeMerge(Map map, Object value) {
		Map objectMap = getObjectMap(map);
		objectMap.merge(keys.get(keys.size() - 1).key, mapPojo(value), new MapMerge());
	}

	protected void executeAdd(Map map, Object value) {
		Optional<Collection> optional = execute(map, keys.size() - 1);
		Collection valueCollection = optional.get();
		value = mapPojo(value);
		valueCollection.add(value);
	}

	private Object mapPojo(Object value) throws JsonSyntaxException {
		if (value != null && !(value.getClass().isPrimitive() || value instanceof String || value instanceof Number || value instanceof Collection)) {//things we can write without further ado
			value = mapToType(value, new TypeToken<Map>() {
			});
		}
		return value;
	}

	public static class ExecutableQueryBuilder<T extends ExecutableQueryBuilder, U> {

		final List<Ctx> keys;
		final boolean nullable;

		private ExecutableQueryBuilder(List<Ctx> keys, boolean nullable) {
			this.keys = keys;
			this.nullable = nullable;
		}

		public Optional<U> execute(Map map) {
			return new MapQuery(keys, nullable).execute(map);
		}
	}

	public static class AbstractQueryBuilder extends ExecutableQueryBuilder<AbstractQueryBuilder, Object> {

		private AbstractQueryBuilder(List<Ctx> keys, boolean nullable) {
			super(keys, nullable);
		}

		private AbstractQueryBuilder(boolean nullable) {
			this(new ArrayList<>(), nullable);
		}

		public SetQueryBuilder set(String name, Object value) {
			keys.add(new Ctx(name, Object.class, Object.class));
			return new SetQueryBuilder(keys, value);
		}

		public MergeQueryBuilder merge(String name, Object value) {
			keys.add(new Ctx(name, Object.class, Object.class));
			return new MergeQueryBuilder(keys, value);
		}

		public AbstractQueryBuilder object(String name) {
			keys.add(new Ctx(name, Object.class, Object.class));
			return this;
		}

		public CollectionQueryBuilder collection(String name) {
			keys.add(new Ctx(name, Collection.class, Collection.class));
			return new CollectionQueryBuilder(keys, nullable);
		}

		public ChildQueryBuilder<LocalDate> localDate(String name) {
			keys.add(new Ctx(name, Map.class, LocalDate.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		public ChildQueryBuilder<LocalDateTime> localDateTime(String name) {
			keys.add(new Ctx(name, Map.class, LocalDateTime.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		public ChildQueryBuilder<String> string(String name) {
			keys.add(new Ctx(name, String.class, String.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		public ChildQueryBuilder<Integer> integer(String name) {
			keys.add(new Ctx(name, Double.class, Integer.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}
		
		public ChildQueryBuilder<Double> floatingPoint(String name) {
			keys.add(new Ctx(name, Double.class, Double.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}
	}

	public static class Query extends AbstractQueryBuilder {

		public Query() {
			super(true);
		}
	}

	public static class ChildQueryBuilder<T> extends ExecutableQueryBuilder<ChildQueryBuilder, T> {
		private ChildQueryBuilder(List<Ctx> keys, boolean nullable) {
			super(keys, nullable);
		}
	}

	public static class CollectionQueryBuilder extends ExecutableQueryBuilder<CollectionQueryBuilder, Collection> {

		private CollectionQueryBuilder(List<Ctx> keys, boolean nullable) {
			super(keys, nullable);
		}

		public AbstractQueryBuilder filter(KeyValueFilter filter) {
			Ctx ctx = super.keys.get(keys.size() - 1);
			ctx.filter = filter;
			return new AbstractQueryBuilder(super.keys, super.nullable);
		}

		public AddQueryBuilder add(Object value) {
			keys.add(new Ctx(null, Object.class, Object.class));
			return new AddQueryBuilder(keys, value);
		}

	}

	public static class SetQueryBuilder extends ExecutableQueryBuilder<SetQueryBuilder, Object> {
		private final Object value;

		private SetQueryBuilder(List<Ctx> keys, Object value) {
			super(keys, true);
			this.value = value;
		}

		@Override
		public Optional execute(Map map) {
			new MapQuery(keys, nullable).executeSet(map, value);
			return Optional.of(value);
		}
	}
	
	public static class MergeQueryBuilder extends ExecutableQueryBuilder<MergeQueryBuilder, Object> {
		private final Object value;

		private MergeQueryBuilder(List<Ctx> keys, Object value) {
			super(keys, true);
			this.value = value;
		}

		@Override
		public Optional execute(Map map) {
			new MapQuery(keys, nullable).executeMerge(map, value);
			return Optional.of(value);
		}
	}

	public static class AddQueryBuilder extends ExecutableQueryBuilder<AddQueryBuilder, Object> {

		private final Object value;

		private AddQueryBuilder(List<Ctx> keys, Object value) {
			super(keys, true);
			this.value = value;
		}

		@Override
		public Optional execute(Map map) {
			new MapQuery(keys, nullable).executeAdd(map, value);
			return Optional.of(value);
		}
	}

	public static class KeyValueFilter implements Predicate<Map> {
		private final String mapKey;
		private final Object value;
		private final Cond cond;

		public KeyValueFilter(String mapKey, Cond cond, Object value) {
			this.mapKey = mapKey;
			this.cond = cond;
			this.value = value;
		}

		@Override
		public boolean test(Map m) {
			return cond.test(m.get(mapKey), value);
		}

		public enum Cond {
			EQ {
				@Override
				public boolean test(Object actualValue, Object matchValue) {
					return actualValue.equals(matchValue);
				}
			};

			abstract public boolean test(Object actualValue, Object matchValue);
		}
	}

	private static class Ctx {
		private final String key;
		private final Class fromClass;
		private final Class toClass;
		private Predicate filter;

		public Ctx(String key, Class fromClass, Class toClass) {
			this.key = key;
			this.fromClass = fromClass;
			this.toClass = toClass;
		}
	}

}
