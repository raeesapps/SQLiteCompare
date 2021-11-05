import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

public class ComparerTests {

    @Test
    public void surname_datatype_different() {
        var source = new Database
                .Builder()
                .object("tables", new DatabaseObject
                        .Builder()
                        .name("employees")
                        .objectType("table")
                        .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                        .dependency(new Dependency(DependencyType.SUBOBJECT, "surname")))
                .object("columns", new DatabaseObject
                        .Builder()
                        .name("firstname")
                        .objectType("column")
                        .property("type", "varchar")
                        .property("parent", "employees"))
                .object("columns", new DatabaseObject
                        .Builder()
                        .name("surname")
                        .objectType("column")
                        .property("type", "varchar")
                        .property("parent", "employees"))
                .build();

        var target = new Database
                .Builder()
                .object("tables", new DatabaseObject
                        .Builder()
                        .name("employees")
                        .objectType("table")
                        .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                        .dependency(new Dependency(DependencyType.SUBOBJECT, "surname")))
                .object("columns", new DatabaseObject
                        .Builder()
                        .name("firstname")
                        .objectType("column")
                        .property("type", "varchar")
                        .property("parent", "employees"))
                .object("columns", new DatabaseObject
                        .Builder()
                        .name("surname")
                        .objectType("column")
                        .property("type", "int")
                        .property("parent", "employees"))
                .build();

        var differences = Comparer.compare(source, target).collect(Collectors.toList());
        Assert.assertEquals(differences.size(), 3);
    }
}
