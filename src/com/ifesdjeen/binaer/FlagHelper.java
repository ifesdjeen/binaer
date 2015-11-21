package com.ifesdjeen.binaer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FlagHelper {

  /**
   * Set Bit with index of {@code flagId} on the {@code destination} integer.
   */
  public static int setBit(int destination,
                           int flagId) {
    return destination | (1 << flagId);
  }

  public static byte setBit(byte destination,
                            int flagId) {
    return (byte) (destination | (1 << flagId));
  }

  public static long setBit(long destination,
                            int flagId) {
    return destination | (1L << flagId);
  }

  /**
   * Unset Bit with index of {@code flagId} on the {@code destination} integer.
   */
  public static int unsetBit(int destination,
                             int flagId) {
    return destination & ~(1 << flagId);
  }

  public static byte unsetBit(byte destination,
                              int flagId) {
    return (byte) (destination & ~(1 << flagId));
  }

  public static long unsetBit(long destination,
                              int flagId) {
    return destination & ~(1L << flagId);
  }

  /**
   * Returns {@code true} if Bit with index of {@code flagId} is set on the
   * {@code destination} integer.
   */
  public static boolean isSet(int destination,
                              int flagId) {
    return (destination & (1 << flagId)) != 0;
  }

  public static boolean isSet(byte destination,
                              int flagId) {
    return (destination & (1 << flagId)) != 0;
  }

  public static boolean isSet(long destination,
                              int flagId) {
    return (destination & (1L << flagId)) != 0;
  }

  public static <T extends Enum> int setFlag(int destination,
                                             Function<T, Integer> mapper,
                                             T e) {
    return setBit(destination, mapper.apply(e));
  }

  public static <T extends Enum> byte setFlag(byte destination,
                                              Function<T, Integer> mapper,
                                              T e) {
    return setBit(destination, mapper.apply(e));
  }

  public static <T extends Enum> long setFlag(long destination,
                                              Function<T, Integer> mapper,
                                              T e) {
    return setBit(destination, mapper.apply(e));
  }

  public static <T extends Enum> int setFlags(int destination,
                                              Function<T, Integer> mapper,
                                              T... e) {
    for (T e1 : e) {
      destination = setBit(destination, mapper.apply(e1));
    }
    return destination;
  }

  public static <T extends Enum> byte setFlags(byte destination,
                                               Function<T, Integer> mapper,
                                               T... e) {
    for (T e1 : e) {
      destination = setBit(destination, mapper.apply(e1));
    }
    return destination;
  }

  public static <T extends Enum> long setFlags(long destination,
                                               Function<T, Integer> mapper,
                                               T... e) {
    for (T e1 : e) {
      destination = setBit(destination, mapper.apply(e1));
    }
    return destination;
  }

  public static <T extends Enum> int unsetFlag(int destination,
                                               Function<T, Integer> mapper,
                                               T e) {
    return unsetBit(destination, mapper.apply(e));
  }

  public static <T extends Enum> byte unsetFlag(byte destination,
                                                Function<T, Integer> mapper,
                                                T e) {
    return unsetBit(destination, mapper.apply(e));
  }

  public static <T extends Enum> long unsetFlag(long destination,
                                                Function<T, Integer> mapper,
                                                T e) {
    return unsetBit(destination, mapper.apply(e));
  }

  public static <T extends Enum> int unsetFlags(int destination,
                                                Function<T, Integer> mapper,
                                                T... e) {
    for (T e1 : e) {
      destination = unsetBit(destination, mapper.apply(e1));
    }
    return destination;
  }

  public static <T extends Enum> byte unsetFlags(byte destination,
                                                 Function<T, Integer> mapper,
                                                 T... e) {
    for (T e1 : e) {
      destination = unsetBit(destination, mapper.apply(e1));
    }
    return destination;
  }

  public static <T extends Enum> long unsetFlags(long destination,
                                                 Function<T, Integer> mapper,
                                                 T... e) {
    for (T e1 : e) {
      destination = unsetBit(destination, mapper.apply(e1));
    }
    return destination;
  }

  public static <T extends Enum<T>> boolean isFlagSet(int destination,
                                                      Function<T, Integer> mapper,
                                                      T e) {
    return isSet(destination, mapper.apply(e));
  }

  public static <T extends Enum<T>> boolean isFlagSet(byte destination,
                                                      Function<T, Integer> mapper,
                                                      T e) {
    return isSet(destination, mapper.apply(e));
  }

  public static <T extends Enum<T>> boolean isFlagSet(long destination,
                                                      Function<T, Integer> mapper,
                                                      T e) {
    return isSet(destination, mapper.apply(e));
  }

  public static <T extends Enum<T>> Map<T, Boolean> areFlagsSet(int destination,
                                                                Function<T, Integer> mapper,
                                                                T... e) {
    Map<T, Boolean> m = new HashMap<>();
    for (T e1 : e) {
      m.put(e1, isSet(destination, mapper.apply(e1)));
    }
    return m;
  }

  public static <T extends Enum<T>> Map<T, Boolean> areFlagsSet(byte destination,
                                                                Function<T, Integer> mapper,
                                                                T... e) {
    Map<T, Boolean> m = new HashMap<>();
    for (T e1 : e) {
      m.put(e1, isSet(destination, mapper.apply(e1)));
    }
    return m;
  }

  public static <T extends Enum<T>> Map<T, Boolean> areFlagsSet(long destination,
                                                                Function<T, Integer> mapper,
                                                                T... e) {
    Map<T, Boolean> m = new HashMap<>();
    for (T e1 : e) {
      m.put(e1, isSet(destination, mapper.apply(e1)));
    }
    return m;
  }

  public static <T extends Enum<T>> List<T> getSetFlags(int destination,
                                                        Function<T, Integer> mapper,
                                                        T... e) {
    List<T> m = new ArrayList<>();
    for (T e1 : e) {
      if (isSet(destination, mapper.apply(e1))) {
        m.add(e1);
      }
    }
    return m;
  }

  public static <T extends Enum<T>> List<T> getSetFlags(byte destination,
                                                        Function<T, Integer> mapper,
                                                        T... e) {
    List<T> m = new ArrayList<>();
    for (T e1 : e) {
      if (isSet(destination, mapper.apply(e1))) {
        m.add(e1);
      }
    }
    return m;
  }

  public static <T extends Enum<T>> List<T> getSetFlags(long destination,
                                                        Function<T, Integer> mapper,
                                                        T... e) {
    List<T> m = new ArrayList<>();
    for (T e1 : e) {
      if (isSet(destination, mapper.apply(e1))) {
        m.add(e1);
      }
    }
    return m;
  }

}
