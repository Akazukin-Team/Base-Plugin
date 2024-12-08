package org.akazukin.library.doma;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CollectorImpl<A, B, R> implements Collector<A, B, R> {

    private final Supplier<B> supplier;

    private final BiConsumer<B, A> accumulator;

    private final BinaryOperator<B> combiner;

    private final Function<B, R> finisher;
    private final Set<Characteristics> characteristics;

    public CollectorImpl(final Supplier<B> supplier, final BiConsumer<B, A> accumulator, final BinaryOperator<B> combiner, final Function<B, R> finisher) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = EnumSet.noneOf(Characteristics.class);
    }

    @Override
    public Supplier<B> supplier() {
        return this.supplier;
    }

    @Override
    public BiConsumer<B, A> accumulator() {
        return this.accumulator;
    }

    @Override
    public BinaryOperator<B> combiner() {
        return this.combiner;
    }

    @Override
    public Function<B, R> finisher() {
        return this.finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return this.characteristics;
    }
}
