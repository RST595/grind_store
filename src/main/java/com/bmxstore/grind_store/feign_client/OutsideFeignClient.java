package com.bmxstore.grind_store.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "currency", url = "https://nbu.uz")
public interface OutsideFeignClient {

    @GetMapping (value = "/en/exchange-rates/json")
    String getCurrencyRate();

}

