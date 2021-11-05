package org.sqlitecompare.comparison;

import org.sqlitecompare.model.DatabaseObject;

import java.util.Optional;

public record Difference(DifferenceType differenceType, Optional<DatabaseObject> source, Optional<DatabaseObject> target, Iterable<String> propertiesChanged) {
}
