package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Branch<CURRENT, PREVIOUS, END> extends Continuation<CURRENT, END> {

  public <T> Branch<T, PREVIOUS, END> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Branch<T, PREVIOUS, END> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Branch<T, PREVIOUS, END> readLong(BiFunction<CURRENT, Long, T> continuation);

  // public <T> Branch<PREVIOUS, PREVIOUS, END> orElse(BiFunction<CURRENT, Long, T> continuation);

//  public <T> Branch<T, PREVIOUS, END> repeat(BiFunction<CURRENT, Integer, T> continuation);
//
//  public <T> Branch<T, PREVIOUS, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);

  public End<PREVIOUS, END> end(Function<CURRENT, END> endFn);

  public interface End<PREVIOUS, END> {
    public <T> Branch<PREVIOUS, PREVIOUS, END> otherwise(Predicate<PREVIOUS> predicate);
    public Function<ByteBuf, END> theEnd();
  }


}