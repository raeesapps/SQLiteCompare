package org.sqlitecompare.comparison;

import org.junit.Test;
import org.sqlitecompare.model.A;
import org.sqlitecompare.model.Database;
import org.sqlitecompare.model.table.TableBuilder;

import java.util.stream.Collectors;

public final class ComparerTests {

    @Test
    public void surname_datatype_different() {
        var sourceDatabaseBuilder = new Database.Builder();
        var sourceEmployees = A.table().withName("employees").withDependency("firstname").withDependency("surname").build();
        var sourceEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar").withParentTable("employees").build();
        var sourceEmployeesSurname = A.column().withName("surname").withDatatype("varchar").withParentTable("employees").build();
        TableBuilder.addTable(sourceDatabaseBuilder, sourceEmployees, sourceEmployeesFirstname, sourceEmployeesSurname);
        var source = sourceDatabaseBuilder.build();
        var targetDatabaseBuilder = new Database.Builder();
        var targetEmployees = A.table().withName("employees").withDependency("firstname").withDependency("surname").build();
        var targetEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar").withParentTable("employees").build();
        var targetEmployeesSurname = A.column().withName("surname").withDatatype("int").withParentTable("employees").build();
        TableBuilder.addTable(targetDatabaseBuilder, targetEmployees, targetEmployeesFirstname, targetEmployeesSurname);
        var target = targetDatabaseBuilder.build();

        var differences = Comparer.compare(source, target).collect(Collectors.toList());

        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployees, targetEmployees);
        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
        DifferencesAssert.assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
    }

    @Test
    public void surname_datatype_different_in_employees_but_equal_in_hr() {
        var sourceDatabaseBuilder = new Database.Builder();
        var sourceEmployees = A.table().withName("employees").withDependency("firstname").withDependency("surname").build();
        var sourceEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar").withParentTable("employees").build();
        var sourceEmployeesSurname = A.column().withName("surname").withDatatype("varchar").withParentTable("employees").build();
        TableBuilder.addTable(sourceDatabaseBuilder, sourceEmployees, sourceEmployeesFirstname, sourceEmployeesSurname);
        var sourceHr = A.table().withName("hr").withDependency("firstname").withDependency("surname").build();
        var sourceHrFirstname = A.column().withName("firstname").withDatatype("varchar").withParentTable("hr").build();
        var sourceHrSurname = A.column().withName("surname").withDatatype("varchar").withParentTable("hr").build();
        TableBuilder.addTable(sourceDatabaseBuilder, sourceHr, sourceHrFirstname, sourceHrSurname);
        var source = sourceDatabaseBuilder.build();

        var targetDatabaseBuilder = new Database.Builder();
        var targetEmployees = A.table().withName("employees").withDependency("firstname").withDependency("surname").build();
        var targetEmployeesFirstname = A.column().withName("firstname").withDatatype("varchar").withParentTable("employees").build();
        var targetEmployeesSurname = A.column().withName("surname").withDatatype("int").withParentTable("employees").build();
        TableBuilder.addTable(targetDatabaseBuilder, targetEmployees, targetEmployeesFirstname, targetEmployeesSurname);
        var targetHr = A.table().withName("hr").withDependency("firstname").withDependency("surname").build();
        var targetHrFirstname = A.column().withName("firstname").withDatatype("varchar").withParentTable("hr").build();
        var targetHrSurname = A.column().withName("surname").withDatatype("varchar").withParentTable("hr").build();
        TableBuilder.addTable(targetDatabaseBuilder, targetHr, targetHrFirstname, targetHrSurname);
        var target = targetDatabaseBuilder.build();

        var differences = Comparer.compare(source, target).collect(Collectors.toList());

        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployees, targetEmployees);
        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
        DifferencesAssert.assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
        DifferencesAssert.assertThat(differences).containsEqual(sourceHr, targetHr);
        DifferencesAssert.assertThat(differences).containsEqual(sourceHrFirstname, targetHrFirstname);
        DifferencesAssert.assertThat(differences).containsEqual(targetHrSurname, targetHrSurname);
    }
}
