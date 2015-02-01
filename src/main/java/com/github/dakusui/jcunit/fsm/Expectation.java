package com.github.dakusui.jcunit.fsm;

import com.github.dakusui.jcunit.core.Checks;
import com.github.dakusui.jcunit.exceptions.JCUnitEnvironmentException;
import com.github.dakusui.jcunit.exceptions.JCUnitException;
import com.github.dakusui.jcunit.exceptions.NestableException;
import org.hamcrest.Matcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Expectation<SUT> {

  private final Type       type;
  public final State<SUT>  state;
  private final Matcher    matcher;

  public Expectation(
      Type type,
      State<SUT> state,
      Matcher matcher) {
    Checks.checknotnull(type);
    Checks.checknotnull(state);
    Checks.checknotnull(matcher);
    this.type = type;
    this.state = state;
    this.matcher = matcher;
  }
/*
          fail(String.format("'%s' is expected to be thrown.", each.then().thrownException));


          valueReturnedOrExceptionThrown = "thrown";
          actual = t;
          if (each.then().thrownException == null)
            throw t;
          outputCheck = each.then().thrownException.matches(t);
          boolean stateCheck = each.then().state.check(sut);
          if (outputCheck && stateCheck)
            reporter.passed(each, sut);
          else
            reporter.failed(each, sut);
          assertTrue(
              String.format("Expected: %s, but '%s' is %s.", each.then(), actual, valueReturnedOrExceptionThrown),
              outputCheck
          );

          assertTrue(
              String.format("Expected status of the SUT is '%s' but it is not satisfied.", each.then().state),
              stateCheck
          );

 */
  public Result checkThrownException(SUT sut, Throwable thrownException) {
    Checks.checknotnull(sut);
    Checks.checknotnull(thrownException);
    Result.Builder b = new Result.Builder("Expectation was not satisfied");
    if (this.type != Type.EXCEPTION_THROWN) {
      b.addFailedReason(String.format("Exception was expected to be thrown but not. (%s)", this.matcher));
    }
    if (!this.matcher.matches(thrownException)) {
      b.addFailedReason(
          String.format("'%s' is expected to be %s but '%s' was thrown. (%s)", this.matcher, this.type, thrownException, thrownException.getMessage()),
          thrownException
      );
    }
    if (!this.state.check(sut)) {
      b.addFailedReason(
          String.format("'%s' is in '%s' state but not.", this.state)
      );
    }
    return b.build();
  }

  public Result checkReturnedValue(SUT sut, Object returnedValue) {
    Checks.checknotnull(sut);
    Result.Builder b = new Result.Builder(String.format("Expectation: [%s] was not satisfied", this));
    if (this.type != Type.VALUE_RETURNED) {
      b.addFailedReason(String.format("Exception was expected not to be thrown but it was. (%s)", this.matcher));
    }
    if (!this.matcher.matches(returnedValue)) {
      b.addFailedReason(
          String.format("'%s' is expected to be %s but '%s' was thrown.", this.matcher, this.type, returnedValue)
      );
    }
    if (!this.state.check(sut)) {
      b.addFailedReason(
          String.format("'%s' is in '%s' state but not.", this.state)
      );
    }
    return b.build();
  }

  @Override
  public String toString() {
    if (this.type == Type.EXCEPTION_THROWN)
      return String.format("Status is '%s' and %s is thrown", this.state, this.matcher);
    return String.format("Status is '%s' and %s is returned", this.state, this.matcher);
  }

  public static enum Type {
    EXCEPTION_THROWN {
      @Override public String toString() {
        return "thrown";
      }
    },
    VALUE_RETURNED {
      public String toString() {
        return "returned";
      }
    }
  }

  static class Reason {
    private final String    message;
    private final Throwable t;

    Reason(String message, Throwable t) {
      this.message = message;
      this.t = t;
    }
  }


  public static class Result extends JCUnitException {
    private final List<Reason> failedReasons;

    public Result(String message, List<Reason> failedReasons) {
      super(message, null);
      this.failedReasons = Collections.unmodifiableList(failedReasons);
    }

    public boolean isSuccessful() {
      return this.failedReasons.isEmpty();
    }

    public void throwIfFailed() {
      if (!this.isSuccessful()) {
        this.fillInStackTrace();
        throw this;
      }
    }

    @Override
    public String getMessage() {
      String ret = super.getMessage();
      if (!failedReasons.isEmpty()) {
        ret += ":[";
        boolean isFirst = true;
        for (Reason each : this.failedReasons) {
          if (!isFirst) {
            ret += ",";
          }
          ret += each.message;
          isFirst = false;
        }
        ret += "]";
      }
      return ret;
    }

    public List<Reason> getFailedReasons() {
      return this.failedReasons;
    }

    static class Builder {
      private List<Reason> failures = new LinkedList<Reason>();
      private String message;

      Builder(String message) {
        this.message = message;
      }

      Builder addFailedReason(String message) {
        Checks.checknotnull(message);
        return this.addFailedReason(message, (Throwable)null);
      }

      Builder addFailedReason(String message, Throwable t) {
        this.failures.add(new Reason(message, t));
        return this;
      }

      Result build() {
        return new Result(message, failures);
      }
    }
  }
}