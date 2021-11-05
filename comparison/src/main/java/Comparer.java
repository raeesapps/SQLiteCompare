import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.javatuples.Pair;

import java.util.Collection;
import java.util.stream.Stream;

public final class Comparer {
    public static Iterable<Difference> compare(Database source, Database target) {
        var sourceKeySet = source.objectLists().keySet();
        var targetKeySet = target.objectLists().keySet();

        var objectTypesInBoth = Stream.concat(
                sourceKeySet.parallelStream(),
                targetKeySet.parallelStream()
        );

       return objectTypesInBoth
               .map(objectType -> compare(source, target, sourceKeySet, targetKeySet, objectType))
               .flatMap(Collection::stream)
               .collect(new ImmutableListCollector<>());
    }

    private static ImmutableList<Difference> compare(Database source, Database target, ImmutableSet<String> sourceKeySet, ImmutableSet<String> targetKeySet, String objectType) {
        if (sourceKeySet.contains(objectType) && targetKeySet.contains(objectType)) {
            return differentOrEqual(source.objectLists().get(objectType), target.objectLists().get(objectType));
        } else if (sourceKeySet.contains(objectType)) {
            return onlyInSource(source.objectLists().get(objectType));
        } else {
            return onlyInTarget(target.objectLists().get(objectType));
        }
    }

    private static ImmutableList<Difference> differentOrEqual(ImmutableList<DatabaseObject> sourceObjects, ImmutableList<DatabaseObject> targetObjects) {
        var sourceObjectMapping = sourceObjects.parallelStream().map(x -> Pair.with(x.toString(), x)).collect(new ImmutableMapCollector<>());
        var targetObjectMapping = targetObjects.parallelStream().map(x -> Pair.with(x.toString(), x)).collect(new ImmutableMapCollector<>());

        var sourceObjectKeySet = sourceObjectMapping.keySet();
        var targetObjectKeySet = targetObjectMapping.keySet();

        var sourceTargetIntersection = differentOrEqual(sourceObjectKeySet
                .parallelStream()
                .filter(targetObjectKeySet::contains)
                .collect(new ImmutableSetCollector<>()), sourceObjectMapping, targetObjectMapping);

        var sourceTargetUnion = Stream.concat(
                        sourceObjectKeySet.parallelStream(),
                        targetObjectKeySet.parallelStream())
                .collect(new ImmutableSetCollector<>());
        var sourceExceptTarget = onlyInSource(sourceTargetUnion
                .parallelStream()
                .filter(object -> !targetObjectKeySet.contains(object))
                .map(object -> sourceObjectMapping.get(object))
                .collect(new ImmutableSetCollector<>()));
        var targetExceptSource = onlyInTarget(sourceTargetUnion
                .parallelStream()
                .filter(object -> !sourceObjects.contains(object))
                .map(object -> targetObjectMapping.get(object))
                .collect(new ImmutableSetCollector<>()));

        return null;
    }

    private static ImmutableList<Difference> differentOrEqual(ImmutableSet<String> objectKeys, ImmutableMap<String, DatabaseObject> sourceObjects, ImmutableMap<String, DatabaseObject> targetObjects)
    {
        var objectKeyStream = objectKeys.stream().parallel();

        var equalObjects = objectKeyStream
                .filter(objectKey -> sourceObjects.get(objectKey).equals(targetObjects.get(objectKey)))
                .map(objectKey -> equal(sourceObjects.get(objectKey), targetObjects.get(objectKey)));
        var differentObjects =  objectKeyStream
                .filter(objectKey -> !sourceObjects.get(objectKey).equals(targetObjects.get(objectKey)))
                .map(objectKey -> different(sourceObjects.get(objectKey), targetObjects.get(objectKey)));

        var both = Stream.concat(equalObjects, differentObjects);
        return both.collect(new ImmutableListCollector<>());
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

    private static Difference equal(DatabaseObject source, DatabaseObject target)
    {
        return new Difference(DifferenceType.EQUAL, source, target, ImmutableList.of());
    }

    private static ImmutableList<Difference> onlyInSource(ImmutableCollection<DatabaseObject> sourceObjects) {
        return sourceObjects
                .parallelStream()
                .map(x -> new Difference(DifferenceType.ONLY_IN_SOURCE, x, null, null))
                .collect(new ImmutableListCollector<>());
    }

    private static ImmutableList<Difference> onlyInTarget(ImmutableCollection<DatabaseObject> targetObjects) {
        return targetObjects
                .parallelStream()
                .map(x -> new Difference(DifferenceType.ONLY_IN_TARGET, null, x, null))
                .collect(new ImmutableListCollector<>());
    }
}
