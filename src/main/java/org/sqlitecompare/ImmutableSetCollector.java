package org.sqlitecompare;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ImmutableSetCollector<T> implements
    Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {
  @Override
  public Supplier<ImmutableSet.Builder<T>> supplier() {
    return ImmutableSet.Builder::new;
  }

  @Override
  public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
    return (b, e) -> b.add(e);
  }

  @Override
  public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
    return (b1, b2) -> b1.addAll(b2.build());
  }

  @Override
  public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
    return ImmutableSet.Builder::build;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return ImmutableSet.of();
  }
}
