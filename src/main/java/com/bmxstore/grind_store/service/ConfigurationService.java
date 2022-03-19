package com.bmxstore.grind_store.service;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigurationService {


    @Autowired
    ObjectMapper objectMapper;

    public void JsonConf(){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }



}
