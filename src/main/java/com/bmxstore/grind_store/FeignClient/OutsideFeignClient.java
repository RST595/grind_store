package com.bmxstore.grind_store.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "currency", url = "https://nbu.uz")
public interface OutsideFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/en/exchange-rates/json")
    String getCurrencyRate();

}

