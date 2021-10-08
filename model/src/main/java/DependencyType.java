public enum DependencyType {
    SUBOBJECT(0),
    REQUIRES(1);

    private int id;

    DependencyType(int id) {
        this.id = id;
    }

    public static DependencyType parse(String dependencyType) {
        return DependencyType.valueOf(dependencyType);
    }
}
