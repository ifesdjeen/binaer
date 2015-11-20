package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Continuation<INIT, CURRENT> {

  public <T> Continuation<INIT, T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Continuation<INIT, T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Continuation<INIT, T> readLong(BiFunction<CURRENT, Long, T> continuation);

  public <T> Continuation<INIT, T> readString(BiFunction<CURRENT, String, T> continuation,
                                              Integer length);

  public <T> Continuation<INIT, T> readString(BiFunction<CURRENT, String, T> continuation,
                                              Function<CURRENT, Integer> length);

  public <T> Continuation<INIT, T> branch(Predicate<CURRENT> predicate,
                                          Continuation<CURRENT, T> continuation,
                                          Predicate<CURRENT> predicate2,
                                          Continuation<CURRENT, T> continuation2);

  public BiFunction<INIT, ByteBuf, CURRENT> toFn();

  public Function<ByteBuf, CURRENT> toFn(Supplier<INIT> supplier);

  static <CURRENT> Continuation<Void, CURRENT> startWithByte(Function<Byte, CURRENT> continuation) {
    return new ContinuationImpl<Void, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(byteBuf.readByte());
      };
    });
  }

  static <INIT, CURRENT> Continuation<INIT, CURRENT> startWithByte(BiFunction<INIT, Byte, CURRENT> continuation) {
    return new ContinuationImpl<INIT, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readByte());
      };
    });
  }

  static <INIT, CURRENT> Continuation<INIT, CURRENT> startWithInt(BiFunction<INIT, Integer, CURRENT> continuation) {
    return new ContinuationImpl<INIT, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readInt());
      };
    });
  }

  static <CURRENT> Continuation<Void, CURRENT> startWithInt(Function<Integer, CURRENT> continuation) {
    return new ContinuationImpl<Void, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(byteBuf.readInt());
      };
    });
  }

  static <INIT, CURRENT> Continuation<INIT, CURRENT> startWithLong(BiFunction<INIT, Long, CURRENT> continuation) {
    return new ContinuationImpl<INIT, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readLong());
      };
    });
  }
  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);


}
