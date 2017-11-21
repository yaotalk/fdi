package com.minivision.fdi.mqtt.auth;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.moquette.spi.impl.subscriptions.Topic;
import io.moquette.spi.security.IAuthorizator;

public class DeviceAuthorizator implements IAuthorizator {
  private static final Logger logger = LoggerFactory.getLogger(DeviceAuthorizator.class);

  public boolean canWrite(Topic topic, String user, String client) {
//    logger.trace("Auth {}@{} can write to Topic {}", user, client, topic);
//    if (StringUtils.isEmpty(user)) {
//      return false;
//    }

    // TODO 判断topic client user是否匹配；
    return true;
  }

  public boolean canRead(Topic topic, String user, String client) {
    //    logger.trace("Auth {}@{} can read from Topic {}", user, client, topic);
    //    if (StringUtils.isEmpty(user)) {
    //      return false;
    //    }
    //
    //    // TODO 判断topic client user是否匹配；
    //    return true;
    //  }
    return true;
  }
}
