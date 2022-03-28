package com.bmxstore.grind_store.service;


import com.bmxstore.grind_store.ex_handler.ErrorMessage;
import com.bmxstore.grind_store.ex_handler.ServiceError;
import com.bmxstore.grind_store.feign_client.Currency;
import com.bmxstore.grind_store.feign_client.OutsideFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final OutsideFeignClient outsideFeignClient;
    private final ObjectMapper objectMapper;
    private Double requestedCurrency;
    private Double rubCurrency;

    @Value("${variables.currencyFrom}")
    private String currencyFrom;

    public Double getCurrencyRate(String currencyCode) throws JsonProcessingException {
        List<Currency> currencies = objectMapper.readValue(outsideFeignClient.getCurrencyRate(), new TypeReference<List<Currency>>(){});
        for (Currency currency : currencies) {
            if(currency.getCode().equals(currencyCode)){
                this.requestedCurrency  = currency.getCb_price();
            }
            if(currency.getCode().equals(currencyFrom)){
                this.rubCurrency  = currency.getCb_price();
            }
        }
        if(requestedCurrency != null && rubCurrency != null){
            return requestedCurrency / rubCurrency;
        } else {
            throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("RATES_WASNT_FOUNDED"));
        }
    }
}
