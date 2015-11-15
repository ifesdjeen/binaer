package com.ifesdjeen.continuation;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface Continuation<CURRENT> { // extends Function<ByteBuf, END>

  public <T> Continuation<T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Continuation<T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Continuation<T> readLong(BiFunction<CURRENT, Long, T> continuation);

  public <END> Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> continuation);

  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);


}
