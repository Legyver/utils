package com.legyver.utils.graphrunner;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to create a Graph from one or more property files by identifying properties with keys that are present in other keys' values
 */
public class PropertyGraphFactory {

	private final VariableExtractionOptions variableExtractionOptions;
	private final VariableTransformationRule variableTransformationRule;

	/**
	 * Construct a PropertyGraphFactory with the specified variable extraction options and transformation rules.
	 * @param variableExtractionOptions the variable extraction options to use
	 * @param variableTransformationRule the variable transformation rule to use
	 */
	public PropertyGraphFactory(VariableExtractionOptions variableExtractionOptions,
								VariableTransformationRule variableTransformationRule) {
		this.variableExtractionOptions = variableExtractionOptions;
		this.variableTransformationRule = variableTransformationRule;
	}

	/**
	 * Construct a PropertyGraphFactory with the specified variable extraction options.
	 * The variable transformation rule will be null.  Use this if you do no need to do any transformation.
	 * @param variableExtractionOptions the variable extraction options to use
	 */
	public PropertyGraphFactory(VariableExtractionOptions variableExtractionOptions) {
		this(variableExtractionOptions, null);
	}

	/**
	 * Construct a PropertyGraphFactory with the specified tokenizer pattern and group
	 * @param tokenizerPattern the regular expression to use
	 * @param group the group to extract
	 */
	public PropertyGraphFactory(Pattern tokenizerPattern, int group) {
		this(new VariableExtractionOptions(tokenizerPattern, group));
	}

	/**
	 * Make a directional graph of properties that reference other properties
	 * example:
	 *   major.version=1
	 *   minor.version=0
	 *   patch.number=0
	 *
	 *   build.number=0000
	 *   build.date.day=11
	 *   build.date.month=April
	 *   build.date.year=2020
	 *
	 *   build.date.format=`${build.date.day} ${build.date.month} ${build.date.year}`
	 *   build.version.format=`${major.version}.${minor.version}.${patch.number}.${build.number}`
	 *   build.message.format=`Build ${build.version}, built on ${build.date}`
	  Note to make build.version resolve as the outcome of build.version.format, we need to use specify this in the {@link #variableTransformationRule}
	 * @param propertyMap map containing properties
	 * @param newNodeFactory factory to use to construct new nodes based on a property's key and value
	 * @return the graph of all nodes in the propertyMap
	 */
	public Graph make(Map<String, Object> propertyMap, BiFunction<String, Object, Graph.Payload> newNodeFactory) {
		Graph.Builder builder = new Graph.Builder();
		//add all properties to graph
		propertyMap.entrySet().stream().forEach(entry-> {
			builder.nodes(newNodeFactory.apply(entry.getKey(), entry.getValue()));
		});

		List<DirectionalProperty> directionalProperties = link(propertyMap.entrySet());
		for (DirectionalProperty directionalProperty : directionalProperties) {
			for (String predecessor : directionalProperty.depends) {
				builder.connect(new Graph.Connection()
						.from(predecessor)
						.to(directionalProperty.key));
			}
		}
		return builder.build();
	}

	private List<DirectionalProperty> link(Set<? extends Map.Entry<String, ?>> propertiesToResolve) {
		List<DirectionalProperty> result = new ArrayList<>();
		for (Map.Entry<String, ? extends Object> property : propertiesToResolve) {
			DirectionalProperty directionalProperty = new DirectionalProperty(property.getKey());
			result.add(directionalProperty);
			String propertyValue = String.valueOf(property.getValue());

			//ex: Since the result of the operation on build.date.format is set as build.date
			// build.date.format must be resolved first before any operation involving build.date can be executed.
			if (variableTransformationRule != null && variableTransformationRule.matches(property.getKey())) {
				// in this example, transformed would be build.date and referenceProperty.key would be build.date.format
				String transformed = variableTransformationRule.transform(property.getKey());
				//so the dependency is transformed depends on referenceProperty.key
				DirectionalProperty alias = new DirectionalProperty(transformed);
				alias.depends.add(property.getKey());
				result.add(alias);
			}

			Matcher m = variableExtractionOptions.getTokenizerPattern().matcher(propertyValue);
			while (m.find()) {
				String group = m.group(variableExtractionOptions.getGroup());
				directionalProperty.depends.add(group);
			}
		}
		return result;
	}

	private class DirectionalProperty {
		private final String key;
		private final Set<String> depends = new HashSet<>();

		private DirectionalProperty(String key) {
			this.key = key;
		}
	}
}
