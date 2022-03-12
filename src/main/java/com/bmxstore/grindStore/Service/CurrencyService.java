package com.bmxstore.grindStore.Service;


import com.bmxstore.grindStore.FeignClient.AllCurrencies;
import com.bmxstore.grindStore.FeignClient.OutsideFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final OutsideFeignClient outsideFeignClient;
    private final ObjectMapper objectMapper;

    public Double getRate(String currency) throws JsonProcessingException {
        AllCurrencies usdEur = objectMapper.readValue(outsideFeignClient.getUsdEur(), AllCurrencies.class);
        Map<String, Double> map = new HashMap<>();
        map.put(usdEur.getUsd().getCode(), usdEur.getUsd().getRate());
        map.put(usdEur.getEur().getCode(), usdEur.getEur().getRate());
        map.put("RUB", 1.0);

        return map.get(currency);
    }
}
