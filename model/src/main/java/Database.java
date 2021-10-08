import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import org.yaml.snakeyaml.Yaml;

public record Database(ImmutableMap<String, ImmutableList<DatabaseObject>> objectLists) {
    @Override
    public String toString() {
        var yaml = new Yaml();
        return yaml.dump(this);
    }
}
