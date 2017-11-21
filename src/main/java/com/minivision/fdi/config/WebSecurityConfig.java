package com.minivision.fdi.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.entity.User;
import com.minivision.fdi.entity.service.UserService;
import com.minivision.fdi.rest.result.common.RestResult;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserService userService;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private SessionRegistry sessionRegistry;
  @Autowired
  private SessionInformationExpiredStrategy sessionExpiredStrategy;
  @Autowired
  private SecurityContextRepository securityContextRepository;
  
  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.securityContext().securityContextRepository(securityContextRepository)
    .and().sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry).expiredSessionStrategy(sessionExpiredStrategy).and()
    .and().csrf().disable().cors()
    .and().rememberMe().key("fdi")
    .and().authorizeRequests().antMatchers("/login.html").permitAll()
    .and().authorizeRequests().anyRequest().authenticated()
    .and().formLogin()
    .successHandler((request, response, authentication) -> {
      if(isAjaxRequest(request)){
        ObjectMapper objectMapper = new ObjectMapper();  
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        User user = (User) authentication.getPrincipal();
        byte[] writeValueAsBytes = objectMapper.writeValueAsBytes(new RestResult<>(user));
        response.getOutputStream().write(writeValueAsBytes);
        response.getOutputStream().flush();
      }else{
        redirectStrategy.sendRedirect(request, response, "/swagger-ui.html");
      }
      
    }).failureHandler((request, response, exception) -> {
      response.setStatus(401);
      if(isAjaxRequest(request)){
        ObjectMapper objectMapper = new ObjectMapper();  
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        byte[] writeValueAsBytes = objectMapper.writeValueAsBytes(new RestResult<>(exception, 401));
        response.getOutputStream().write(writeValueAsBytes);
        response.getOutputStream().flush();
      }else{
        redirectStrategy.sendRedirect(request, response, "/login.html");
      }
      //objectMapper.writeValue(response.getOutputStream(), new RestResult<>(exception));
    }).permitAll()
    .and().exceptionHandling().authenticationEntryPoint(ajaxAuthenticationEntryPoint()).accessDeniedHandler(ajaxAccessDeniedHandler())
    .and().logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()).permitAll();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
  }
  
  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() 
    throws Exception {
      return super.authenticationManagerBean();
  }
  
  @Bean
  public AuthenticationEntryPoint ajaxAuthenticationEntryPoint(){
    return new AuthenticationEntryPoint() {
      
      @Override
      public void commence(HttpServletRequest request, HttpServletResponse response,
          AuthenticationException authException) throws IOException, ServletException {
        
        if(request.getServletPath().contains("swagger-ui")){
          redirectStrategy.sendRedirect(request, response, "login.html");
        }else{
          ObjectMapper objectMapper = new ObjectMapper();  
          response.setHeader("Content-Type", "application/json;charset=UTF-8");
          response.setStatus(401);
          RestResult<Object> restResult = new RestResult<>(authException);
          restResult.setStatus(401); //未鉴权
          byte[] writeValueAsBytes = objectMapper.writeValueAsBytes(restResult);
          response.getOutputStream().write(writeValueAsBytes);
          response.getOutputStream().flush();
        }
      }
    };
  }

  @Bean
  public AccessDeniedHandler ajaxAccessDeniedHandler() {
    return new AccessDeniedHandler() {
      @Override
      public void handle(HttpServletRequest request, HttpServletResponse response,
          AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();  
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(403);
        RestResult<Object> restResult = new RestResult<>(accessDeniedException);
        restResult.setStatus(403); //权限不足 Forbidden
        byte[] writeValueAsBytes = objectMapper.writeValueAsBytes(restResult);
        response.getOutputStream().write(writeValueAsBytes);
        response.getOutputStream().flush();
      }
    };
  }
  
  private boolean isAjaxRequest(HttpServletRequest request){
    return (request.getHeader("x-requested-with") != null) && (request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"));
  }
  
  @Bean
  public SessionRegistry sessionRegistry(){
    return new SessionRegistryImpl();
  }
  
  @Bean
  public SessionInformationExpiredStrategy sessionExpiredStrategy(){
    return new SessionInformationExpiredStrategy() {
      @Override
      public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
          throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();
        if(request.getServletPath().contains("swagger-ui")){
          redirectStrategy.sendRedirect(request, response, "login.html");
        }else{
          ObjectMapper objectMapper = new ObjectMapper();  
          response.setHeader("Content-Type", "application/json;charset=UTF-8");
          response.setStatus(401);
          RestResult<Object> restResult = new RestResult<>("Login has expired, Please login again.");
          restResult.setStatus(401); //未鉴权
          byte[] writeValueAsBytes = objectMapper.writeValueAsBytes(restResult);
          response.getOutputStream().write(writeValueAsBytes);
          response.getOutputStream().flush();
        }
      }
    };
  }
}
