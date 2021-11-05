import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.javatuples.Pair;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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

        var sourceTargetIntersection = sourceObjectKeySet
                .parallelStream()
                .filter(targetObjectKeySet::contains)
                .collect(new ImmutableSetCollector<>());

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
