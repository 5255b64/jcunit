package com.github.dakusui.jcunit.runners.standard.annotations;

import com.github.dakusui.jcunit.plugins.caengines.CoveringArrayEngine;
import com.github.dakusui.jcunit.plugins.caengines.IPO2CoveringArrayEngine;
import com.github.dakusui.jcunit.plugins.caengines.StandardCoveringArrayEngine;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Generator {
  Generator DEFAULT= new Generator() {

    @Override
    public Class<? extends Annotation> annotationType() {
      return Generator.class;
    }

    @Override
    public Class<? extends CoveringArrayEngine> value() {
      return StandardCoveringArrayEngine.class;
    }

    @Override
    public Value[] args() {
      return new Value[] { new Value.Builder().add("2").build()};
    }
  };
  Class<? extends CoveringArrayEngine> value() default IPO2CoveringArrayEngine.class;

  Value[] args() default {};

}
