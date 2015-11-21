package com.ifesdjeen.binaer;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BinaerImpl<INIT, CURRENT> implements Binaer<INIT, CURRENT> {

  private final Function<INIT, Function<ByteBuf, CURRENT>> parentContinuation;

  public BinaerImpl(Function<INIT, Function<ByteBuf, CURRENT>> parentContinuation) {
    this.parentContinuation = parentContinuation;
  }

  @Override
  public <T> Binaer<INIT, T> readByte(BiFunction<CURRENT, Byte, T> continuation) {
    return new BinaerImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readByte());
      };
    });
  }

  @Override
  public <T> Binaer<INIT, T> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BinaerImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readInt());
      };
    });
  }

  @Override
  public <T> Binaer<INIT, T> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BinaerImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuffer) -> {
        CURRENT c = fn.apply(byteBuffer);
        return continuation.apply(c, byteBuffer.readLong());
      };
    });
  }

  //  @Override
  //  public <T> Continuation<PREVIOUS, T> readString(BiFunction<CURRENT, String, T> continuation, Integer length) {
  //    throw new NotImplementedException();
  //  }

  @Override
  public <T> Binaer<INIT, T> readString(Function<CURRENT, Integer> length,
                                        BiFunction<CURRENT, String, T> continuation) {

    return new BinaerImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);

      return (byteBuf) -> {
        CURRENT current = fn.apply(byteBuf);
        byte[] bytes = new byte[length.apply(current)];
        byteBuf.readBytes(bytes);
        return continuation.apply(current, new String(bytes));
      };
    });
  }

  @Override
  public <T> Binaer<INIT, T> branch(Predicate<CURRENT> predicate,
                                    Binaer<CURRENT, T> continuation,
                                    Predicate<CURRENT> predicate2,
                                    Binaer<CURRENT, T> continuation2) {
    return new BinaerImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuf) -> {
        CURRENT c = fn.apply(byteBuf);
        if (predicate.test(c)) {
          return continuation.toBiFn()
                             .apply(c, byteBuf); // TODO: avoid dereferencing every time
        } else if (predicate2.test(c)) {
          return continuation2.toBiFn()
                              .apply(c, byteBuf);
        } else {
          throw new RuntimeException("No matching protocol clauses");
        }
      };
    });
  }

  @Override
  public <ITEM, NEXT> Binaer<INIT, NEXT> repeat(Binaer<CURRENT, ITEM> continuation,
                                                Function<CURRENT, Integer> length,
                                                BiFunction<CURRENT, List<ITEM>, NEXT> merge) {
    return new BinaerImpl<>((previous) -> {
      Function<ByteBuf, CURRENT> fn = parentContinuation.apply(previous);
      return (byteBuf) -> {
        CURRENT c = fn.apply(byteBuf);
        List<ITEM> l = new ArrayList<>();
        for (int i = 0; i < length.apply(c); i++) {
          l.add(continuation.toBiFn().apply(c, byteBuf));
        }
        return merge.apply(c, l);
      };
    });
  }

  @Override
  public BiFunction<INIT, ByteBuf, CURRENT> toBiFn() {
    return ((previous, byteBuf) -> {
      return parentContinuation.apply(previous).apply(byteBuf);
    });
  }

  @Override
  public Function<ByteBuf, CURRENT> toFn(Supplier<INIT> supplier) {
    return (byteBuf -> {
      return parentContinuation.apply(supplier.get()).apply(byteBuf);
    });
  }

  @Override
  public Function<ByteBuf, CURRENT> toFn() {
    return (byteBuf -> {
      return parentContinuation.apply(null).apply(byteBuf);
    });
  }
}
