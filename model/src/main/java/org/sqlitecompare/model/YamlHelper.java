package org.sqlitecompare.model;

import com.google.common.collect.ImmutableList;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public final class YamlHelper {
    public static Yaml newYaml() {
        var representer = new Representer();
        representer.addClassTag(DatabaseObject.class, Tag.MAP);
        representer.addClassTag(ImmutableList.class, Tag.SEQ);
        return new Yaml(representer);
    }
}
