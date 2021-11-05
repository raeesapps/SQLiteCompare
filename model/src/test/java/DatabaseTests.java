import org.junit.Assert;
import org.junit.Test;

public class DatabaseTests {
    @Test
    public void database_ToString_ProducesValidYaml() {
        var database = new Database
                .Builder()
                .object("tables", new DatabaseObject
                        .Builder()
                        .name("employees")
                        .objectType("table")
                        .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                        .dependency(new Dependency(DependencyType.SUBOBJECT, "surname")).build())
                .object("columns", new DatabaseObject
                        .Builder()
                        .name("firstname")
                        .objectType("column")
                        .property("type", "varchar")
                        .property("parent", "employees").build())
                .object("columns", new DatabaseObject
                        .Builder()
                        .name("surname")
                        .objectType("column")
                        .property("type", "varchar")
                        .property("parent", "employees").build())
                .build();

        var serialisation = database.toString();

        Assert.assertEquals("""
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
                    properties: {type: varchar, parent: employees}
                  - dependencies: *id001
                    name: surname
                    objectType: column
                    properties: {type: varchar, parent: employees}
                                """, serialisation);
    }
}
