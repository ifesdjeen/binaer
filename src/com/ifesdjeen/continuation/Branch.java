package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Branch<CURRENT, PREVIOUS> {

  public <T> Branch<T, PREVIOUS> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Branch<T, PREVIOUS> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Branch<T, PREVIOUS> readLong(BiFunction<CURRENT, Long, T> continuation);

  public Branch<CURRENT, CURRENT> branch(Predicate<CURRENT> continuation);

  // public <T> Branch<PREVIOUS, PREVIOUS, END> orElse(BiFunction<CURRENT, Long, T> continuation);

//  public <T> Branch<T, PREVIOUS, END> repeat(BiFunction<CURRENT, Integer, T> continuation);
//
//  public <T> Branch<T, PREVIOUS, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);

  public End<PREVIOUS, CURRENT> end();

  public interface End<PREVIOUS, END> {
    public Branch<PREVIOUS, PREVIOUS> otherwise(Predicate<PREVIOUS> predicate);
    public Function<ByteBuf, END> toFn();
    public Continuation<END> back();
  }


}