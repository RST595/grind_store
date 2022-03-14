package com.bmxstore.grindStore.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "currency", url = "https://nbu.uz")
public interface OutsideFeignClient {

    @GetMapping(value = "/en/exchange-rates/json")
    String getCurrencyRate();

}

