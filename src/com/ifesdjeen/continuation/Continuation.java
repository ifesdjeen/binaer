package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Continuation<CURRENT> { // extends Function<ByteBuf, END>

  public <T> Continuation<T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Continuation<T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Continuation<T> readLong(BiFunction<CURRENT, Long, T> continuation);

  public <T> Continuation<T> readString(BiFunction<CURRENT, String, T> continuation,
                                        Integer length);

  public <T> Continuation<T> readString(BiFunction<CURRENT, String, T> continuation,
                                        Function<CURRENT, Integer> length);

  public <T> Continuation<T> branch(Predicate<CURRENT> predicate,
                                    NestedContinuation<CURRENT, T> continuation,
                                    Predicate<CURRENT> predicate2,
                                    NestedContinuation<CURRENT, T> continuation2);

  public Function<ByteBuf, CURRENT> toFn();

  public static <CURRENT> NestedContinuation<CURRENT, CURRENT> branch() {
    return new BranchStart<CURRENT>();
  }

  public static <T> Continuation<T> readByte(Function<Byte, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      byte b = byteBuf.readByte();
      return continuation.apply(b);
    });
  }

  public static <T> Continuation<T> readInt(Function<Integer, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      int b = byteBuf.readInt();
      return continuation.apply(b);
    });
  }

  public static <T> Continuation<T> readLong(Function<Long, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      long b = byteBuf.readLong();
      return continuation.apply(b);
    });
  }

  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);


}
