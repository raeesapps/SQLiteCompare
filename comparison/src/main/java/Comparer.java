import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.javatuples.Pair;

import java.util.stream.Stream;

public final class Comparer {
    public static Stream<Difference> compare(Database source, Database target) {
        var sourceKeySet = source.objectLists().keySet();
        var targetKeySet = target.objectLists().keySet();

        var objectTypesInBoth = Stream.concat(
                sourceKeySet.parallelStream(),
                targetKeySet.parallelStream()
        );

       return objectTypesInBoth.flatMap(objectType -> compare(source, target, sourceKeySet, targetKeySet, objectType));
    }

    private static Stream<Difference> compare(Database source, Database target, ImmutableSet<String> sourceKeySet, ImmutableSet<String> targetKeySet, String objectType) {
        if (sourceKeySet.contains(objectType) && targetKeySet.contains(objectType)) {
            return differentOrEqual(source.objectLists().get(objectType), target.objectLists().get(objectType));
        } else if (sourceKeySet.contains(objectType)) {
            return onlyInSource(source.objectLists().get(objectType).parallelStream());
        } else {
            return onlyInTarget(target.objectLists().get(objectType).parallelStream());
        }
    }

    private static Stream<Difference> differentOrEqual(ImmutableList<DatabaseObject> sourceObjects, ImmutableList<DatabaseObject> targetObjects) {
        var sourceObjectMapping = sourceObjects.parallelStream().map(x -> Pair.with(x.toString(), x)).collect(new ImmutableMapCollector<>());
        var targetObjectMapping = targetObjects.parallelStream().map(x -> Pair.with(x.toString(), x)).collect(new ImmutableMapCollector<>());

        var sourceObjectKeySet = sourceObjectMapping.keySet();
        var targetObjectKeySet = targetObjectMapping.keySet();

        var sourceTargetIntersection = differentOrEqual(sourceObjectKeySet
                .parallelStream()
                .filter(targetObjectKeySet::contains), sourceObjectMapping, targetObjectMapping);

        var sourceTargetUnion = Stream.concat(
                        sourceObjectKeySet.parallelStream(),
                        targetObjectKeySet.parallelStream())
                .collect(new ImmutableSetCollector<>());
        var sourceExceptTarget = onlyInSource(sourceTargetUnion
                .parallelStream()
                .filter(object -> !targetObjectKeySet.contains(object))
                .map(object -> sourceObjectMapping.get(object)));
        var targetExceptSource = onlyInTarget(sourceTargetUnion
                .parallelStream()
                .filter(object -> !sourceObjects.contains(object))
                .map(object -> targetObjectMapping.get(object)));

        return Stream.concat(sourceTargetIntersection, Stream.concat(sourceExceptTarget, targetExceptSource));
    }

    private static Stream<Difference> differentOrEqual(Stream<String> objectKeys, ImmutableMap<String, DatabaseObject> sourceObjects, ImmutableMap<String, DatabaseObject> targetObjects)
    {
        var equalObjects = objectKeys
                .filter(objectKey -> sourceObjects.get(objectKey).equals(targetObjects.get(objectKey)))
                .map(objectKey -> equal(sourceObjects.get(objectKey), targetObjects.get(objectKey)));
        var differentObjects =  objectKeys
                .filter(objectKey -> !sourceObjects.get(objectKey).equals(targetObjects.get(objectKey)))
                .map(objectKey -> different(sourceObjects.get(objectKey), targetObjects.get(objectKey)));

        return Stream.concat(equalObjects, differentObjects);
    }

    private static Difference different(DatabaseObject source, DatabaseObject target)
    {
        // Please note that this method assumes source and target properties have the same key set.

        var sourceProperties = source.properties();
        var sourcePropertiesKeySet = sourceProperties.keySet();

        var targetProperties = target.properties();
        var targetPropertiesKeySet = targetProperties.keySet();

        /*
        if (!sourcePropertiesKeySet.containsAll(targetPropertiesKeySet))
        {
            throw new Exception();
        }*/
        var propertiesChanged = sourcePropertiesKeySet
                .stream()
                .parallel()
                .filter(key -> !sourceProperties.get(key).equals(targetProperties.get(key)))
                .collect(new ImmutableListCollector<>());

        return new Difference(DifferenceType.DIFFERENT, source, target, propertiesChanged);
    }

    private static Difference equal(DatabaseObject source, DatabaseObject target) {
        return new Difference(DifferenceType.EQUAL, source, target, ImmutableList.of());
    }

    private static Stream<Difference> onlyInSource(Stream<DatabaseObject> sourceObjects) {
        return sourceObjects.map(x -> new Difference(DifferenceType.ONLY_IN_SOURCE, x, null, null));
    }

    private static Stream<Difference> onlyInTarget(Stream<DatabaseObject> targetObjects) {
        return targetObjects.map(x -> new Difference(DifferenceType.ONLY_IN_TARGET, null, x, null));
    }
}
