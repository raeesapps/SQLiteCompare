package org.sqlitecompare.model;

import org.junit.Assert;
import org.junit.Test;

public final class DatabaseObjectTests {
  @Test
  public void databaseObject_ToString_ProducesValidYaml() {
    var employees = new DatabaseObject
        .Builder()
        .name("employees")
        .objectType("table")
        .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
        .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
        .build();

    var serialisationOfEmployees = employees.toString();

    Assert.assertEquals("""
        dependencies:
        - {dependencyType: SUBOBJECT, nameOfConsumedObject: firstname}
        - {dependencyType: SUBOBJECT, nameOfConsumedObject: surname}
        name: employees
        objectType: table
        properties: {}
                        """, serialisationOfEmployees);
  }
}
