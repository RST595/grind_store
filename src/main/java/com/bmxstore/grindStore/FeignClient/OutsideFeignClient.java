package com.bmxstore.grindStore.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "currency", url = "http://www.floatrates.com/widget")
public interface OutsideFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/00001652/00ca82c82d68ce997e0e0c5cc09aba29/rub.json")
    String getUsdEur();

}

