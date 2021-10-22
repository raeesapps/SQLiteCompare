import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public record Database(ImmutableMap<String, ImmutableList<DatabaseObject>> objectLists) {
    @Override
    public String toString() {
        var representer = new Representer();
        representer.addClassTag(DatabaseObject.class, Tag.MAP);
        representer.addClassTag(ImmutableList.class, Tag.SEQ);
        var yaml = new Yaml(representer);
        return yaml.dumpAs(this, Tag.MAP, DumperOptions.FlowStyle.AUTO);
    }
}
