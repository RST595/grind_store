package com.bmxstore.grindStore.Service;


import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.FeignClient.Currency;
import com.bmxstore.grindStore.FeignClient.OutsideFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final OutsideFeignClient outsideFeignClient;
    private final ObjectMapper objectMapper;
    private final String LOCAL_CURRENCY = "RUB";
    private Double requestedCurrency;
    private Double rubCurrency;

    public Double getCurrencyRate(String currencyCode) throws JsonProcessingException {
        List<Currency> currencies = objectMapper.readValue(outsideFeignClient.getCurrencyRate(), new TypeReference<List<Currency>>(){});
        for (Currency currency : currencies) {
            if(currency.getCode().equals(currencyCode)){
                this.requestedCurrency  = currency.getCb_price();
            }
            if(currency.getCode().equals(LOCAL_CURRENCY)){
                this.rubCurrency  = currency.getCb_price();
            }
        }
        if(requestedCurrency != null && rubCurrency != null){
            return requestedCurrency / rubCurrency;
        } else {
            throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.DUPLICATED);
        }
    }
}
