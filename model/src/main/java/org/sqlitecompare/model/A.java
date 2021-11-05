package org.sqlitecompare.model;

import org.sqlitecompare.model.column.ColumnBuilder;
import org.sqlitecompare.model.table.TableBuilder;

public final class A {
  public static TableBuilder table() {
    return new TableBuilder();
  }

  public static ColumnBuilder column() {
    return new ColumnBuilder();
  }
}
