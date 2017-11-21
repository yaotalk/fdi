package com.minivision.fdi.mqtt.protocol;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ActiveCodeUtilTest extends Assert{
  
  private ActiveCodeUtil activeCodeUtil;

  @Before
  public void setUp(){
    activeCodeUtil = ActiveCodeUtil.builder().secretKey("minivsion").activeCodeLength(16).saltKeyLength(4).build();
  }
  
  @Test
  public void test() {
    String model = "pad";
    String sn = "a08d1670de8d";
    String activeCode1 = activeCodeUtil.generateActiveCode(model, sn);
    String activeCode2 = activeCodeUtil.generateActiveCode(model, sn);
    
    assertTrue(activeCode1.length() == 16);
    assertFalse(activeCode1.equals(activeCode2));
    assertTrue(activeCodeUtil.match(activeCode1, model, sn));
    assertTrue(activeCodeUtil.match(activeCode2, model, sn));
    
    assertFalse(activeCodeUtil.match(activeCode1, model, "test12345678"));
    assertFalse(activeCodeUtil.match(activeCode2, "dap", sn));
  }
  
}
