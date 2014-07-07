package com.github.dakusui.jcunit.core;

import com.github.dakusui.enumerator.tuple.AttrValue;

import java.util.*;

/**
 * Created by hiroshi on 7/3/14.
 */
public class Factor implements Iterable<Object> {
  public final String       name;
  public final List<Object> levels;

  public Factor(String name, List<Object> levels) {
    Utils.checknotnull(name);
    Utils.checknotnull(levels);
    Utils.checkcond(levels.size() > 0,
        String.format("Factor '%s' has no levels.", name));
    this.name = name;
    this.levels = Collections.unmodifiableList(levels);
  }

  public List<AttrValue<String, Object>> asAttrValues() {
    List<AttrValue<String, Object>> ret = new ArrayList<AttrValue<String, Object>>(
        levels.size());
    for (Object l : this.levels) {
      ret.add(new AttrValue<String, Object>(name, l));
    }
    return ret;
  }

  @Override public Iterator<Object> iterator() {
    return this.levels.iterator();
  }

  public static class Builder {
    private String name;
    private List<Object> levels = new LinkedList<Object>();

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder addLevel(Object level) {
      this.levels.add(level);
      return this;
    }

    public Factor build() {
      return new Factor(this.name, this.levels);
    }

    public List<Object> getLevels() {
      return levels;
    }
  }
}