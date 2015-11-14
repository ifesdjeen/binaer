package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Continuation<CURRENT, END> { // extends Function<ByteBuf, END>

  public <T> Continuation<T, END> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Continuation<T, END> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Continuation<T, END> readLong(BiFunction<CURRENT, Long, T> continuation);

  public Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> continuation);

  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);



}
