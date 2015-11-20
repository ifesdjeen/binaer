package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

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
  public <T> Continuation<T> readString(BiFunction<CURRENT, String, T> continuation, Integer length) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      byte[] bytes = new byte[length];
      byteBuf.readBytes(bytes);
      return continuation.apply(current, new String(bytes));
    });
  }

  @Override
  public <T> Continuation<T> readString(BiFunction<CURRENT, String, T> continuation, Function<CURRENT, Integer> length) {
    return new ContinuationImpl<>((byteBuf) -> {
      CURRENT current = parentContinuation.apply(byteBuf);
      byte[] bytes = new byte[length.apply(current)];
      byteBuf.readBytes(bytes);
      return continuation.apply(current, new String(bytes));
    });
  }

  @Override
  public <T> Continuation<T> branch(Predicate<CURRENT> predicate, NestedContinuation<CURRENT, T> continuation,
                                    // TODO: chanage to branch
                                    Predicate<CURRENT> predicate2, NestedContinuation<CURRENT, T> continuation2) {
    return new ContinuationImpl<>((byteBuf -> {
      CURRENT c = parentContinuation.apply(byteBuf);
      if (predicate.test(c)) {
        return continuation.toFn().apply(c, byteBuf); // TODO: avoid dereferencing every time
      } else if (predicate2.test(c)) {
        return continuation2.toFn().apply(c, byteBuf);
      } else {
        throw new RuntimeException("No matching protocol clauses");
      }
    }));
  }

  @Override
  public Function<ByteBuf, CURRENT> toFn() {
    return parentContinuation;
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

  // TODO: contunue

}
