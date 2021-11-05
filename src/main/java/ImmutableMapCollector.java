import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.javatuples.Pair;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ImmutableMapCollector<K, V> implements Collector<Pair<K, V>, ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> {
    @Override
    public Supplier<ImmutableMap.Builder<K, V>> supplier() {
        return ImmutableMap.Builder::new;
    }

    @Override
    public BiConsumer<ImmutableMap.Builder<K, V>, Pair<K, V>> accumulator() {
        return (mapBuilder, pair) -> mapBuilder.put(pair.getValue0(), pair.getValue1());
    }

    @Override
    public BinaryOperator<ImmutableMap.Builder<K, V>> combiner() {
        return (x, y) -> x.putAll(y.build());
    }

    @Override
    public Function<ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> finisher() {
        return ImmutableMap.Builder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of();
    }
}