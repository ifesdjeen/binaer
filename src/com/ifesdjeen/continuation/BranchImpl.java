package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BranchImpl<CURRENT, PREVIOUS, END> implements Branch<CURRENT, PREVIOUS, END> {


  private final List<Tuple<PREVIOUS, END>>             otherBranches;
  private final Predicate<PREVIOUS>                    currentPredicate;
  private final Function<ByteBuf, PREVIOUS>            beforeBranch;
  private final BiFunction<PREVIOUS, ByteBuf, CURRENT> parentContinuation;

  // For branch start
  public BranchImpl(List<Tuple<PREVIOUS, END>> otherBranches,
                    Predicate<PREVIOUS> currentPredicate,
                    Function<ByteBuf, PREVIOUS> beforeBranch,
                    BiFunction<PREVIOUS, ByteBuf, CURRENT> parentContinuation
                   ) {
    this.otherBranches = otherBranches;
    this.currentPredicate = currentPredicate;
    this.beforeBranch = beforeBranch;
    this.parentContinuation = parentContinuation;
  }

  //  // Further flow
  //  public BranchImpl(List<Tuple<PREVIOUS>> predicates,
  //                    Function<ByteBuf, CURRENT> parentContinuation) {
  //    this.otherBranches = new LinkedList<>();
  //    this.parentContinuation = parentContinuation;
  //  }


  //  // Otherwise condition
  //  public BranchImpl(List<Tuple<PREVIOUS>> predicates,
  //                    Function<ByteBuf, PREVIOUS> beforeBranch,
  //                    Predicate<PREVIOUS> currentPredicate,
  //                    Function<ByteBuf, CURRENT> parentContinuation) {
  //    this.otherBranches = predicates;
  //    this.beforeBranch = beforeBranch;
  //    this.otherBranches.add(new Tuple<PREVIOUS>(beforeBranch, currentPredicate));
  //    this.parentContinuation = parentContinuation;
  //  }

  //private Function<ByteBuf, CURRENT>

  @Override
  public <T> Branch<T, PREVIOUS, END> readByte(BiFunction<CURRENT, Byte, T> continuation) {

    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ignore, byteBuf) -> {
                              CURRENT c = parentContinuation.apply(null, byteBuf);
                              return continuation.apply(c, byteBuf.readByte());
                            });

  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ignore, byteBuf) -> {
                              CURRENT c = parentContinuation.apply(null, byteBuf);
                              return continuation.apply(c, byteBuf.readInt());
                            });
  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ignore, byteBuf) -> {
                              CURRENT c = parentContinuation.apply(null, byteBuf);
                              return continuation.apply(c, byteBuf.readLong());
                            });
  }

  @Override
  public Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> continuation) {
    throw new NotImplementedException();
  }

  public End<PREVIOUS, END> end(Function<CURRENT, END> endFn) {
    otherBranches.add(new Tuple<PREVIOUS, END>(currentPredicate,
                                               (previous, byteBuf) -> {
                                                 CURRENT cur = parentContinuation.apply(previous, byteBuf);
                                                 return endFn.apply(cur);
                                               }));
    return new BranchEnd<>(otherBranches,
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
    public <T> Branch<PREVIOUS, PREVIOUS, END> otherwise(Predicate<PREVIOUS> predicate) {
      return new BranchStart<>(otherBranches, predicate, beforeBranch);
    }

    @Override
    public Function<ByteBuf, END> theEnd() {
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


  }

}
