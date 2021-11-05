package org.sqlitecompare.comparison;

import org.assertj.core.api.ListAssert;
import org.sqlitecompare.model.DatabaseObject;

import java.util.List;
import java.util.function.Predicate;

public final class DifferencesAssert extends ListAssert<Difference> {

    public static DifferencesAssert assertThat(List<Difference> differences) {
        return new DifferencesAssert(differences);
    }

    private DifferencesAssert(List<Difference> actual) {
        super(actual);
    }

    public DifferencesAssert containsOnlyInSource(DatabaseObject source) {
        isNotNull();
        isNotEmpty();

        var getDifference = getDifferenceContainingSource(source);
        var maybeDifference = actual.stream().filter(getDifference).findFirst();
        if (maybeDifference.isEmpty()) {
            failWithMessage("There is not a difference object containing a reference to %s", source.name());
        }

        var difference = maybeDifference.get();
        if (!difference.differenceType().equals(DifferenceType.ONLY_IN_SOURCE)) {
            failWithMessage("The difference between %s is not an only in source difference", source.name());
        }

        return this;
    }

    private static Predicate<Difference> getDifferenceContainingSource(DatabaseObject source) {
        return x -> x.source().isPresent()
                && x.source().get().uniqueIdentifier().equals(source.uniqueIdentifier());
    }

    public DifferencesAssert containsOnlyInTarget(DatabaseObject target) {
        isNotNull();
        isNotEmpty();

        var getDifference = getDifferenceContainingTarget(target);
        var maybeDifference = actual.stream().filter(getDifference).findFirst();
        if (maybeDifference.isEmpty()) {
            failWithMessage("There is not a difference object containing a reference to %s", target.name());
        }

        var difference = maybeDifference.get();
        if (!difference.differenceType().equals(DifferenceType.ONLY_IN_TARGET)) {
            failWithMessage("The difference between %s is not an only in target difference", target.name());
        }

        return this;
    }

    private static Predicate<Difference> getDifferenceContainingTarget(DatabaseObject target) {
        return x -> x.target().isPresent()
                && x.target().get().uniqueIdentifier().equals(target.uniqueIdentifier());
    }

    public DifferencesAssert containsEqual(DatabaseObject source, DatabaseObject target) {
        isNotNull();
        isNotEmpty();

        var getDifference = getDifferenceContainingSourceAndTarget(source, target);
        var maybeDifference = actual.stream().filter(getDifference).findFirst();
        if (maybeDifference.isEmpty()) {
            failWithMessage("There is not a difference object containing a reference to both %s and %s", source.name(), target.name());
        }

        var difference = maybeDifference.get();
        if (!difference.differenceType().equals(DifferenceType.EQUAL)) {
            failWithMessage("The difference between %s and %s is not an equal difference", source.name(), target.name());
        }

        return this;
    }

    public DifferencesAssert containsDifferent(DatabaseObject source, DatabaseObject target) {
        isNotNull();
        isNotEmpty();

        var getDifference = getDifferenceContainingSourceAndTarget(source, target);
        var maybeDifference = actual.stream().filter(getDifference).findFirst();
        if (maybeDifference.isEmpty()) {
            failWithMessage("There is not a difference object containing a reference to both %s and %s", source.name(), target.name());
        }

        var difference = maybeDifference.get();
        if (!difference.differenceType().equals(DifferenceType.DIFFERENT)) {
            failWithMessage("The difference between %s and %s is not a different difference", source.name(), target.name());
        }

        return this;
    }

    private static Predicate<Difference> getDifferenceContainingSourceAndTarget(DatabaseObject source, DatabaseObject target) {
        return x -> x.source().isPresent()
                && x.target().isPresent()
                && x.source().get().uniqueIdentifier().equals(source.uniqueIdentifier())
                && x.target().get().uniqueIdentifier().equals(target.uniqueIdentifier());
    }
}
