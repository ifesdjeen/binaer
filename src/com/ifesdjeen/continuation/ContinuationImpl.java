package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ContinuationImpl<CURRENT> implements Continuation<CURRENT> {

  private final Function<ByteBuf, CURRENT> parentContinuation;

  public ContinuationImpl(Function<ByteBuf, CURRENT> parent) {
    this.parentContinuation = parent;
  }

  @Override
  public <NEXT> Continuation<NEXT> readByte(BiFunction<CURRENT, Byte, NEXT> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      byte b = byteBuf.readByte();
      return continuation.apply(current, b);
    });
  }

  @Override
  public <T> Continuation<T> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      int b = byteBuf.readInt();
      return continuation.apply(current, b);
    });
  }

  @Override
  public <T> Continuation<T> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      long b = byteBuf.readLong();
      return continuation.apply(current, b);
    });
  }

  @Override
  public <END> Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> predicate) {
    return new BranchStart<>(new LinkedList<>(), predicate, parentContinuation);

  }

  //  @Override
  //  public <T> Continuation<List<T>, END> repeat(BiFunction<CURRENT, Integer, T> continuation) {
  //    return null;
  //  }

  //  @Override
  //  public <T> Continuation<T, END> optional(BiFunction<CURRENT, Integer, Optional<T>> optional) {
  //    return null;
  //  }

  //
  //  @Override
  //  public Function<ByteBuf, END> build() {
  //    return null;
  //  }

  //  @Override
  //  public END apply(ByteBuf byteBuf) {
  //    return null;
  //  }

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

}
