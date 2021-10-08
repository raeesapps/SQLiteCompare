import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public final class DatabaseFactory {
    private static final String CLASS_INITIALISATION_EXCEPTION_MESSAGE = "DatabaseFactory cannot be initialised!";

    private static final WorkScheduler workScheduler = new WorkScheduler();
    private static final Yaml yaml = new Yaml();

    private DatabaseFactory() throws Exception {
        throw new Exception(CLASS_INITIALISATION_EXCEPTION_MESSAGE);
    }

    public static Database createDatabase(final String databaseYaml) throws ExecutionException, InterruptedException {
        var deserialiseDatabaseAction = new Callable<Database>() {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public Database call() {
                var databaseProperties = (Map<String, Object>) yaml.load(databaseYaml);
                var objectLists = (Map<String, ArrayList>) databaseProperties.get("objectLists");

                var objectListsBuilder = new ImmutableMap.Builder<String, ImmutableList<DatabaseObject>>();
                for (var currentType : objectLists.keySet()) {
                    var objectsOfCurrentType = (ArrayList) objectLists.get(currentType);
                    var deserialisedObjects = new ImmutableList.Builder<DatabaseObject>();
                    for (var currentObject : objectsOfCurrentType) {
                        var currentObjectAsMap = (Map) currentObject;

                        var deserialisedObjectBuilder = new DatabaseObject
                                .Builder()
                                .name((String) currentObjectAsMap.get("name"))
                                .objectType((String) currentObjectAsMap.get("objectType"));

                        var properties = (Map) currentObjectAsMap.get("properties");
                        for (var propertyKey : properties.keySet()) {
                            deserialisedObjectBuilder.property((String) propertyKey, properties.get(propertyKey));
                        }

                        var dependencies = (List) currentObjectAsMap.get("dependencies");
                        for (var dependencyObject : dependencies) {
                            var dependencyMap = (Map) dependencyObject;
                            deserialisedObjectBuilder.dependency(new Dependency(DependencyType.valueOf((String) dependencyMap.get("dependencyType")), (String) dependencyMap.get("nameOfConsumedObject")));
                        }

                        deserialisedObjects.add(deserialisedObjectBuilder.build());
                    }

                    objectListsBuilder.put(currentType, deserialisedObjects.build());
                }

                return new Database(objectListsBuilder.build());
            }
        };

        var deserialiseDatabaseTask = workScheduler.submitWork(deserialiseDatabaseAction);
        return deserialiseDatabaseTask.get();
    }
}
