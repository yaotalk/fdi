package com.minivision.fdi.config;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.common.CommonConstants;
import com.minivision.fdi.mqtt.protocol.ActiveCodeUtil;

@Configuration
public class CommonConfig {

  @Bean
  public ObjectMapper objectMapper(){
    ObjectMapper om = new ObjectMapper();
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    om.setDateFormat(new SimpleDateFormat(CommonConstants.FULL_DATE_FORMAT));
    //om.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    return om;
  }

  @Bean
  public AntPathMatcher pathMatcher(){
    return new AntPathMatcher();
  }

  @Bean
  public StandardPasswordEncoder passwordEncoder(){
    return new StandardPasswordEncoder("minivision");
  }

  @Bean
  public ActiveCodeUtil activeCodeUtil(){
    return ActiveCodeUtil.builder().secretKey("minivsion").activeCodeLength(16).saltKeyLength(4).build();
  }

  @Bean("mqttSenderWorker")
  public ExecutorService mqttSenderWorker() {
    return Executors.newCachedThreadPool(new FdiThreadFactory("mqttSenderWorker"));
  }

  public static class FdiThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public FdiThreadFactory(String poolNamePrefix) {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();
      namePrefix = poolNamePrefix + "-thread-";
    }

    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r,
          namePrefix + threadNumber.getAndIncrement(),
          0);
      if (t.isDaemon())
        t.setDaemon(false);
      if (t.getPriority() != Thread.NORM_PRIORITY)
        t.setPriority(Thread.NORM_PRIORITY);
      return t;
    }
  }

}
