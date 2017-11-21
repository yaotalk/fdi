package com.minivision.fdi.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Unused {
  
  @AliasFor("value")
  String description() default "";
  
  @AliasFor("description")
  String value() default "";

}
