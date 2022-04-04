package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CurrencyService currencyService;

    @Value("${variables.currencyTo}")
    private String currencyTo;

    @Test
    void getValidCurrencyRate() throws Exception {
        Double rate = currencyService.getCurrencyRate(currencyTo);
        assertNotNull(rate);
    }
}
