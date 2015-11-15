package com.ifesdjeen.continuation;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Set;
import java.util.function.Function;

public class ContinuationTest {

  @Test
  public void simpleProtocolTest() {
    ByteBuf buf = Unpooled.buffer();
    buf.writeByte(2);
    buf.writeByte(2);
    buf.writeInt(3);
    buf.writeByte(4);
    buf.writeLong(5);
    buf.writeLong(6);
    buf.writeLong(7);


    Function<ByteBuf, Object> fn = ContinuationImpl
      .readByte(version -> {
        Header h = new Header();
        h.version = version == 1 ? "1" : "2";
        return h;
      })
      .readByte((Header header, Byte aByte) -> {
        return header;
      })
      .branch(header1 -> header1.version == "1").readLong((header, opcode) -> {
        return header;
      }).end((i -> i))
      .otherwise(header2 -> header2.version == "2").readInt((header, opcode) -> {
        return header;
      }).end(i -> i)
      .theEnd();

    System.out.println(fn.apply(buf));

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
