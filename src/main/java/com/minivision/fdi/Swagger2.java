package com.minivision.fdi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket createRestApi() {
      /*List<ResponseMessage> responses = Arrays.asList(
          new ResponseMessageBuilder()   
          .code(500)
          .message("服务内部错误")
          .build(),
         new ResponseMessageBuilder() 
          .code(403)
          .message("禁止访问(Forbidden)")
          .build(),
         new ResponseMessageBuilder() 
          .code(400)
          .message("请求参数错误(Bad Request)")
          .build(),
         new ResponseMessageBuilder() 
          .code(401)
          .message("未授权(Unauthorized)")
          .build(),
         new ResponseMessageBuilder() 
          .code(405)
          .message("参数格式错误(invalid input)")
          .build());*/
      return new Docket(DocumentationType.SWAGGER_2)
              .apiInfo(apiInfo())
              .useDefaultResponseMessages(false)
              //.globalResponseMessage(RequestMethod.GET, responses)
              //.globalResponseMessage(RequestMethod.POST, responses)
              .select()
              .apis(RequestHandlerSelectors.basePackage("com.minivision.fdi"))
              .paths(PathSelectors.any())
              .build();
              //.securitySchemes(Arrays.asList(baseAuth()))
              //.securityContexts(securityContext());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("小视IT峰会人脸识别签到后台服务")
                .description("小视IT峰会人脸识别签到后台服务RESTful APIs")
                //.termsOfServiceUrl("http:///")
                .contact(new Contact("PanXinmiao", null, "panxinmiao@minivision.com.cn"))
                .version("1.0")
                .build();
    }

    
/*    private SecurityScheme baseAuth() {
      return new BasicAuth("auth");
    }
  
    
    private SecurityContext makeSecurityContext(String path, AuthorizationScope... scopes) {
        SecurityReference securityReference = SecurityReference
          .builder()
          .reference("auth")
          .scopes(scopes)
          .build();

        return SecurityContext
          .builder()
          .securityReferences(Arrays.asList(securityReference))
          .forPaths(PathSelectors.ant(path))
          .build();
    }
    
    
    private List<SecurityContext> securityContext() {
      SecurityContext faceContext = makeSecurityContext("/api/v1/user/*", new AuthorizationScope("user", "须登录的用户相关"));
      return Arrays.asList(faceContext);
    }*/
}