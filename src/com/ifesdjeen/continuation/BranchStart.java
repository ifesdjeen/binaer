package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BranchStart<PREVIOUS> implements Branch<PREVIOUS, PREVIOUS> {


  private final List<Tuple<PREVIOUS, ?>>    otherBranches;
  private final Predicate<PREVIOUS>         currentPredicate;
  private final Function<ByteBuf, PREVIOUS> beforeBranch;

  // For branch start
  public BranchStart(List<Tuple<PREVIOUS, ?>> otherBranches,
                     Predicate<PREVIOUS> currentPredicate,
                     Function<ByteBuf, PREVIOUS> beforeBranch
                    ) {
    this.otherBranches = otherBranches;
    this.currentPredicate = currentPredicate;
    this.beforeBranch = beforeBranch;
  }

  @Override
  public <T> Branch<T, PREVIOUS> readByte(BiFunction<PREVIOUS, Byte, T> continuation) {

    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (current, byteBuf) -> {
                              return continuation.apply(current, byteBuf.readByte());
                            });

  }

  @Override
  public <T> Branch<T, PREVIOUS> readInt(BiFunction<PREVIOUS, Integer, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (current, byteBuf) -> {
                              return continuation.apply(current, byteBuf.readInt());
                            });
  }

  @Override
  public <T> Branch<T, PREVIOUS> readLong(BiFunction<PREVIOUS, Long, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (current, byteBuf) -> {
                              return continuation.apply(current, byteBuf.readLong());
                            });
  }

  @Override
  public End<PREVIOUS, PREVIOUS> end() {
    throw new RuntimeException("can't touch this");
  }

  @Override
  public Branch<PREVIOUS, PREVIOUS> branch(Predicate<PREVIOUS> continuation) {
    throw new NotImplementedException();
  }

}

