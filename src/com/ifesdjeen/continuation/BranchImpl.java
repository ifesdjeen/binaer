package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BranchImpl<CURRENT, PREVIOUS, END> implements Branch<CURRENT, PREVIOUS, END> {

  private static class Tuple<END> {
    private final Function<ByteBuf, END> prev;
    private final Predicate<END>         predicate;

    private Tuple(Function<ByteBuf, END> prev, Predicate<END> predicate) {
      this.prev = prev;
      this.predicate = predicate;
    }

  }

  private List<Tuple<PREVIOUS>> predicates;
  private Function<ByteBuf, PREVIOUS> prev;
  private Function<ByteBuf, CURRENT> parentContinuation;

  public BranchImpl(List<Tuple<PREVIOUS>> predicates,
                    Function<ByteBuf, PREVIOUS> prev,
                    Function<ByteBuf, CURRENT> parentContinuation) {
    this.predicates = new LinkedList<>();
    this.prev = prev;
    this.parentContinuation = parentContinuation;
  }

  public BranchImpl(Function<ByteBuf, PREVIOUS> prev,
                    Predicate<PREVIOUS> predicate,
                    Function<ByteBuf, CURRENT> parentContinuation) {
    this.prev = prev;
    this.predicates = new LinkedList<>();
    this.predicates.add(new Tuple<PREVIOUS>(prev, predicate));
    this.parentContinuation = parentContinuation;
  }

  public BranchImpl(List<Tuple<PREVIOUS>> predicates,
                    Function<ByteBuf, PREVIOUS> prev,
                    Predicate<PREVIOUS> predicate,
                    Function<ByteBuf, CURRENT> parentContinuation) {
    this.predicates = predicates;
    this.prev = prev;
    this.predicates.add(new Tuple<PREVIOUS>(prev, predicate));
    this.parentContinuation = parentContinuation;
  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readByte(BiFunction<CURRENT, Byte, T> continuation) {
    return new BranchImpl<>(predicates,
                            prev,
                            (ByteBuf byteBuf) -> {
                              CURRENT c = parentContinuation.apply(byteBuf);
                              return continuation.apply(c, byteBuf.readByte());
                            });
  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BranchImpl<>(predicates,
                            prev,
                            (ByteBuf byteBuf) -> {
                              CURRENT c = parentContinuation.apply(byteBuf);
                              return continuation.apply(c, byteBuf.readInt());
                            });
  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BranchImpl<>(predicates,
                            prev,
                            (ByteBuf byteBuf) -> {
                              CURRENT c = parentContinuation.apply(byteBuf);
                              return continuation.apply(c, byteBuf.readLong());
                            });
  }

  @Override
  public Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> continuation) {
    throw new NotImplementedException();
  }

  @Override
  public <T> Branch<PREVIOUS, PREVIOUS, END> orElse(BiFunction<CURRENT, Long, T> continuation) {
    return null;
  }

  @Override
  public End<PREVIOUS, END> end() {
    return null;
  }
}
