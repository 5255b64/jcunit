package com.github.dakusui.jcunit.core.rules;

import com.github.dakusui.jcunit.core.JCUnit;
import com.github.dakusui.jcunit.core.Utils;
import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.core.tuples.TupleUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class JCUnitRule extends TestWatcher {
  private Class<?>            testClass;
  private String              testName;
  private int                 id;
  private List<Serializable>  labels;
  private JCUnit.TestCaseType type;
  private Tuple               testCase;

  @Override
  protected void starting(Description d) {
    JCUnit.TestCaseInternalAnnotation ann = d
        .getAnnotation(JCUnit.TestCaseInternalAnnotation.class);
    Utils.checknotnull(ann,
        "This class(%s) should be used with classes annotated @RunWith(%s.class)",
        this.getClass(), JCUnit.class.getClass());
    this.testClass = d.getTestClass();
    this.testName = d.getMethodName();
    this.id = ann.getId();
    this.type = ann.getTestCaseType();
    this.labels = ann.getLabels();
    this.testCase = TupleUtils.unmodifiableTuple(ann.getTestCase());
  }

  public JCUnit.TestCaseType getTestCaseType() {
    return this.type;
  }

  public Class<?> getTestClass() {
    return this.testClass;
  }

  public String getTestName() {
    return this.testName;
  }

  public List<Serializable> getLabels() {
    return Collections.unmodifiableList(this.labels);
  }

  public <T extends Serializable> Iterable<T> getLabels(Class<T> clazz) {
    Utils.checknotnull(clazz);
    List<T> ret = new ArrayList<T>(this.labels.size());
    for (Serializable l : this.labels) {
      if (clazz.isAssignableFrom(l.getClass())) {
        ret.add((T) l);
      }
    }
    return ret;
  }

  public Tuple getTestCase() {
    return this.testCase;
  }

  public int getId() {
    return this.id;
  }

}