package org.sqlitecompare.comparison;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.sqlitecompare.ImmutableListCollector;
import org.sqlitecompare.ImmutableMapCollector;
import org.sqlitecompare.model.Database;
import org.sqlitecompare.model.DatabaseObject;

import java.util.Optional;
import java.util.stream.Stream;

public final class Comparer {
  public static Stream<Difference> compare(Database source, Database target) {
    var sourceKeySet = source.objectLists().keySet();
    var targetKeySet = target.objectLists().keySet();

    var objectTypesInBoth = Sets.union(sourceKeySet, targetKeySet).parallelStream();

    return objectTypesInBoth.flatMap(
        objectType -> compare(source, target, sourceKeySet, targetKeySet, objectType));
  }

  private static Stream<Difference> compare(
      Database source, Database target, ImmutableSet<String> sourceKeySet,
      ImmutableSet<String> targetKeySet, String objectType) {
    if (sourceKeySet.contains(objectType) && targetKeySet.contains(objectType)) {
      return differentOrEqual(
          source.objectLists().get(objectType), target.objectLists().get(objectType));
    } else if (sourceKeySet.contains(objectType)) {
      return onlyInSource(source.objectLists().get(objectType).parallelStream());
    } else {
      return onlyInTarget(target.objectLists().get(objectType).parallelStream());
    }
  }

  private static Stream<Difference> differentOrEqual(
      ImmutableList<DatabaseObject> sourceObjects, ImmutableList<DatabaseObject> targetObjects) {
    var sourceObjectMapping = sourceObjects.parallelStream().map(x -> Pair.with(x.uniqueIdentifier(), x))
        .collect(new ImmutableMapCollector<>());
    var targetObjectMapping = targetObjects.parallelStream().map(x -> Pair.with(x.uniqueIdentifier(), x))
        .collect(new ImmutableMapCollector<>());

    var sourceObjectKeySet = sourceObjectMapping.keySet();
    var targetObjectKeySet = targetObjectMapping.keySet();

    var sourceTargetIntersection = Sets
        .intersection(sourceObjectKeySet, targetObjectKeySet)
        .immutableCopy();

    var objectsInBoth = differentOrEqual(
        sourceTargetIntersection, sourceObjectMapping, targetObjectMapping);

    var objectKeysInSourceButNotTarget = Sets
        .difference(sourceObjectKeySet, targetObjectKeySet)
        .immutableCopy();
    var sourceExceptTarget = onlyInSource(objectKeysInSourceButNotTarget
        .parallelStream()
        .map(sourceObjectMapping::get));

    var objectKeysInTargetButNotSource = Sets
        .difference(targetObjectKeySet, sourceObjectKeySet)
        .immutableCopy();
    var targetExceptSource = onlyInTarget(objectKeysInTargetButNotSource
        .parallelStream()
        .map(targetObjectMapping::get));

    return Stream.concat(objectsInBoth, Stream.concat(targetExceptSource, sourceExceptTarget));
  }

  private static Stream<Difference> differentOrEqual(
      ImmutableSet<String> objectKeys, ImmutableMap<String, DatabaseObject> sourceObjects,
      ImmutableMap<String, DatabaseObject> targetObjects) {
    var equalObjects = objectKeys
        .parallelStream()
        .filter(objectKey -> sourceObjects.get(objectKey).equals(targetObjects.get(objectKey)))
        .map(objectKey -> equal(sourceObjects.get(objectKey), targetObjects.get(objectKey)));

    var differentObjects = objectKeys
        .parallelStream()
        .filter(objectKey -> !sourceObjects.get(objectKey).equals(targetObjects.get(objectKey)))
        .map(objectKey -> different(sourceObjects.get(objectKey), targetObjects.get(objectKey)));

    return Stream.concat(equalObjects, differentObjects);
  }

  private static Difference different(DatabaseObject source, DatabaseObject target) {
    // This code assumes source properties has the same key set as target properties.
    var sourceProperties = source.properties();
    var sourcePropertiesKeySet = sourceProperties.keySet();

    var targetProperties = target.properties();

    var propertiesChanged = sourcePropertiesKeySet
        .parallelStream()
        .filter(key -> !sourceProperties.get(key).equals(targetProperties.get(key)))
        .collect(new ImmutableListCollector<>());

    return new Difference(
        DifferenceType.DIFFERENT, Optional.of(source), Optional.of(target), propertiesChanged);
  }

  private static Difference equal(DatabaseObject source, DatabaseObject target) {
    return new Difference(
        DifferenceType.EQUAL, Optional.of(source), Optional.of(target), ImmutableList.of());
  }

  private static Stream<Difference> onlyInSource(Stream<DatabaseObject> sourceObjects) {
    return sourceObjects.map(
        sourceObject -> new Difference(DifferenceType.ONLY_IN_SOURCE, Optional.of(sourceObject),
            Optional.empty(), ImmutableList.of()));
  }

  private static Stream<Difference> onlyInTarget(Stream<DatabaseObject> targetObjects) {
    return targetObjects.map(
        targetObject -> new Difference(DifferenceType.ONLY_IN_TARGET, Optional.empty(),
            Optional.of(targetObject), ImmutableList.of()));
  }
}
