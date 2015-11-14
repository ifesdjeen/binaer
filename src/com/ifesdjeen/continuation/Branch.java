package com.ifesdjeen.continuation;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface Branch<CURRENT, PREVIOUS, END> extends Continuation<CURRENT, END> {

  public <T> Branch<T, PREVIOUS, END> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Branch<T, PREVIOUS, END> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Branch<T, PREVIOUS, END> readLong(BiFunction<CURRENT, Long, T> continuation);

  public <T> Branch<PREVIOUS, PREVIOUS, END> orElse(BiFunction<CURRENT, Long, T> continuation);

//  public <T> Branch<T, PREVIOUS, END> repeat(BiFunction<CURRENT, Integer, T> continuation);
//
//  public <T> Branch<T, PREVIOUS, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);

  public End<PREVIOUS, END> end();

  public interface End<PREVIOUS, END> extends Branch<END, PREVIOUS, END> {
    public Continuation<PREVIOUS, END> back();
  }


}