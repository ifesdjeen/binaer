package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ContinuationImpl<PREVIOUS, CURRENT> implements Continuation<PREVIOUS, CURRENT> {

  private final Function<PREVIOUS, Function<ByteBuf, CURRENT>> parentContinuation;

  public ContinuationImpl(Function<PREVIOUS, Function<ByteBuf, CURRENT>> parentContinuation) {
    this.parentContinuation = parentContinuation;
  }


  @Override
  public <T> Continuation<PREVIOUS, T> readByte(BiFunction<CURRENT, Byte, T> continuation) {
    return new ContinuationImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readByte());
      };
    });
  }

  @Override
  public <T> Continuation<PREVIOUS, T> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new ContinuationImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readInt());
      };
    });
  }

  @Override
  public <T> Continuation<PREVIOUS, T> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new ContinuationImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readLong());
      };
    });
  }

  @Override
  public <T> Continuation<PREVIOUS, T> readString(BiFunction<CURRENT, String, T> continuation, Integer length) {
    throw new NotImplementedException();
  }

  @Override
  public <T> Continuation<PREVIOUS, T> readString(BiFunction<CURRENT, String, T> continuation,
                                                  Function<CURRENT, Integer> length) {
    throw new NotImplementedException();
  }

  @Override
  public <T> Continuation<PREVIOUS, T> branch(Predicate<CURRENT> predicate,
                                              Continuation<CURRENT, T> continuation,
                                              Predicate<CURRENT> predicate2,
                                              Continuation<CURRENT, T> continuation2) {
    return new ContinuationImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuf) -> {
        CURRENT c = fn.apply(byteBuf);
        if (predicate.test(c)) {
          return continuation.toFn()
                             .apply(c, byteBuf); // TODO: avoid dereferencing every time
        } else if (predicate2.test(c)) {
          return continuation2.toFn()
                              .apply(c, byteBuf);
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

  @Override
  public Function<ByteBuf, CURRENT> toFn(Supplier<PREVIOUS> supplier) {
    return (byteBuf -> {
      return parentContinuation.apply(supplier.get()).apply(byteBuf);
    });
  }

}
