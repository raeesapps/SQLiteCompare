import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.Yaml;

public record DatabaseObject(String fullyQualifiedName, String objectType, ImmutableMap<String, Object> properties, ImmutableList<Dependency> dependencies) {
    @Override
    public String toString() {
        var yaml = new Yaml();
        return yaml.dump(this);
    }
}
