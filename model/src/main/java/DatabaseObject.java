import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public record DatabaseObject(String fullyQualifiedName, String objectType, ImmutableMap<String, Object> properties, ImmutableSet<Dependency> dependencies) {
}
