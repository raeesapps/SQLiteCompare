package org.sqlitecompare.model.table;

import org.sqlitecompare.model.Database;
import org.sqlitecompare.model.DatabaseObject;
import org.sqlitecompare.model.Dependency;
import org.sqlitecompare.model.DependencyType;

public final class TableBuilder {
    private final DatabaseObject.Builder tableBuilder = new DatabaseObject.Builder().objectType("table");

    public TableBuilder withName(String tableName) {
        tableBuilder.name(tableName);
        return this;
    }

    public TableBuilder withDependency(String columnName) {
        tableBuilder.dependency(new Dependency(DependencyType.SUBOBJECT, columnName));
        return this;
    }

    public DatabaseObject build() {
        return tableBuilder.build();
    }

    public static void addTable(Database.Builder databaseBuilder, DatabaseObject table, DatabaseObject... columns) {
        databaseBuilder.object("tables", table);

        for (var column : columns) {
            databaseBuilder.object("columns", column);
        }
    }
}
