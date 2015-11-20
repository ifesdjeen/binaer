package com.ifesdjeen.binaer;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Binaer<INIT, CURRENT> {

  <T> Binaer<INIT, T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  default <T> Binaer<INIT, T> readByte(Function<Byte, T> continuation) {
    return readByte((a_, t) -> continuation.apply(t));
  }

  <T> Binaer<INIT, T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  default <T> Binaer<INIT, T> readInt(Function<Integer, T> continuation) {
    return readInt((a_, t) -> continuation.apply(t));
  }

  <T> Binaer<INIT, T> readLong(BiFunction<CURRENT, Long, T> continuation);

  default <T> Binaer<INIT, T> readLong(Function<Long, T> continuation) {
    return readLong((a_, t) -> continuation.apply(t));
  }

//  <T> Continuation<INIT, T> readString(BiFunction<CURRENT, String, T> continuation,
//                                       Integer length);

  <T> Binaer<INIT, T> readString(Function<CURRENT, Integer> length,
                                 BiFunction<CURRENT, String, T> continuation);

  default <T> Binaer<INIT, T> readString(Function<CURRENT, Integer> length,
                                         Function<String, T> continuation) {
    return readString(length,
                      (a, b) -> continuation.apply(b));
  }

  <T> Binaer<INIT, T> branch(Predicate<CURRENT> predicate,
                             Binaer<CURRENT, T> continuation,
                             Predicate<CURRENT> predicate2,
                             Binaer<CURRENT, T> continuation2);

//  <T> Continuation<INIT, T> repeat(Continuation<CURRENT, T> continuation,
//                                   Integer length);

  public <ITEM, NEXT> Binaer<INIT, NEXT> repeat(Binaer<CURRENT, ITEM> continuation,
                                                Function<CURRENT, Integer> length,
                                                BiFunction<CURRENT, List<ITEM>, NEXT> merge);


//  <T> Continuation<INIT, T> repeat(Continuation<CURRENT, T> continuation,
//                                   Predicate<T> length);

  BiFunction<INIT, ByteBuf, CURRENT> toBiFn();

  Function<ByteBuf, CURRENT> toFn(Supplier<INIT> supplier);
  Function<ByteBuf, CURRENT> toFn();



  static <CURRENT> Binaer<Void, CURRENT> startWithByte(Function<Byte, CURRENT> continuation) {
    return new BinaerImpl<Void, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(byteBuf.readByte());
      };
    });
  }

  static <INIT, CURRENT> Binaer<INIT, CURRENT> startWithByte(BiFunction<INIT, Byte, CURRENT> continuation) {
    return new BinaerImpl<INIT, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readByte());
      };
    });
  }

  static <INIT, CURRENT> Binaer<INIT, CURRENT> startWithInt(BiFunction<INIT, Integer, CURRENT> continuation) {
    return new BinaerImpl<INIT, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readInt());
      };
    });
  }

  static <CURRENT> Binaer<Void, CURRENT> startWithInt(Function<Integer, CURRENT> continuation) {
    return new BinaerImpl<Void, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(byteBuf.readInt());
      };
    });
  }

  static <INIT, CURRENT> Binaer<INIT, CURRENT> startWithLong(BiFunction<INIT, Long, CURRENT> continuation) {
    return new BinaerImpl<INIT, CURRENT>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readLong());
      };
    });
  }
  //public <T> Continuation<T, END> repeat(BiFunction<CURRENT, Integer, T> continuation);

  // public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional);


}
