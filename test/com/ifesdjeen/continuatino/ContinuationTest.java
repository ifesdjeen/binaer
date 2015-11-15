package com.ifesdjeen.continuatino;

import com.ifesdjeen.continuation.Continuation;
import com.ifesdjeen.continuation.ContinuationImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Set;
import java.util.function.BiFunction;

public class ContinuationTest {

  @Test
  public void simpleProtocolTest() {
    ByteBuf buf = Unpooled.buffer();
    buf.writeByte(1);
    buf.writeByte(1);
    buf.writeInt(1);
    buf.writeByte(1);
    buf.writeLong(1);

    ContinuationImpl
      .readByte(version -> {
        Header h = new Header();
        h.version = version == 1 ? "1" : "2";
        return h;
      })
      .readByte((Header header, Byte aByte) -> {
        return header;
      })
      .branch(header1 -> header1.version == "1").readLong((header, opcode) -> { return header; }).end()
      .otherwise(header2 -> header2.version == "2").readInt((header, opcode) -> { return header; }).end();


  }

  public class Header {
    public String           version;
    public Set<HeaderFlags> flags;
  }

  enum HeaderFlags {
    SERIAL_CONSISTENCY,
    DEFAULT_TIMESTAMP,
    WITH_NAMES_FOR_VALUES
  }
}
