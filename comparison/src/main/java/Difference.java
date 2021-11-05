public record Difference(DifferenceType differenceType, DatabaseObject source, DatabaseObject target, Iterable<String> propertiesChanged) {
}
