package com.minivision.fdi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import com.minivision.fdi.entity.service.UserService;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserService userService;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().rememberMe().key("fdi")
      .and().authorizeRequests().anyRequest().permitAll()
      .and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
      .and().logout().permitAll();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    //auth.userDetailsService(userService);
    auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
  }
  
  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() 
    throws Exception {
      return super.authenticationManagerBean();
  }
  
  @Bean
  public StandardPasswordEncoder passwordEncoder(){
    return new StandardPasswordEncoder("minivision");
  }

}
