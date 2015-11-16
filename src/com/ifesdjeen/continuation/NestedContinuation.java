package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface NestedContinuation<INIT, CURRENT> { // extends Function<ByteBuf, END>

  public <T> NestedContinuation<INIT, T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> NestedContinuation<INIT, T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> NestedContinuation<INIT, T> readLong(BiFunction<CURRENT, Long, T> continuation);

  public <T> NestedContinuation<INIT, T> branch(Predicate<CURRENT> predicate,
                                                NestedContinuation<CURRENT, T> continuation,
                                                Predicate<CURRENT> predicate2,
                                                NestedContinuation<CURRENT, T> continuation2);

  public BiFunction<INIT, ByteBuf, CURRENT> toFn();
  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);


}
