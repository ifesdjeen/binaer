package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class BranchImpl<CURRENT, PREVIOUS, END> implements Branch<CURRENT, PREVIOUS, END> {


  private final List<Tuple<PREVIOUS, END>>  otherBranches;
  private final Predicate<PREVIOUS>         currentPredicate;
  private final Function<ByteBuf, PREVIOUS> beforeBranch;
  private final Function<ByteBuf, CURRENT>  parentContinuation;

  // For branch start
  public BranchImpl(List<Tuple<PREVIOUS, END>> otherBranches,
                    Predicate<PREVIOUS> currentPredicate,
                    Function<ByteBuf, PREVIOUS> beforeBranch,
                    Function<ByteBuf, CURRENT> parentContinuation
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
    if (parentContinuation == null) {
      return new BranchImpl<>(otherBranches,
                              currentPredicate,
                              beforeBranch,
                              (ByteBuf byteBuf) -> {
                                CURRENT c = parentContinuation.apply(byteBuf);
                                return continuation.apply(c, byteBuf.readByte());
                              });
    } else {
      return new BranchImpl<>(otherBranches,
                              currentPredicate,
                              beforeBranch,
                              (ByteBuf byteBuf) -> {
                                CURRENT c = parentContinuation.apply(byteBuf);
                                return continuation.apply(c, byteBuf.readByte());
                              });
    }
  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readInt(BiFunction<CURRENT, Integer, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ByteBuf byteBuf) -> {
                              CURRENT c = parentContinuation.apply(byteBuf);
                              return continuation.apply(c, byteBuf.readInt());
                            });
  }

  @Override
  public <T> Branch<T, PREVIOUS, END> readLong(BiFunction<CURRENT, Long, T> continuation) {
    return new BranchImpl<>(otherBranches,
                            currentPredicate,
                            beforeBranch,
                            (ByteBuf byteBuf) -> {
                              CURRENT c = parentContinuation.apply(byteBuf);
                              return continuation.apply(c, byteBuf.readLong());
                            });
  }

  @Override
  public Branch<CURRENT, CURRENT, END> branch(Predicate<CURRENT> continuation) {
    throw new NotImplementedException();
  }

  public End<PREVIOUS, END> end(Function<CURRENT, END> endFn) {
    otherBranches.add(new Tuple<PREVIOUS, END>(currentPredicate,
                                               new Function<ByteBuf, END>() {
                                                 @Override
                                                 public END apply(ByteBuf byteBuf) {
                                                   CURRENT cur = parentContinuation.apply(byteBuf);
                                                   return endFn.apply(cur);
                                                 }
                                               }));
    return null;
    //    return new BranchEnd<>(otherBranches,
    //                           currentPredicate,
    //                           beforeBranch);
  }

  public class BranchEnd<PREVIOUS, END> implements Branch.End<PREVIOUS, END> {

    private final List<Tuple<PREVIOUS, END>>  otherBranches;
    private final Predicate<PREVIOUS>         currentPredicate;
    private final Function<ByteBuf, PREVIOUS> beforeBranch;

    public BranchEnd(List<Tuple<PREVIOUS, END>> otherBranches,
                     Predicate<PREVIOUS> currentPredicate,
                     Function<ByteBuf, PREVIOUS> beforeBranch) {
      this.otherBranches = otherBranches;
      this.currentPredicate = currentPredicate;
      this.beforeBranch = beforeBranch;
    }

    @Override
    public <T> Branch<PREVIOUS, PREVIOUS, END> otherwise(Predicate<PREVIOUS> predicate) {
      return new BranchImpl<PREVIOUS, PREVIOUS, END>(otherBranches,
                                                     predicate,
                                                     beforeBranch,
                                                     beforeBranch);
    }

    @Override
    public Function<ByteBuf, END> theEnd() {
      return (byteBuf -> {
        PREVIOUS previous = beforeBranch.apply(byteBuf);
        for (Tuple<PREVIOUS, END> tuple : otherBranches) {
          if (tuple.getPredicate().test(previous)) {
            return tuple.getPrev().apply(byteBuf);
          }
        }
        throw new RuntimeException("No protocol matches");
      });
    }


  }

}
