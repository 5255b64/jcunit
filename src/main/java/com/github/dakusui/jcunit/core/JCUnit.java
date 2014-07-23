package com.github.dakusui.jcunit.core;

import com.github.dakusui.jcunit.constraint.ConstraintManager;
import com.github.dakusui.jcunit.constraint.LabeledTestCase;
import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.generators.TestCaseGenerator;
import com.github.dakusui.jcunit.generators.TestCaseGeneratorFactory;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class JCUnit extends Suite {
	private final ArrayList<Runner> runners = new ArrayList<Runner>();

	/**
	 * Only called reflectively by JUnit. Do not use programmatically.
	 */
	public JCUnit(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		TestCaseGenerator testCaseGenerator = TestCaseGeneratorFactory.INSTANCE
				.createTestCaseGenerator(klass);
		int id;
    // TODO implement 'label' feature.
		for (id = 0; id < testCaseGenerator.size(); id++) {
			runners.add(new JCUnitRunner(getTestClass().getJavaClass(),
					JCUnitTestCaseType.Normal, id, new LinkedList<Serializable>(), testCaseGenerator.get(id)));
		}
    // TODO
		ConstraintManager cm = testCaseGenerator.getConstraintManager();
		final List<LabeledTestCase> violations = cm.getViolations();
		for (LabeledTestCase violation : violations) {
			runners.add(new JCUnitRunner(getTestClass().getJavaClass(),
					JCUnitTestCaseType.Violation, id, violation.getLabels(),
					violation.getTestCase()));
			id++;
		}
    // TODO
		if (hasParametersMethod()) {
			for (Tuple tuple : allCustomTuples()) {
				runners.add(new JCUnitRunner(getTestClass().getJavaClass(),
						JCUnitTestCaseType.Custom, id, new LinkedList<Serializable>(), tuple));
				id++;
			}
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private Iterable<Tuple> allCustomTuples() throws Throwable {
		Object parameters = getParametersMethod().invokeExplosively(null);
		if (parameters instanceof Iterable) {
			return (Iterable<Tuple>) parameters;
		} else {
			throw parametersMethodReturnedWrongType();
		}
	}

	private boolean hasParametersMethod() {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(
				Parameters.class);
		for (FrameworkMethod each : methods) {
			if (each.isStatic() && each.isPublic()) {
				return true;
			}
		}
		return false;
	}

	private FrameworkMethod getParametersMethod() throws Exception {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(
				Parameters.class);
		for (FrameworkMethod each : methods) {
			if (each.isStatic() && each.isPublic()) {
				return each;
			}
		}
		throw new Exception("No public static parameters method on class "
				+ getTestClass().getName());
	}

	private Exception parametersMethodReturnedWrongType() throws Exception {
		String className = getTestClass().getName();
		String methodName = getParametersMethod().getName();
		String message = MessageFormat.format(
				"{0}.{1}() must return an Iterable of arrays.",
				className, methodName);
		return new Exception(message);
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Parameters {
	}
}
