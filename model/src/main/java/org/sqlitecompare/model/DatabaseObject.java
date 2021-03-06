package org.sqlitecompare.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Tag;

public record DatabaseObject(String name, String objectType,
                             ImmutableMap<String, Object> properties,
                             ImmutableList<Dependency> dependencies) {
  public String uniqueIdentifier() {
    return "Name: " + name + "; ObjectType: " + objectType + "; Parent: "
        + (properties.getOrDefault("parent", "top-level object"));
  }

  @Override
  public String toString() {
    var yaml = YamlHelper.newYaml();
    return yaml.dumpAs(this, Tag.MAP, DumperOptions.FlowStyle.AUTO);
  }

  public static class Builder {
    private final ImmutableMap.Builder<String, Object> properties = new ImmutableMap.Builder<>();
    private final ImmutableList.Builder<Dependency> dependencies = new ImmutableList.Builder<>();
    private String name;
    private String objectType;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder objectType(String objectType) {
      this.objectType = objectType;
      return this;
    }

    public Builder property(String name, Object value) {
      properties.put(name, value);
      return this;
    }

    public Builder dependency(Dependency dependency) {
      dependencies.add(dependency);
      return this;
    }

    public DatabaseObject build() {
      return new DatabaseObject(name, objectType, properties.build(), dependencies.build());
    }
  }
}
