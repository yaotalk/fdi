package com.minivision.fdi.common;

public final class IdUtils {

  public static boolean isValidId(Long id) {
    return id != null && id > 0;
  }
  
}
