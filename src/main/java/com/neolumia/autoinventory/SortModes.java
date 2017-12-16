package com.neolumia.autoinventory;

public final class SortModes {

  // Type -> Data -> Max Size -> Amount
  @SuppressWarnings("deprecation")
  public static final SortMode DEFAULT = (o1, o2) -> {
    int c = Integer.compare(o1.getTypeId(), o2.getTypeId());
    if (c == 0) {
      c = Byte.compare(o1.getData().getData(), o2.getData().getData());
      if (c == 0) {
        c = Integer.compare(o1.getMaxStackSize(), o2.getMaxStackSize());
        if (c == 0) {
          c = Integer.compare(o2.getAmount(), o1.getAmount());
        }
      }
    }
    return c;
  };
}
