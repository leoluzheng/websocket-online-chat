package com.wdg.chatonlinewebsocket.utils;

/**
 * ClassName: WebSocketConfig
 * Description:
 * date: 2019/12/6 13:55
 *
 * @author WuDG(1490727316)
 * @version 0.1
 * @since JDK 1.8
 */
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;

/**
 * 开启WebSocket支持
 * @author zhengkai
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebSocketConfig implements ServletContextInitializer {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        System.out.println("org.apache.tomcat.websocket.textBufferSize");
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","20000000");
    }

}