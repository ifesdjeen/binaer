package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Branch<PREVIOUS, CURRENT> {

  public <T> Continuation<T> readByte(BiFunction<CURRENT, Byte, T> continuation);

  public <T> Continuation<T> readInt(BiFunction<CURRENT, Integer, T> continuation);

  public <T> Continuation<T> readLong(BiFunction<CURRENT, Long, T> continuation);

}