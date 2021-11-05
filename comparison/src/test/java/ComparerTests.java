import org.junit.Test;

import java.util.stream.Collectors;

public class ComparerTests {

    @Test
    public void surname_datatype_different() {
        var sourceEmployees = new DatabaseObject
                .Builder()
                .name("employees")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();
        var sourceEmployeesFirstname = new DatabaseObject
                .Builder()
                .name("firstname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "employees")
                .build();
        var sourceEmployeesSurname = new DatabaseObject
                .Builder()
                .name("surname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "employees")
                .build();
        var source = new Database
                .Builder()
                .object("tables", sourceEmployees)
                .object("columns", sourceEmployeesFirstname)
                .object("columns", sourceEmployeesSurname)
                .build();

        var targetEmployees = new DatabaseObject
                .Builder()
                .name("employees")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();
        var targetEmployeesFirstname = new DatabaseObject
                .Builder()
                .name("firstname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "employees")
                .build();
        var targetEmployeesSurname = new DatabaseObject
                .Builder()
                .name("surname")
                .objectType("column")
                .property("type", "int")
                .property("parent", "employees")
                .build();
        var target = new Database
                .Builder()
                .object("tables", targetEmployees)
                .object("columns", targetEmployeesFirstname)
                .object("columns", targetEmployeesSurname)
                .build();

        var differences = Comparer.compare(source, target).collect(Collectors.toList());
        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployees, targetEmployees);
        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
        DifferencesAssert.assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
    }

    @Test
    public void surname_datatype_different_in_employees_but_equal_in_hr() {
        var sourceEmployees = new DatabaseObject
                .Builder()
                .name("employees")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();
        var sourceEmployeesFirstname = new DatabaseObject
                .Builder()
                .name("firstname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "employees")
                .build();
        var sourceEmployeesSurname = new DatabaseObject
                .Builder()
                .name("surname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "employees")
                .build();
        var sourceHr = new DatabaseObject
                .Builder()
                .name("hr")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();
        var sourceHrFirstname = new DatabaseObject
                .Builder()
                .name("firstname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "hr")
                .build();
        var sourceHrSurname = new DatabaseObject
                .Builder()
                .name("surname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "hr")
                .build();
        var source = new Database
                .Builder()
                .object("tables", sourceEmployees)
                .object("tables", sourceHr)
                .object("columns", sourceEmployeesFirstname)
                .object("columns", sourceEmployeesSurname)
                .object("columns", sourceHrFirstname)
                .object("columns", sourceHrSurname)
                .build();

        var targetEmployees = new DatabaseObject
                .Builder()
                .name("employees")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();
        var targetEmployeesFirstname = new DatabaseObject
                .Builder()
                .name("firstname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "employees")
                .build();
        var targetEmployeesSurname = new DatabaseObject
                .Builder()
                .name("surname")
                .objectType("column")
                .property("type", "int")
                .property("parent", "employees")
                .build();
        var targetHr = new DatabaseObject
                .Builder()
                .name("hr")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();
        var targetHrFirstname = new DatabaseObject
                .Builder()
                .name("firstname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "hr")
                .build();
        var targetHrSurname = new DatabaseObject
                .Builder()
                .name("surname")
                .objectType("column")
                .property("type", "varchar")
                .property("parent", "hr")
                .build();
        var target = new Database
                .Builder()
                .object("tables", targetEmployees)
                .object("tables", targetHr)
                .object("columns", targetEmployeesFirstname)
                .object("columns", targetEmployeesSurname)
                .object("columns", targetHrFirstname)
                .object("columns", targetHrSurname)
                .build();

        var differences = Comparer.compare(source, target).collect(Collectors.toList());
        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployees, targetEmployees);
        DifferencesAssert.assertThat(differences).containsEqual(sourceEmployeesFirstname, targetEmployeesFirstname);
        DifferencesAssert.assertThat(differences).containsDifferent(sourceEmployeesSurname, targetEmployeesSurname);
        DifferencesAssert.assertThat(differences).containsEqual(sourceHr, targetHr);
        DifferencesAssert.assertThat(differences).containsEqual(sourceHrFirstname, targetHrFirstname);
        DifferencesAssert.assertThat(differences).containsEqual(targetHrSurname, targetHrSurname);
    }
}
