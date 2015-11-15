package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.Function;
import java.util.function.Predicate;

public class Tuple<PREVIOUS, END> {
  private final Function<ByteBuf, END> prev;
  private final Predicate<PREVIOUS>    predicate;

  public Tuple(Predicate<PREVIOUS> predicate, Function<ByteBuf, END> prev) {
    this.prev = prev;
    this.predicate = predicate;
  }

  public Function<ByteBuf, END> getPrev() {
    return prev;
  }

  public Predicate<PREVIOUS> getPredicate() {
    return predicate;
  }
}
