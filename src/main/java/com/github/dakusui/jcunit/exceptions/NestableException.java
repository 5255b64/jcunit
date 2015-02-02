package com.github.dakusui.jcunit.exceptions;

import com.github.dakusui.jcunit.core.Checks;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class NestableException extends JCUnitException {
  private final List<Throwable> nested = new LinkedList<Throwable>();

  /**
   * Creates an object of this class.
   *
   * @param message An error message for this object.
   * @param t       A nested <code>throwable</code> object.
   */
  public NestableException(String message, Throwable t) {
    super(message, t);
  }

  /**
   * Creates an object of this class.
   *
   * @param message An error message for this object.
   */
  public NestableException(String message) {
    super(message, null);
  }

  public void addChild(Throwable child) {
    this.nested.add(Checks.checknotnull(child));
  }

  @Override
  public String getMessage() {
    String ret = super.getMessage();
    if (!nested.isEmpty()) {
      ret += ":[";
      boolean isFirst = true;
      for (Throwable each : this.nested) {
        if (!isFirst) {
          ret += ",";
        }
        ret += each.getMessage();
        isFirst = false;
      }
      ret += "]";
    }
    return ret;
  }

  public boolean hasChildren() {
    return !nested.isEmpty();
  }

  public void printStackTrace(PrintStream ps) {
    // Keep the same style as java.lang.Throwable.
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (ps) {
      //tps.println(super.getMessage());
      super.printStackTrace(ps);
      ps.println();
      for (Throwable each : this.nested) {
        each.printStackTrace(ps);
      }
    }
  }
  @Override
  public void printStackTrace(PrintWriter pw) {
    // Keep the same style as java.lang.Throwable.
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (pw) {
      //tpw.println(super.getMessage());
      super.printStackTrace(pw);
      pw.println();
      for (Throwable each : this.nested) {
        each.printStackTrace(pw);
      }
    }
  }

}
