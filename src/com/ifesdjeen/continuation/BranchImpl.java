package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BranchImpl<CURRENT, PREVIOUS> implements Branch<CURRENT, PREVIOUS> {


  private final List<Tuple<PREVIOUS, ?>>               otherBranches;
  private final Predicate<PREVIOUS>                    currentPredicate;
  private final Function<ByteBuf, PREVIOUS>            beforeBranch;
  private final BiFunction<PREVIOUS, ByteBuf, CURRENT> parentContinuation;

  public BranchImpl(List<Tuple<PREVIOUS, ?>> otherBranches,
                    Predicate<PREVIOUS> currentPredicate,
                    Function<ByteBuf, PREVIOUS> beforeBranch,
                    BiFunction<PREVIOUS, ByteBuf, CURRENT> parentContinuation
                   ) {
    this.otherBranches = otherBranches;
    this.currentPredicate = currentPredicate;
    this.beforeBranch = beforeBranch;
    this.parentContinuation = parentContinuation;
  }

  @Override
  public <T> Branch<T, PREVIOUS> readByte(BiFunction<CURRENT, Byte, T> continuation) {

    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ignore, byteBuf) -> {
                              CURRENT c = parentContinuation.apply(null, byteBuf);
                              return continuation.apply(c, byteBuf.readByte());
                            });

  }

  @Override
  public <T> Branch<T, PREVIOUS> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ignore, byteBuf) -> {
                              CURRENT c = parentContinuation.apply(null, byteBuf);
                              return continuation.apply(c, byteBuf.readInt());
                            });
  }

  @Override
  public <T> Branch<T, PREVIOUS> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ignore, byteBuf) -> {
                              CURRENT c = parentContinuation.apply(null, byteBuf);
                              return continuation.apply(c, byteBuf.readLong());
                            });
  }

  @Override
  public Branch<CURRENT, CURRENT> branch(Predicate<CURRENT> predicate) {
    return new BranchStart<>(new LinkedList<>(), predicate, byteBuf -> {
      CURRENT c = parentContinuation.apply(null, byteBuf);
      return c;
    });
  }

  public End<PREVIOUS, CURRENT> end() {
    otherBranches.add(new Tuple<PREVIOUS, CURRENT>(currentPredicate,
                                                   (previous, byteBuf) -> {
                                                     CURRENT cur = parentContinuation.apply(
                                                       previous, byteBuf);
                                                     return cur;
                                                   }));

    // TODO: add typecheck
    List<Tuple<PREVIOUS, CURRENT>> o = (List<Tuple<PREVIOUS, CURRENT>>) (List) otherBranches;

    return new BranchEnd<>(o,
                           beforeBranch);
  }

  public class BranchEnd<PREVIOUS, END> implements Branch.End<PREVIOUS, END> {

    private final List<Tuple<PREVIOUS, END>>  otherBranches;
    private final Function<ByteBuf, PREVIOUS> beforeBranch;

    public BranchEnd(List<Tuple<PREVIOUS, END>> otherBranches,
                     Function<ByteBuf, PREVIOUS> beforeBranch) {
      this.otherBranches = otherBranches;
      this.beforeBranch = beforeBranch;
    }

    @Override
    public Branch<PREVIOUS, PREVIOUS> otherwise(Predicate<PREVIOUS> predicate) {
      // TODO: add typechecks!
      List<Tuple<PREVIOUS, ?>> o = (List<Tuple<PREVIOUS, ?>>)(List)otherBranches;
      return new BranchStart<>(o, predicate, beforeBranch);
    }

    @Override
    public Function<ByteBuf, END> toFn() {
      return (byteBuf -> {
        PREVIOUS previous = beforeBranch.apply(byteBuf);
        for (Tuple<PREVIOUS, END> tuple : otherBranches) {
          if (tuple.getPredicate().test(previous)) {
            return tuple.getPrev().apply(previous, byteBuf);
          }
        }
        throw new RuntimeException("No protocol matches");
      });
    }

    @Override
    public Continuation<END> back() {
      return new ContinuationImpl<>(toFn());
    }


  }

}
