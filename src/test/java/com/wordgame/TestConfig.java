package com.wordgame;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.wordgame.core.JumbleEngine;

@TestConfiguration
public class TestConfig {

    @Bean
    public JumbleEngine jumbleEngine() {
        return new JumbleEngine();
    }

}
