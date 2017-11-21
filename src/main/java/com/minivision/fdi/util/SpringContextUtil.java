package com.minivision.fdi.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
  
  private static ApplicationContext applicationContext = null;

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    SpringContextUtil.applicationContext = applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public static Object getBean(String name) throws BeansException {
    return applicationContext.getBean(name);
  }

  public static <T> T getBean(Class<T> clazz) throws BeansException {
    return applicationContext.getBean(clazz);
  }

  public static boolean containsBean(String name) {
    return applicationContext.containsBean(name);
  }

  public static boolean isSingleton(String name){
    return applicationContext.isSingleton(name);
  }

}
