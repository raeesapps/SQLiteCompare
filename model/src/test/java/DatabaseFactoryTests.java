import org.junit.Test;

import java.util.concurrent.ExecutionException;

public final class DatabaseFactoryTests {
    @Test
    public void createDatabase_GivenYaml_CorrectlyDeserialisesDatabase() throws ExecutionException, InterruptedException {
        var yaml = """
                objectLists:
                  tables:
                  -
                    dependencies:
                    - {dependencyType: SUBOBJECT, nameOfConsumedObject: firstname}
                    - {dependencyType: SUBOBJECT, nameOfConsumedObject: surname}
                    name: employees
                    objectType: table
                    properties: {}
                  columns:
                  -
                    dependencies: &id001 []
                    name: firstname
                    objectType: column
                    properties: {type: varchar}
                  -
                    dependencies: *id001
                    name: surname
                    objectType: column
                    properties: {type: varchar}
                """;

        var database = DatabaseFactory.createDatabase(yaml);
    }
}
