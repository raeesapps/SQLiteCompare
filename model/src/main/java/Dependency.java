import com.google.common.collect.ImmutableSet;

public record Dependency(DependencyType dependencyType, String fullyQualifiedNameOfConsumedObject, ImmutableSet<String> propertiesRequiringRebuild) {
}
