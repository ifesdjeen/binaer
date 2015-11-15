package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Tuple<PREVIOUS, END> {
  private final BiFunction<PREVIOUS, ByteBuf, END> prev;
  private final Predicate<PREVIOUS>                predicate;

  public Tuple(Predicate<PREVIOUS> predicate, BiFunction<PREVIOUS, ByteBuf, END> prev) {
    this.prev = prev;
    this.predicate = predicate;
  }

  public BiFunction<PREVIOUS, ByteBuf, END> getPrev() {
    return prev;
  }

  public Predicate<PREVIOUS> getPredicate() {
    return predicate;
  }
}
