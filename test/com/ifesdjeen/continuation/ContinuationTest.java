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
    Function<ByteBuf, Object> fn = ContinuationImpl
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
      .branch(list -> list.get(0).intValue() == 1)
      .readByte((list, firstBranch) -> {
        list.add(firstBranch);
        return list;
      })
      .end((i -> i))
      .otherwise(list -> list.get(0).intValue() == 2)
      .readByte((list, secondBranch) -> {
        list.add((byte) (secondBranch + 1));
        return list;
      })
      .end((i -> i))
      .toFn();


    assertThat(fn.apply(Unpooled.buffer().writeByte(1)
                                .writeInt(2)
                                .writeLong(3)
                                .writeByte(4)),
               is(new ArrayList<Number>(Arrays.asList((byte) 1, 2, 3L, (byte)4))));

    assertThat(fn.apply(Unpooled.buffer().writeByte(2)
                                .writeInt(2)
                                .writeLong(3)
                                .writeByte(4)),
               is(new ArrayList<Number>(Arrays.asList((byte) 2, 2, 3L, (byte)5))));

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
