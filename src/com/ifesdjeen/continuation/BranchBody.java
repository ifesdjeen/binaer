package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BranchBody<PREVIOUS, CURRENT> implements NestedContinuation<PREVIOUS, CURRENT> {

  private final Function<PREVIOUS, Function<ByteBuf, CURRENT>> parentContinuation;

  public BranchBody(Function<PREVIOUS, Function<ByteBuf, CURRENT>> parentContinuation) {
    this.parentContinuation = parentContinuation;
  }


  @Override
  public <T> NestedContinuation<PREVIOUS, T> readByte(BiFunction<CURRENT, Byte, T> continuation) {
    return new BranchBody<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readByte());
      };
    });
  }

  @Override
  public <T> NestedContinuation<PREVIOUS, T> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BranchBody<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readInt());
      };
    });
  }

  @Override
  public <T> NestedContinuation<PREVIOUS, T> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BranchBody<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readLong());
      };
    });
  }

  @Override
  public <T> NestedContinuation<PREVIOUS, T> branch(Predicate<CURRENT> predicate,
                                                    NestedContinuation<CURRENT, T> continuation,
                                                    Predicate<CURRENT> predicate2,
                                                    NestedContinuation<CURRENT, T> continuation2) {
    return new BranchBody<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuf) -> {
        CURRENT c = fn.apply(byteBuf);
        if (predicate.test(c)) {
          return continuation.toFn().apply(c, byteBuf); // TODO: avoid dereferencing every time
        } else if (predicate2.test(c)) {
          return continuation2.toFn().apply(c, byteBuf);
        } else {
          throw new RuntimeException("No matching protocol clauses");
        }
      };
    });
  }



  @Override
  public BiFunction<PREVIOUS, ByteBuf, CURRENT> toFn() {
    return ((previous, byteBuf) -> {
      return parentContinuation.apply(previous).apply(byteBuf);
    });
  }
}