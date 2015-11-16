package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Continuation<CURRENT> { // extends Function<ByteBuf, END>

  public <T> Continuation<T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Continuation<T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Continuation<T> readLong(BiFunction<CURRENT, Long, T> continuation);

  public <T> Continuation<T> branch(Predicate<CURRENT> predicate,
                                    NestedContinuation<CURRENT, T> continuation,
                                    Predicate<CURRENT> predicate2,
                                    NestedContinuation<CURRENT, T> continuation2);

  public Function<ByteBuf, CURRENT> toFn();
  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);


}
