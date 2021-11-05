package org.sqlitecompare.model;

public enum DependencyType {
  SUBOBJECT(0),
  REQUIRES(1);

  private final int id;

  DependencyType(int id) {
    this.id = id;
  }

  public static DependencyType parse(String dependencyType) {
    return DependencyType.valueOf(dependencyType);
  }
}
