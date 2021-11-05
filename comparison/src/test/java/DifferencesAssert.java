import org.assertj.core.api.ListAssert;

import java.util.List;
import java.util.function.Predicate;

public final class DifferencesAssert extends ListAssert<Difference> {

    public static DifferencesAssert assertThat(List<Difference> differences) {
        return new DifferencesAssert(differences);
    }

    private DifferencesAssert(List<Difference> actual) {
        super(actual);
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
