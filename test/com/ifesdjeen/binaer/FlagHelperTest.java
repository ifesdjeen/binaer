package com.ifesdjeen.binaer;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FlagHelperTest {

  @Test
  public void bitSettingTest() {
    int i = 0;
    i = FlagHelper.setFlag(i,
                           mapper,
                           HeaderFlags.SERIAL_CONSISTENCY);
    assertThat(FlagHelper.isFlagSet(i,
                                    mapper,
                                    HeaderFlags.SERIAL_CONSISTENCY),
               is(true));

    assertThat(FlagHelper.isFlagSet(i,
                                    mapper,
                                    HeaderFlags.DEFAULT_TIMESTAMP),
               is(false));
  }

  @Test
  public void getSetFlagsTest() {
    int i = 0;
    i = FlagHelper.setFlags(i,
                            mapper,
                            HeaderFlags.SERIAL_CONSISTENCY,
                            HeaderFlags.WITH_NAMES_FOR_VALUES);

    assertThat(FlagHelper.getSetFlags(i,
                                      mapper,
                                      HeaderFlags.values()),
               is(Arrays.asList(HeaderFlags.SERIAL_CONSISTENCY,
                                HeaderFlags.WITH_NAMES_FOR_VALUES)));
  }

  enum HeaderFlags {
    SERIAL_CONSISTENCY,
    DEFAULT_TIMESTAMP,
    WITH_NAMES_FOR_VALUES
  }

  private static Function<HeaderFlags, Integer> mapper = flag -> {
    switch (flag) {
      case SERIAL_CONSISTENCY:
        return 0;
      case DEFAULT_TIMESTAMP:
        return 1;
      case WITH_NAMES_FOR_VALUES:
        return 2;
    }
    throw new RuntimeException("Couldn't find a matching clause");
  };
}
