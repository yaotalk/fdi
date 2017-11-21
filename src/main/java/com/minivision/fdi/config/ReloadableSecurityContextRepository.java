package com.minivision.fdi.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import com.minivision.fdi.entity.service.UserService;

@Component
public class ReloadableSecurityContextRepository implements SecurityContextRepository {
  private SecurityContextRepository delegate = new HttpSessionSecurityContextRepository();
  @Autowired
  private UserService userService;
  
  //private Set<String> needReload = new HashSet<>();

  @Override
  public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
      SecurityContext securityContext = delegate.loadContext(requestResponseHolder);
      
      Authentication principal = securityContext.getAuthentication();
      if(principal == null) {
          return securityContext;
      }

      //TODO 根据是否需要重新加载的标识决定是否需要重新加载？
      //if(needReload.contains(principal.getName())){
      try{
        UserDetails userDetails = userService.loadUserByUsername(principal.getName());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        securityContext.setAuthentication(token);
      }catch (UsernameNotFoundException e){
        securityContext.setAuthentication(null);
      }
      saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());
      //needReload.remove(principal.getName());
        //log.info("authorized changed, reload it. principal: [{}]", principal.getName());
      //}
      return securityContext;
  }

/*  public void needReload(String username){
    needReload.add(username);
  }*/
  
  @Override
  public void saveContext(final SecurityContext context, final HttpServletRequest request, final HttpServletResponse response) {
      delegate.saveContext(context, request, response);
  }

  @Override
  public boolean containsContext(final HttpServletRequest request) {
      return delegate.containsContext(request);
  }
}