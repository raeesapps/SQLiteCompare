package org.sqlitecompare.model;

public record Dependency(DependencyType dependencyType, String nameOfConsumedObject) {
}
