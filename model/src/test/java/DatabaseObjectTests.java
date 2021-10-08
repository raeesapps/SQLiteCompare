import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public final class DatabaseObjectTests {
    @Test
    public void databaseObject_ToString_ProducesValidYaml() {
        var yaml = new Yaml();
        var employees = new DatabaseObject
                .Builder()
                .name("employees")
                .objectType("table")
                .dependency(new Dependency(DependencyType.SUBOBJECT, "firstname"))
                .dependency(new Dependency(DependencyType.SUBOBJECT, "surname"))
                .build();

        var serialisationOfEmployees = yaml.dump(employees);

        Assert.assertEquals("""
                !!DatabaseObject
                dependencies:
                - {dependencyType: SUBOBJECT, nameOfConsumedObject: firstname}
                - {dependencyType: SUBOBJECT, nameOfConsumedObject: surname}
                name: employees
                objectType: table
                properties: {}
                                """, serialisationOfEmployees);
    }
}
