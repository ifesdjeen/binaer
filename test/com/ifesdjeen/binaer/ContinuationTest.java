package com.ifesdjeen.binaer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ContinuationTest {

  @Test
  public void simpleProtocolTest() {
    Binaer<List<Number>, List<Number>> continuation =
      Binaer
        .startWithByte((List<Number> o, Byte first) -> {
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
                Binaer.startWithByte((List<Number> list, Byte firstBranch) -> {
                  list.add(firstBranch);
                  return list;
                }),
                list -> list.get(0).intValue() == 2,
                Binaer.startWithByte((List<Number> list, Byte firstBranch) -> {
                  list.add((byte) (firstBranch + 1));
                  return list;
                }))
        .readInt((list1, integer) -> {
          list1.add(integer);
          return list1;
        });

    {
      ByteBuf buf = Unpooled.buffer().writeByte(1)
                            .writeInt(2)
                            .writeLong(3)
                            .writeByte(4)
                            .writeInt(5);
      assertThat(continuation.toBiFn().apply(new LinkedList<>(), buf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 1, 2, 3L, (byte) 4, 5))));

      buf.resetReaderIndex();

      assertThat(continuation.toFn(LinkedList::new).apply(buf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 1, 2, 3L, (byte) 4, 5))));
    }

    {
      ByteBuf buf = Unpooled.buffer().writeByte(2)
                            .writeInt(2)
                            .writeLong(3)
                            .writeByte(4)
                            .writeInt(5);
      assertThat(continuation.toBiFn().apply(new LinkedList<>(), buf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 2, 2, 3L, (byte) 5, 5))));
      buf.resetReaderIndex();
      assertThat(continuation.toFn(() -> new LinkedList<>()).apply(buf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 2, 2, 3L, (byte) 5, 5))));
    }
  }

  @Test
  public void asdTest() {
    Binaer<Void, Object> continuation =
      Binaer
        .startWithByte(Function.identity())
        .branch(
          (Byte type) -> type == (byte) 1,
          Binaer.startWithLong((Byte type, Long l) -> new Date(l)),

          (Byte type) -> type == (byte) 2,
          Binaer.startWithInt((Byte type, Integer stringLength) -> stringLength)
                .readString(Function.identity(),
                                  (String s) -> s));

    System.out.println(
      continuation.toBiFn().apply(null, Unpooled.buffer()
                                                .writeByte(1)
                                                .writeLong(System.currentTimeMillis())));

    System.out.println(
      continuation.toBiFn().apply(null, Unpooled.buffer()
                                                .writeByte(2)
                                                .writeInt(6)
                                                .writeBytes("abcdef".getBytes())));


  }


  @Test
  public void nestedBranchTest() {
    Binaer<List<Number>, List<Number>> continuation =
      Binaer
        .startWithByte((List<Number> o, Byte first) -> {
          o.add(first);
          return o;
        })
        .branch(list -> list.get(0).intValue() == 1,
                Binaer.startWithInt((List<Number> list, Integer firstBranch) -> {
                  list.add(firstBranch);
                  return list;
                }).branch(list -> list.get(1).intValue() == 300,
                          Binaer.startWithInt((list, i) -> {
                            list.add(i + 100);
                            return list;
                          }),
                          list -> list.get(1).intValue() == 400,
                          Binaer.startWithInt((list, i) -> {
                            list.add(i + 200);
                            return list;
                          })),
                list -> list.get(0).intValue() == 2,
                Binaer.startWithByte((List<Number> list, Byte firstBranch) -> {
                  list.add(firstBranch);
                  return list;
                }))
        .readInt((List<Number> list1, Integer integer) -> {
          list1.add(integer);
          return list1;
        });

    {
      ByteBuf byteBuf = Unpooled.buffer()
                                .writeByte(1)
                                .writeInt(300)
                                .writeInt(2)
                                .writeInt(3);
      assertThat(continuation.toBiFn().apply(new LinkedList<>(), byteBuf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 1, 300, 102, 3))));
      byteBuf.resetReaderIndex();
      assertThat(continuation.toFn(() -> new LinkedList<>()).apply(byteBuf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 1, 300, 102, 3))));
    }

    {
      ByteBuf byteBuf = Unpooled.buffer()
                                .writeByte(1)
                                .writeInt(400)
                                .writeInt(2)
                                .writeInt(3);
      assertThat(continuation.toBiFn().apply(new LinkedList<>(), byteBuf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 1, 400, 202, 3))));
      byteBuf.resetReaderIndex();
      assertThat(continuation.toFn(() -> new LinkedList<>()).apply(byteBuf),
                 is(new ArrayList<Number>(Arrays.asList((byte) 1, 400, 202, 3))));
    }
  }

  @Test
  public void pascalStringTest() {
    Binaer<Void, String> continuation =
      Binaer.startWithInt(Function.identity())
            .readString(Function.identity(),
                              (Integer integer, String s) -> s);

    assertThat(continuation.toFn(() -> null)
                           .apply(Unpooled.buffer()
                                          .writeInt(6)
                                          .writeBytes("abcdef".getBytes())),
               is("abcdef"));
  }

  @Test
  public void repeatedStringTest() {
    Binaer<Integer, String> netString =
      Binaer.startWithInt((Integer a_, Integer i) -> i)
            .readString(Function.identity(),
                              Function.identity());

    Binaer<Void, List<String>> continuation =
      Binaer.startWithInt(Function.identity())
            .repeat(netString,
                          Function.identity(),
                          (prev, l) -> l);

    assertThat(continuation.toFn(() -> null)
                           .apply(Unpooled.buffer()
                                          .writeInt(3)
                                          .writeInt(6)
                                          .writeBytes("abcdef".getBytes())
                                          .writeInt(5)
                                          .writeBytes("fghij".getBytes())
                                          .writeInt(4)
                                          .writeBytes("klmn".getBytes())),
               is(Arrays.asList("abcdef", "fghij", "klmn")));
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


