package com.minivision.fdi.mqtt.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class FeatureEncoderDecoderTest extends Assert{

  @Test
  public void test() {
    float[] fs = new float[] {0.1234f, 0.4321f, 0.3579f, 0.9753f};
    String s = FeatureUtils.encode(fs);
    float[] decode = FeatureUtils.decode(s);
    assertTrue(Arrays.equals(fs, decode));
  }
  
  @Test
  public void test1() {
    Random r = new Random();
    float[] features = new float[512];
    for (int i=0; i<features.length; i++) {
      features[i] = r.nextFloat();
    }
    String encode = FeatureUtils.encode(features);
    System.out.println(encode.length());
    float[] decode = FeatureUtils.decode(encode);
    assertTrue(Arrays.equals(features, decode));
  }
}
