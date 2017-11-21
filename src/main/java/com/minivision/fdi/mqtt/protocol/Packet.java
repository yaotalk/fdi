package com.minivision.fdi.mqtt.protocol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Packet<T> {
  private Head head;
  private T body;

  public Packet() {}

  public Packet(Head head) {
    setHead(head);
  }

  public Packet(Head head, T body) {
    setHead(head);
    setBody(body);
  }

  @Setter
  @Getter
  @ToString
  public static final class Head {
    private int version = 1;
    private long id;
    private int code;
    private int type;

    public Head() {}

    public Head(long id, int code, int type) {
      setId(id);
      setCode(code);
      setType(type);
    }

    public static class CmdCode{
      public static final int CONNECT = 0;
      public static final int DISCONNECT = 1;
      public static final int LOST = 2;
      public static final int STATUS = 3;
      public static final int REPORT_STATS = 4;
      
      public static final int CONFIG_SET = 101;
      public static final int CONFIG_AQUIRE = 102;
      public static final int BIND_FACESET = 103;
      public static final int FACESET_BIND = 104;
      public static final int SIGN_IN = 105;
      public static final int SIGN_FAIL =106;
      public static final int REFRESH_FACESET = 107;

      public static final int FACE_SEARCH = 108;
      public static final int QR_SEARCH = 109;
      
      public static final int DETECT_ACTIVE = 110;
      public static final int ACTIVE = 111;
      
      public static final int UNBIND_FACESET = 112;

      public static final int LIST_MEETING = 112;
    }
    
    public static class Type{
      public static final int NOTIFY = 0;
      public static final int REQUEST = 1;
      public static final int RESPONSE_OK = 2;
      public static final int RESPONSE_BAD_REQ = 3;
      public static final int RESPONSE_TIMEOUT = 4;
      public static final int RESPONSE_PROCESS_FAIL = 5;
      // boundary of vendor specified error
      public static final int RESPONSE_VENDOR_SPEC_ERROR = 10;
      
      public static final int RESPONSE_ACTIVECODE_ERROR = 11;
      
      public static String getSysDesc(int errType) {
        return null;
      }
      
      public static boolean isResponse(int type) {
        return type >= RESPONSE_OK;
      }
  
      public static boolean isResponseOk(Packet<?> p) {
          return p.getHead().getType() == RESPONSE_OK;
      }
    }

  }

}

