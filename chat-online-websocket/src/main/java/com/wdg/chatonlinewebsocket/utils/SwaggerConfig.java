package com.wdg.chatonlinewebsocket.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
/**
 * @Author: WuDG on 2019/7/28 18:24
 * @Description: 前后端swagger接口
 */
@Configuration
@SuppressWarnings("all ")
public class SwaggerConfig {
    /**
     * @Author: WuDG on 2019/7/27 2:40
     * @param: 
     * #return:
     * @Description: 设置扫描接口路径，一般是controller包下
     */
    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wdg.chatonlinewebsocket.controller"))
                .build();
    }

    /**
     * 该套 API 说明，包含作者、简介、版本、host、服务URL
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger2接口页面 @WuDG")
                .contact(new Contact("WuDG","null","1490727316@qq.com"))
                .version("0.1")
                .termsOfServiceUrl("47.101.204.74:8088/")
                .description("前后端分离开发")
                .build();
    }

}