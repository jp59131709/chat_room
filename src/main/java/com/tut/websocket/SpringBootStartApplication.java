package com.tut.websocket;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


public class SpringBootStartApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意ここでは、mainメソッドで実行されていたApplication起動クラスを指します
        return builder.sources(WebsocketApplication.class);
    }

}
