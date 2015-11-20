package com.ifesdjeen.binaer.support;

public class FlagHelper {

  public static int setBit(int value, int bit) {
    return value | (1 << bit);
  }

  public static boolean isSet(int value, int bit) {
    return (value & (1 << bit)) != 0;
  }
}
