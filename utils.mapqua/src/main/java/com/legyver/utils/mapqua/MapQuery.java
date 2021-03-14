package com.legyver.utils.mapqua;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.jackiso.JacksonObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;
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

	private Optional execute(Map map) throws CoreException {
		return execute(map, keys.size());
	}

	private Optional execute(Map map, int stop) throws CoreException {
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
	private Object adjustForType(Object value, Class toClass) throws CoreException {
		Object result = value;
		if (toClass.equals(int.class) || toClass.equals(Integer.class)) {
			if (value instanceof Double) {
				Double doubleValue = (Double) value;
				result = doubleValue.intValue();
			}
		} else if (toClass.equals(LocalDateTime.class)) {
			result = mapToType(value, toClass);
		} else if (toClass.equals(LocalDate.class)) {
			result = mapToType(value, toClass);
		}
		return result;
	}

	private <T> T mapToType(Object value, Class<T> klass) throws CoreException {
		String json = JacksonObjectMapper.INSTANCE.writeValueAsString(value);
		return JacksonObjectMapper.INSTANCE.readValue(json, klass);
	}
	
	private Map getObjectMap(Map map) throws CoreException {
		Map objectMap;
		if (keys.size() == 1) {
			objectMap = map;
		} else {
			objectMap = (Map) execute(map, keys.size() - 1).get();
		}
		return objectMap;
	}

	private void executeSet(Map map, Object value) throws CoreException {
		Map objectMap = getObjectMap(map);
		objectMap.put(keys.get(keys.size() - 1).key, mapPojo(value));
	}

	private void executeMerge(Map map, Object value) throws CoreException {
		Map objectMap = getObjectMap(map);
		objectMap.merge(keys.get(keys.size() - 1).key, mapPojo(value), new MapMerge());
	}

	private void executeAdd(Map map, Object value) throws CoreException {
		Optional<Collection> optional = execute(map, keys.size() - 1);
		Collection valueCollection = optional.get();
		value = mapPojo(value);
		valueCollection.add(value);
	}

	private Object mapPojo(Object value) throws CoreException {
		//things we can write without further ado
		if (value != null && !(value.getClass().isPrimitive()
				|| value instanceof String
				|| value instanceof Number
				|| value instanceof Collection
				|| value instanceof Temporal
		)) {
			//write POJO's as a map
			value = mapToType(value, Map.class);
		}
		return value;
	}

	/**
	 * Parent builder to create a Map-based query
	 */
	public static class ExecutableQueryBuilder<T extends ExecutableQueryBuilder, U> {

		final List<Ctx> keys;
		final boolean nullable;

		private ExecutableQueryBuilder(List<Ctx> keys, boolean nullable) {
			this.keys = keys;
			this.nullable = nullable;
		}

		/**
		 * Executes the query against the map
		 * @param map the Map to query
		 * @return Optional.ofNullable() if the query has no result, else the result of the query
		 * @throws CoreException if there is a problem marshalling to/from JSON
		 */
		public Optional<U> execute(Map map) throws CoreException {
			return new MapQuery(keys, nullable).execute(map);
		}
	}

	/**
	 * Builder to create Object-typed queries
	 */
	public static class AbstractQueryBuilder extends ExecutableQueryBuilder<AbstractQueryBuilder, Object> {

		private AbstractQueryBuilder(List<Ctx> keys, boolean nullable) {
			super(keys, nullable);
		}

		private AbstractQueryBuilder(boolean nullable) {
			this(new ArrayList<>(), nullable);
		}

		/**
		 * Transition to a {@link SetQueryBuilder}
		 * @param name name of the value
		 * @param value value
		 * @return SetQueryBuilder
		 */
		public SetQueryBuilder set(String name, Object value) {
			keys.add(new Ctx(name, Object.class, Object.class));
			return new SetQueryBuilder(keys, value);
		}


		/**
		 * Transition to a {@link MergeQueryBuilder}
		 * @param name name of the value
		 * @param value value
		 * @return MergeQueryBuilder
		 */
		public MergeQueryBuilder merge(String name, Object value) {
			keys.add(new Ctx(name, Object.class, Object.class));
			return new MergeQueryBuilder(keys, value);
		}

		/**
		 * The value to be queried is of type Object
		 * @param name name of the value
		 * @return AbstractQueryBuilder
		 */
		public AbstractQueryBuilder object(String name) {
			keys.add(new Ctx(name, Object.class, Object.class));
			return this;
		}

		/**
		 * Transition to a {@link CollectionQueryBuilder}
		 * @param name name of the value
		 * @return CollectionQueryBuilder
		 */
		public CollectionQueryBuilder collection(String name) {
			keys.add(new Ctx(name, Collection.class, Collection.class));
			return new CollectionQueryBuilder(keys, nullable);
		}

		/**
		 * The value to be queried is of type LocalDate
		 * @param name name of the value
		 * @return ChildQueryBuilder
		 */
		public ChildQueryBuilder<LocalDate> localDate(String name) {
			keys.add(new Ctx(name, Map.class, LocalDate.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		/**
		 * The value to be queried is of type LocalDateTime
		 * @param name name of the value
		 * @return ChildQueryBuilder
		 */
		public ChildQueryBuilder<LocalDateTime> localDateTime(String name) {
			keys.add(new Ctx(name, Map.class, LocalDateTime.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		/**
		 * The value to be queried is of type String
		 * @param name name of the value
		 * @return ChildQueryBuilder
		 */
		public ChildQueryBuilder<String> string(String name) {
			keys.add(new Ctx(name, String.class, String.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		/**
		 * The value to be queried is of type Integer
		 * @param name name of the value
		 * @return ChildQueryBuilder
		 */
		public ChildQueryBuilder<Integer> integer(String name) {
			keys.add(new Ctx(name, Double.class, Integer.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}

		/**
		 * The value to be queried is of type Double
		 * @param name name of the value
		 * @return ChildQueryBuilder
		 */
		public ChildQueryBuilder<Double> floatingPoint(String name) {
			keys.add(new Ctx(name, Double.class, Double.class));
			return new ChildQueryBuilder<>(keys, nullable);
		}
	}

	/**
	 * Top-level builder for readability of Queries
	 */
	public static class Query extends AbstractQueryBuilder {

		/**
		 * Create a Query
		 */
		public Query() {
			super(true);
		}
	}

	/**
	 * Builder hook to continue building child query
	 * @param <T> the expected type of the result
	 */
	public static class ChildQueryBuilder<T> extends ExecutableQueryBuilder<ChildQueryBuilder, T> {
		private ChildQueryBuilder(List<Ctx> keys, boolean nullable) {
			super(keys, nullable);
		}
	}

	/**
	 * Query to build a collection of results
	 */
	public static class CollectionQueryBuilder extends ExecutableQueryBuilder<CollectionQueryBuilder, Collection> {

		private CollectionQueryBuilder(List<Ctx> keys, boolean nullable) {
			super(keys, nullable);
		}

		/**
		 * Supply a filter to pass be applied to the query
		 * @param filter filter to apply to the collection
		 * @return the Query Builder
		 */
		public AbstractQueryBuilder filter(KeyValueFilter filter) {
			Ctx ctx = super.keys.get(keys.size() - 1);
			ctx.filter = filter;
			return new AbstractQueryBuilder(super.keys, super.nullable);
		}

		/**
		 * Add a value to the query
		 * @param value the value to add
		 * @return the AddQueryBuilder
		 */
		public AddQueryBuilder add(Object value) {
			keys.add(new Ctx(null, Object.class, Object.class));
			return new AddQueryBuilder(keys, value);
		}

	}

	/**
	 * A child QueryBuilder when the value is expected to be set on the query result
	 */
	public static class SetQueryBuilder extends ExecutableQueryBuilder<SetQueryBuilder, Object> {
		private final Object value;

		private SetQueryBuilder(List<Ctx> keys, Object value) {
			super(keys, true);
			this.value = value;
		}

		@Override
		public Optional execute(Map map) throws CoreException {
			new MapQuery(keys, nullable).executeSet(map, value);
			return Optional.of(value);
		}
	}

	/**
	 * A child QueryBuilder when the value is expected to be merged with the current value present on the query result
	 */
	public static class MergeQueryBuilder extends ExecutableQueryBuilder<MergeQueryBuilder, Object> {
		private final Object value;

		private MergeQueryBuilder(List<Ctx> keys, Object value) {
			super(keys, true);
			this.value = value;
		}

		@Override
		public Optional execute(Map map) throws CoreException {
			new MapQuery(keys, nullable).executeMerge(map, value);
			return Optional.of(value);
		}
	}

	/**
	 * A child QueryBuilder when the value is expected to be added the a collection located on the query result
	 */
	public static class AddQueryBuilder extends ExecutableQueryBuilder<AddQueryBuilder, Object> {

		private final Object value;

		private AddQueryBuilder(List<Ctx> keys, Object value) {
			super(keys, true);
			this.value = value;
		}

		@Override
		public Optional execute(Map map) throws CoreException {
			new MapQuery(keys, nullable).executeAdd(map, value);
			return Optional.of(value);
		}
	}

	/**
	 * Filter for query
	 */
	public static class KeyValueFilter implements Predicate<Map> {
		private final String mapKey;
		private final Object value;
		private final Cond cond;

		/**
		 * Construct a KeyValueFilter with the provided values
		 * @param mapKey the map key to look up
		 * @param cond the {@link Cond} condition to match on
		 * @param value the value considered an adequate match
		 */
		public KeyValueFilter(String mapKey, Cond cond, Object value) {
			this.mapKey = mapKey;
			this.cond = cond;
			this.value = value;
		}

		@Override
		public boolean test(Map m) {
			return cond.test(m.get(mapKey), value);
		}

		/**
		 * Conditions for the Query
		 */
		public enum Cond {
			/**
			 * Condition Equals
			 */
			EQ {
				@Override
				public boolean test(Object actualValue, Object matchValue) {
					return actualValue.equals(matchValue);
				}
			};

			/**
			 * Tests the condition
			 * @param actualValue the actual value
			 * @param matchValue the match value
			 * @return true if the values match, otherwise false
			 */
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
