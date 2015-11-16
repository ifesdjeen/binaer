package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ContinuationTest {

  @Test
  public void simpleProtocolTest() {
    Function<ByteBuf, List<Number>> fn =
      ContinuationImpl
        .readByte(first -> {
          List<Number> o = new ArrayList<Number>();
          o.add(first);
          return o;
        })
        .readInt((List<Number> list, Integer second) -> {
          list.add(second);
          return list;
        })
        .readLong((List<Number> list, Long third) -> {
          list.add(third);
          return list;
        })
        .branch(list -> list.get(0).intValue() == 1,
                new BranchStart<List<Number>>()
                  .readByte((List<Number> list, Byte firstBranch) -> {
                    list.add(firstBranch);
                    return list;
                  }),
                list -> list.get(0).intValue() == 2,
                new BranchStart<List<Number>>()
                  .readByte((List<Number> list, Byte firstBranch) -> {
                    list.add((byte) (firstBranch + 1));
                    return list;
                  }))
        .readInt((list1, integer) -> {
          list1.add(integer);
          return list1;
        })
        .toFn();


    assertThat(fn.apply(Unpooled.buffer().writeByte(1)
                                .writeInt(2)
                                .writeLong(3)
                                .writeByte(4)
                                .writeInt(5)),
               is(new ArrayList<Number>(Arrays.asList((byte) 1, 2, 3L, (byte) 4, 5))));

    assertThat(fn.apply(Unpooled.buffer().writeByte(2)
                                .writeInt(2)
                                .writeLong(3)
                                .writeByte(4)
                                .writeInt(5)),
               is(new ArrayList<Number>(Arrays.asList((byte) 2, 2, 3L, (byte) 5, 5))));
  }

  @Test
  public void nestedBranchTest() {
    Function<ByteBuf, List<Number>> fn =
      ContinuationImpl
        .readByte(first -> {
          List<Number> o = new ArrayList<Number>();
          o.add(first);
          return o;
        })
        .branch(list -> list.get(0).intValue() == 1,
                new BranchStart<List<Number>>()
                  .readInt((List<Number> list, Integer firstBranch) -> {
                    list.add(firstBranch);
                    return list;
                  }).branch(list -> list.get(1).intValue() == 300,
                            new BranchStart<List<Number>>()
                              .readInt((list, i) -> {
                                list.add(i + 100);
                                return list;
                              }),
                            list -> list.get(1).intValue() == 400,
                            new BranchStart<List<Number>>()
                              .readInt((list, i) -> {
                                list.add(i + 200);
                                return list;
                              })),
                list -> list.get(0).intValue() == 2,
                new BranchStart<List<Number>>()
                  .readByte((List<Number> list, Byte firstBranch) -> {
                    list.add(firstBranch);
                    return list;
                  }))
        .readInt((List<Number> list1, Integer integer) -> {
          list1.add(integer);
          return list1;
        })
        .toFn();


    assertThat(fn.apply(Unpooled.buffer()
                                .writeByte(1)
                                .writeInt(300)
                                .writeInt(2)
                                .writeInt(3)),
               is(new ArrayList<Number>(Arrays.asList((byte) 1, 300, 102, 3))));

    assertThat(fn.apply(Unpooled.buffer()
                                .writeByte(1)
                                .writeInt(400)
                                .writeInt(2)
                                .writeInt(3)),
               is(new ArrayList<Number>(Arrays.asList((byte) 1, 400, 202, 3))));
    //
  }

  public class Header {
    public String           version;
    public Set<HeaderFlags> flags;

    @Override
    public String toString() {
      return "Header{" +
             "version='" + version + '\'' +
             ", flags=" + flags +
             '}';
    }
  }

  enum HeaderFlags {
    SERIAL_CONSISTENCY,
    DEFAULT_TIMESTAMP,
    WITH_NAMES_FOR_VALUES
  }
}

