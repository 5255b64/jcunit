package com.github.dakusui.jcunit.plugins.caengines;

/**
 */

import com.github.dakusui.jcunit.core.utils.BaseBuilder;
import com.github.dakusui.jcunit.core.utils.Checks;
import com.github.dakusui.jcunit.core.utils.Utils;
import com.github.dakusui.jcunit.core.factor.FactorSpace;
import com.github.dakusui.jcunit.core.factor.Factors;
import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.plugins.Plugin;
import com.github.dakusui.jcunit.plugins.constraints.ConstraintChecker;
import com.github.dakusui.jcunit.runners.core.RunnerContext;
import com.github.dakusui.jcunit.runners.standard.annotations.Generator;
import com.github.dakusui.jcunit.runners.standard.annotations.Value;

import java.util.Collections;
import java.util.List;

import static com.github.dakusui.jcunit.core.utils.Checks.checknotnull;

public interface CoveringArrayEngine extends Plugin {
  CoveringArray generate(FactorSpace factorSpace);

  /**
   * A class to build a tuple generator.
   *
   * @param <S> data source type. Can be {@literal @}Param, {@code String}, etc.
   */
  class Builder<S> {

    private RunnerContext                        runnerContext;
    private Factors                              factors;
    private ConstraintChecker                    constraintChecker;
    private Param.Resolver<S>                    resolver;
    private List<S>                              configArgsForEngine;
    private Class<? extends CoveringArrayEngine> engineClass;

    public Builder(
        RunnerContext runnerContext,
        Factors factors, ConstraintChecker cm, Class<? extends CoveringArrayEngine> engineClass
    ) {
      this.runnerContext = checknotnull(runnerContext);
      this.factors = factors;
      this.engineClass = checknotnull(engineClass);
      this.constraintChecker = checknotnull(cm);
    }

    public Builder setConfigArgsForEngine(List<S> parameters) {
      this.configArgsForEngine = parameters;
      return this;
    }

    public Builder setResolver(Param.Resolver<S> resolver) {
      this.resolver = resolver;
      return this;
    }


    public Factors getFactors() {
      return factors;
    }

    public ConstraintChecker getConstraintChecker() {
      return constraintChecker;
    }

    public CoveringArrayEngine build() {
      checknotnull(this.constraintChecker);
      checknotnull(this.engineClass);
      checknotnull(this.factors);
      checknotnull(this.constraintChecker);
      checknotnull(this.runnerContext);
      CoveringArrayEngine ret;
      Plugin.Factory<CoveringArrayEngine, S> factory;
      List<S> configArgsForEngine;
      if (this.resolver != null) {
        // PECS cast: Factory uses a class object as a consumer.
        //noinspection unchecked
        factory = new Factory<CoveringArrayEngine, S>(
            (Class<? super CoveringArrayEngine>) this.engineClass,
            this.resolver,
            runnerContext
        );
        configArgsForEngine = this.configArgsForEngine;
      } else {
        // PassThroughResolver is always safe.
        //noinspection unchecked
        factory = new Factory<CoveringArrayEngine, S>(
            (Class<? super CoveringArrayEngine>) this.engineClass,
            (Param.Resolver<S>) Param.Resolver.passThroughResolver(),
            runnerContext
        );
        configArgsForEngine = Collections.emptyList();
      }
      ret = factory.create(configArgsForEngine);
      return ret;
    }

  }

  /**
   * An abstract model class that provides a basic implementation of {@code CAEngine}.
   * Users can create a new tuple generator by extending this class.
   */
  abstract class Base extends Plugin.Base implements CoveringArrayEngine, Plugin {

    public Base() {
    }

    final public CoveringArray generate(FactorSpace factorSpace) {
      Checks.checknotnull(factorSpace);
      return createCoveringArray(Utils.transform(
          Utils.dedup(generate(factorSpace.factors, factorSpace.constraintChecker)),
          new Utils.Form<Tuple, Tuple>() {
            @Override
            public Tuple apply(Tuple in) {
              return new Tuple.Builder().putAll(in).dictionaryOrder(true).build();
            }
          }
      ));
    }

    protected CoveringArray createCoveringArray(List<Tuple> testCases) {
      return new CoveringArray.Base(testCases);
    }

    /**
     * Implementation of this method must return a list of tuples (test cases)
     * generated by this object.
     * <p/>
     *
     * @param factors    factors from which tuples will be generated.
     * @param constraintChecker constraint on tuple generation.
     */
    abstract protected List<Tuple> generate(Factors factors, ConstraintChecker constraintChecker);
  }

  class FromAnnotation implements BaseBuilder<CoveringArrayEngine> {
    private final Class<? extends CoveringArrayEngine> engineClass;
    private final RunnerContext                        runnerContext;
    private final List<Value>                          configValues;

    public FromAnnotation(Generator engine, RunnerContext runnerContext) {
      this(engine.value(), runnerContext, Utils.asList(engine.args()));
    }

    private FromAnnotation(Class<? extends CoveringArrayEngine> engineClass, RunnerContext context, List<Value> configValues) {
      this.engineClass = checknotnull(engineClass);
      this.runnerContext = checknotnull(context);
      this.configValues = checknotnull(Collections.unmodifiableList(configValues));
    }

    @Override
    public <T extends CoveringArrayEngine> T build() {
      Factory<CoveringArrayEngine, Value> pluginFactory
          = Factory.newFactory(engineClass, new Value.Resolver(), this.runnerContext);
      //noinspection unchecked
      return (T) pluginFactory.create(this.configValues);
    }
  }
}