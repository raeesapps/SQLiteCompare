import com.google.common.collect.ImmutableList;

public record Dependency(DependencyType dependencyType, String fullyQualifiedNameOfConsumedObject, ImmutableList<String> propertiesRequiringRebuild) {
}
