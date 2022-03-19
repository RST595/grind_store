package com.bmxstore.grind_store.config;

import com.bmxstore.grind_store.service.ConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Bean
    public ConfigurationService jsonConfig(){
        return new ConfigurationService();
    }
}
