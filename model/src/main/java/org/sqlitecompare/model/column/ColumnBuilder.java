package org.sqlitecompare.model.column;

import org.sqlitecompare.model.DatabaseObject;

public final class ColumnBuilder {
  private final DatabaseObject.Builder builder = new DatabaseObject.Builder().objectType("column");

  public ColumnBuilder withName(String name) {
    builder.name(name);
    return this;
  }

  public ColumnBuilder withDatatype(String datatype) {
    builder.property("datatype", datatype);
    return this;
  }

  public ColumnBuilder withParentTable(String parentTableName) {
    builder.property("parent", parentTableName);
    return this;
  }

  public DatabaseObject build() {
    return builder.build();
  }
}
