import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Tag;

public record Database(ImmutableMap<String, ImmutableList<DatabaseObject>> objectLists) {
    @Override
    public String toString() {
        var yaml = YamlHelper.newYaml();
        return yaml.dumpAs(this, Tag.MAP, DumperOptions.FlowStyle.AUTO);
    }
}
