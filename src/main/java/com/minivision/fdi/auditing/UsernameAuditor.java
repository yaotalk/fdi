package com.minivision.fdi.auditing;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class UsernameAuditor implements AuditorAware<String> {

  @Override
  public String getCurrentAuditor() {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    return "";
  }

}
