import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;

public record Database(ImmutableMap<String, ImmutableList<DatabaseObject>> objectLists) {

}
