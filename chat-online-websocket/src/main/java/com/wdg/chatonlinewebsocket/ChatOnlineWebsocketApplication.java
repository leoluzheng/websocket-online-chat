package com.wdg.chatonlinewebsocket;

import com.wdg.chatonlinewebsocket.utils.WebsocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@SpringBootApplication(exclude=
        {DataSourceAutoConfiguration.class})
@ComponentScan({"com.wdg.chatonlinewebsocket.dao","com.wdg.chatonlinewebsocket.*"})
@SuppressWarnings("all")
public class ChatOnlineWebsocketApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ChatOnlineWebsocketApplication.class);
    public static String userIp = "";
    public static void main(String[] args) {
        SpringApplication.run(ChatOnlineWebsocketApplication.class, args);
    }

    @GetMapping("/")
    @ResponseBody
    public String index(){
        return "Welcome chat-online";
    }
    @RequestMapping("/index")
    public String select(Model model, HttpServletRequest request){
        userIp = WebsocketUtil.getIpAddress(request);
        LOG.info("真实IP:"+ userIp);
        model.addAttribute("userIp", userIp);
        return "chat";
    }
}
