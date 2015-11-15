package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ContinuationImpl<CURRENT, END> implements Continuation<CURRENT, END> {

  private final Function<ByteBuf, CURRENT> parentContinuation;

  public ContinuationImpl(Function<ByteBuf, CURRENT> parent) {
    this.parentContinuation = parent;
  }

  @Override
  public <NEXT> Continuation<NEXT, END> readByte(BiFunction<CURRENT, Byte, NEXT> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      byte b = byteBuf.readByte();
      return continuation.apply(current, b);
    });
  }

  @Override
  public <T> Continuation<T, END> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      int b = byteBuf.readInt();
      return continuation.apply(current, b);
    });
  }

  @Override
  public <T> Continuation<T, END> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      long b = byteBuf.readLong();
      return continuation.apply(current, b);
    });
  }

  @Override
  public Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> predicate) {
    return new BranchImpl<CURRENT, CURRENT, END>(new LinkedList<>(), predicate, parentContinuation, null);

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

  public static <T, END> Continuation<T, END> readByte(Function<Byte, T> continuation) {
    throw new NotImplementedException();
  }

  public static  <T, END> Continuation<T, END> readInt(Function<Integer, T> continuation) {
    throw new NotImplementedException();
  }

  public static <T, END> Continuation<T, END> readLong(Function<Long, T> continuation) {
    throw new NotImplementedException();
  }

}
