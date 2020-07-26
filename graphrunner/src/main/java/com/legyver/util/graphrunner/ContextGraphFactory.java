package com.legyver.util.graphrunner;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextGraphFactory {

	private final VariableExtractionOptions variableExtractionOptions;
	private final VariableTransformationRule variableTransformationRule;

	public ContextGraphFactory(VariableExtractionOptions variableExtractionOptions,
							   VariableTransformationRule variableTransformationRule) {
		this.variableExtractionOptions = variableExtractionOptions;
		this.variableTransformationRule = variableTransformationRule;
	}

	public ContextGraphFactory(VariableExtractionOptions variableExtractionOptions) {
		this(variableExtractionOptions, null);
	}

	public ContextGraphFactory(Pattern tokenizerPattern, int group) {
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
	  Note to make build.version resolve as the outcome of build.version.format, we need to use specify this in the {@link variableTransformationRule}
	 * @param propertyMap: map containing properties
	 * @return
	 */
	public ContextGraph make(Map<String, ? extends Object> propertyMap) {
		ContextGraph contextGraph = new ContextGraph();

		List<DirectionalProperty> directionalProperties = link(propertyMap.entrySet());
		for (DirectionalProperty directionalProperty : directionalProperties) {
			for (String predecessor : directionalProperty.depends) {
				contextGraph.accept(directionalProperty.key, predecessor);
			}
		}
		return contextGraph;
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
