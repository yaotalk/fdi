package com.minivision.fdi.mqtt.protocol;

import java.nio.ByteBuffer;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

public class FeatureUtils {
  
  public static String encode(float[] feature) {
    if(feature == null){
      return null;
    }
    ByteBuffer buffer = ByteBuffer.allocate(feature.length * 4);
    for(float f: feature){
      buffer.putFloat(f);
    }
    return Base64.getEncoder().encodeToString(buffer.array());
  }
  
  public static float[] decode(String feature) {
    if(StringUtils.isEmpty(feature)){
      return null;
    }
    byte[] bytes = Base64.getDecoder().decode(feature);
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    int size = bytes.length / 4;
    
    float[] fs = new float[size];
    for(int i=0;i<size;i++){
      fs[i] = buffer.getFloat();
    }
    return fs;
  }
  
  public static void main(String[] args) {
    
  }
}
