package org.sqlitecompare.comparison;

import org.junit.Test;
import org.sqlitecompare.model.A;
import org.sqlitecompare.model.Database;
import org.sqlitecompare.model.table.TableBuilder;

import java.util.stream.Collectors;

import static org.sqlitecompare.comparison.DifferencesAssert.assertThat;

public final class ComparerTests {
  @Test
  public void empty_database_has_no_differences() {
    var sourceDatabase = new Database.Builder().build();
    var targetDatabase = new Database.Builder().build();

    var differences = Comparer.compare(sourceDatabase, targetDatabase).collect(Collectors.toList());

    assertThat(differences).isEmpty();
  }

  @Test
  public void employees_table_only_in_source() {
    var sourceDatabaseBuilder = new Database.Builder();
    var sourceEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var sourceEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var sourceEmployeesSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        sourceDatabaseBuilder, sourceEmployees, sourceEmployeesFirstname, sourceEmployeesSurname);
    var source = sourceDatabaseBuilder.build();
    var targetDatabaseBuilder = new Database.Builder();
    var target = targetDatabaseBuilder.build();

    var differences = Comparer.compare(source, target).collect(Collectors.toList());

    assertThat(differences).containsOnlyInSource(sourceEmployees);
    assertThat(differences).containsOnlyInSource(sourceEmployeesFirstname);
    assertThat(differences).containsOnlyInSource(sourceEmployeesSurname);
  }

  @Test
  public void employees_table_only_in_target() {
    var sourceDatabaseBuilder = new Database.Builder();
    var source = sourceDatabaseBuilder.build();
    var targetDatabaseBuilder = new Database.Builder();
    var targetEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var targetEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var targetEmployeesSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        targetDatabaseBuilder, targetEmployees, targetEmployeesFirstname, targetEmployeesSurname);
    var target = targetDatabaseBuilder.build();

    var differences = Comparer.compare(source, target).collect(Collectors.toList());

    assertThat(differences).containsOnlyInTarget(targetEmployees);
    assertThat(differences).containsOnlyInTarget(targetEmployeesFirstname);
    assertThat(differences).containsOnlyInTarget(targetEmployeesSurname);
  }

  @Test
  public void surname_datatype_different() {
    var sourceDatabaseBuilder = new Database.Builder();
    var sourceEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var sourceEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var sourceEmployeesSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        sourceDatabaseBuilder, sourceEmployees, sourceEmployeesFirstname, sourceEmployeesSurname);
    var source = sourceDatabaseBuilder.build();
    var targetDatabaseBuilder = new Database.Builder();
    var targetEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var targetEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var targetEmployeesSurname = A.column().withName("surname").withDatatype("int")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        targetDatabaseBuilder, targetEmployees, targetEmployeesFirstname, targetEmployeesSurname);
    var target = targetDatabaseBuilder.build();

    var differences = Comparer.compare(source, target).collect(Collectors.toList());

    assertThat(differences).containsEqual(sourceEmployees, targetEmployees);
    assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
    assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
  }

  @Test
  public void surname_datatype_different_and_target_has_job_column() {
    var sourceDatabaseBuilder = new Database.Builder();
    var sourceEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var sourceEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var sourceEmployeesSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        sourceDatabaseBuilder, sourceEmployees, sourceEmployeesFirstname, sourceEmployeesSurname);
    var source = sourceDatabaseBuilder.build();
    var targetDatabaseBuilder = new Database.Builder();
    var targetEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").withDependency("job").build();
    var targetEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var targetEmployeesSurname = A.column().withName("surname").withDatatype("int")
        .withParentTable("employees").build();
    var targetEmployeesJob = A.column().withName("job").withDatatype("varchar")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        targetDatabaseBuilder, targetEmployees, targetEmployeesFirstname, targetEmployeesSurname,
        targetEmployeesJob);
    var target = targetDatabaseBuilder.build();

    var differences = Comparer.compare(source, target).collect(Collectors.toList());

    assertThat(differences).containsDifferent(sourceEmployees, targetEmployees);
    assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
    assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
    assertThat(differences).containsOnlyInTarget(targetEmployeesJob);
  }

  @Test
  public void surname_datatype_different_in_employees_but_equal_in_hr() {
    var sourceDatabaseBuilder = new Database.Builder();
    var sourceEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var sourceEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var sourceEmployeesSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        sourceDatabaseBuilder, sourceEmployees, sourceEmployeesFirstname, sourceEmployeesSurname);
    var sourceHr = A.table().withName("hr").withDependency("firstname").withDependency("surname")
        .build();
    var sourceHrFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("hr").build();
    var sourceHrSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("hr").build();
    TableBuilder.addTable(sourceDatabaseBuilder, sourceHr, sourceHrFirstname, sourceHrSurname);
    var source = sourceDatabaseBuilder.build();

    var targetDatabaseBuilder = new Database.Builder();
    var targetEmployees = A.table().withName("employees").withDependency("firstname")
        .withDependency("surname").build();
    var targetEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("employees").build();
    var targetEmployeesSurname = A.column().withName("surname").withDatatype("int")
        .withParentTable("employees").build();
    TableBuilder.addTable(
        targetDatabaseBuilder, targetEmployees, targetEmployeesFirstname, targetEmployeesSurname);
    var targetHr = A.table().withName("hr").withDependency("firstname").withDependency("surname")
        .build();
    var targetHrFirstname = A.column().withName("firstname").withDatatype("varchar")
        .withParentTable("hr").build();
    var targetHrSurname = A.column().withName("surname").withDatatype("varchar")
        .withParentTable("hr").build();
    TableBuilder.addTable(targetDatabaseBuilder, targetHr, targetHrFirstname, targetHrSurname);
    var target = targetDatabaseBuilder.build();

    var differences = Comparer.compare(source, target).collect(Collectors.toList());

    assertThat(differences).containsEqual(sourceEmployees, targetEmployees);
    assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
    assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
    assertThat(differences).containsEqual(sourceHr, targetHr);
    assertThat(differences).containsEqual(sourceHrFirstname, targetHrFirstname);
    assertThat(differences).containsEqual(targetHrSurname, targetHrSurname);
  }
}
