package com.minivision.fdi.mqtt.protocol;

import static org.springframework.security.crypto.util.EncodingUtils.subArray;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.util.Assert;

/**
 * @author Panxinmiao
 * 激活码相关的工具类
 * 根据设备的model和sn，指定公钥，生成指定长度的一串，对用户友好的只由数字和大写字母组成的激活码。
 * 激活码中有加盐部分，保证了即使是同一设备，每次生成的激活码也不一样，大大提高了破解和伪造激活码的难度。
 * 
 */
public class ActiveCodeUtil {

  private final int activeCodeLength;
  private final int saltKeyLength;
  private final SecurityUtil securityUtil;
  private final SecureRandom random = new SecureRandom();

  private ActiveCodeUtil(String secretKey, int activeCodeLength, int saltKeyLength) {
    this.activeCodeLength = activeCodeLength;
    this.saltKeyLength = saltKeyLength;
    this.securityUtil = new SecurityUtil(secretKey);
  }

  private byte[] generateSalt() {
    byte[] bytes = new byte[saltKeyLength];
    int[] array = random.ints(4, 0, 36).map(n -> n < 10 ? n + 48 : n + 55).toArray();
    for (int i = 0; i < saltKeyLength; i++) {
      bytes[i] = (byte) array[i];
    }
    String salt = new String(bytes);
    byte[] saltDigist = Base64.getDecoder().decode(salt.getBytes());
    return saltDigist;
  }

  public String generateActiveCode(String model, String sn) {
    String rawKey = model + "::" + sn;
    byte[] salt = generateSalt();
    byte[] digest = securityUtil.digest(rawKey, salt);
    byte[] base64 = Base64.getEncoder().encode(digest);
    return new String(base64).replace("+", "").replace("/", "").substring(0, activeCodeLength).toUpperCase();
  }

  public boolean match(String activeCode, String model, String sn) {
    String rawKey = model + "::" + sn;
    byte[] digestedCode = Base64.getDecoder().decode(activeCode.toUpperCase().getBytes());
    byte[] salt = subArray(digestedCode, 0, saltKeyLength/4 * 3);
    byte[] digested = securityUtil.digest(rawKey, salt);
    byte[] base64 = Base64.getEncoder().encode(digested);
    String code = new String(base64).replace("+", "").replace("/", "");
    return code.toUpperCase().startsWith(activeCode);
  }



  public static ActiveCodeUtilBuilder builder() {
    return new ActiveCodeUtilBuilder();
  }

  public static class ActiveCodeUtilBuilder {
    private String secretKey = "minivision";
    private int activeCodeLength = 16;
    private int saltKeyLength = 4; // 必须是4的倍数

    private ActiveCodeUtilBuilder() {};

    public ActiveCodeUtilBuilder activeCodeLength(int length) {
      this.activeCodeLength = length;
      return this;
    }

    public ActiveCodeUtilBuilder saltKeyLength(int length) {
      Assert.isTrue(length % 4 == 0, "Salt key length 必须是4的倍数");
      this.saltKeyLength = length;
      return this;
    }

    public ActiveCodeUtilBuilder secretKey(String secret) {
      this.secretKey = secret;
      return this;
    }

    public ActiveCodeUtil build() {
      return new ActiveCodeUtil(secretKey, activeCodeLength, saltKeyLength);
    }

  }
}
