package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class BranchStart<CURRENT> implements NestedContinuation<CURRENT, CURRENT> {

  // For branch start
  public BranchStart() {
  }


  @Override
  public <T> NestedContinuation<CURRENT, T> readByte(BiFunction<CURRENT, Byte, T> continuation) {
    return new BranchBody<CURRENT, T>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readByte());
      };
    });
  }

  @Override
  public <T> NestedContinuation<CURRENT, T> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BranchBody<CURRENT, T>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readInt());
      };
    });
  }

  @Override
  public <T> NestedContinuation<CURRENT, T> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BranchBody<CURRENT, T>((current) -> {
      return (byteBuf) -> {
        return continuation.apply(current, byteBuf.readLong());
      };
    });
  }

  @Override
  public <T> NestedContinuation<CURRENT, T> branch(Predicate<CURRENT> predicate,
                                                   NestedContinuation<CURRENT, T> continuation,
                                                   Predicate<CURRENT> predicate2,
                                                   NestedContinuation<CURRENT, T> continuation2) {
    throw new RuntimeException("You've just branched, can't branch on that level");
  }

  @Override
  public BiFunction<CURRENT, ByteBuf, CURRENT> toFn() {
    return null;
  }
}

