package org.sqlitecompare.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.javatuples.Pair;
import org.sqlitecompare.ImmutableMapCollector;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.HashMap;
import java.util.Map;

public record Database(ImmutableMap<String, ImmutableList<DatabaseObject>> objectLists) {
    @Override
    public String toString() {
        var yaml = YamlHelper.newYaml();
        return yaml.dumpAs(this, Tag.MAP, DumperOptions.FlowStyle.AUTO);
    }

    public static class Builder {
        private final Map<String, ImmutableList.Builder<DatabaseObject>> objectListsBuilder = new HashMap<>();

        public Builder object(String objectGroup, DatabaseObject object) {
            if (objectListsBuilder.containsKey(objectGroup)) {
                var objectListBuilder = objectListsBuilder.get(objectGroup);
                objectListBuilder.add(object);
            } else {
                objectListsBuilder.put(objectGroup, new ImmutableList.Builder<DatabaseObject>().add(object));
            }

            return this;
        }

        public Database build() {
            var objectListsKeySet = objectListsBuilder.keySet();
            var objectLists = objectListsKeySet
                    .stream()
                    .map(objectType -> Pair.with(objectType, objectListsBuilder.get(objectType).build()))
                    .collect(new ImmutableMapCollector<>());
            return new Database(objectLists);
        }
    }
}
