import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseTests {
    @Test
    public void database_ToString_ProducesValidYaml() {
        var databaseObjects = new ImmutableMap.Builder<String, ImmutableList<DatabaseObject>>()
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
                                .build()).build());
        var database = new Database(databaseObjects.build());

        var serialisation = database.toString();

        Assert.assertEquals("""
                !!Database
                objectLists:
                  tables:
                  - !!DatabaseObject
                    dependencies:
                    - {dependencyType: SUBOBJECT, nameOfConsumedObject: firstname}
                    - {dependencyType: SUBOBJECT, nameOfConsumedObject: surname}
                    name: employees
                    objectType: table
                    properties: {}
                  columns:
                  - !!DatabaseObject
                    dependencies: &id001 []
                    name: firstname
                    objectType: column
                    properties: {type: varchar}
                  - !!DatabaseObject
                    dependencies: *id001
                    name: surname
                    objectType: column
                    properties: {type: varchar}
                """, serialisation);
    }
}
