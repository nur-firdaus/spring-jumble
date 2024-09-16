package com.wordgame;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wordgame.core.JumbleEngine;

@Configuration
public class AppConfig {

    @Bean
    public JumbleEngine jumbleEngine() {
        return new JumbleEngine();
    }

}
