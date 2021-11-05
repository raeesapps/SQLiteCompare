package org.sqlitecompare.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public final class DatabaseFactoryTests {
  @Test
  public void createDatabase_GivenYaml_CorrectlyDeserialisesDatabase()
      throws ExecutionException, InterruptedException {
    var yaml = """
        objectLists:
          tables:
          - dependencies:
            - {dependencyType: SUBOBJECT, nameOfConsumedObject: firstname}
            - {dependencyType: SUBOBJECT, nameOfConsumedObject: surname}
            name: employees
            objectType: table
            properties: {}
          columns:
          - dependencies: &id001 []
            name: firstname
            objectType: column
            properties: {type: varchar}
          - dependencies: *id001
            name: surname
            objectType: column
            properties: {type: varchar}
        """;

    var expectedDatabase = new Database(
        new ImmutableMap.Builder<String, ImmutableList<DatabaseObject>>()
            .put("tables", new ImmutableList.Builder<DatabaseObject>().add(new DatabaseObject
                .Builder()
                .name("employees")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build()).build())
            .put("columns", new ImmutableList.Builder<DatabaseObject>().add(
                new DatabaseObject
                    .Builder()
                    .name("firstname")
                    .objectType("column")
                    .property("type", "varchar")
                    .build(),
                new DatabaseObject
                    .Builder()
                    .name("surname")
                    .objectType("column")
                    .property("type", "varchar")
                    .build()).build()).build());
    var actualDatabase = DatabaseFactory.createDatabase(yaml);

    Assert.assertEquals(expectedDatabase, actualDatabase);
  }
}
