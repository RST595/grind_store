package com.bmxstore.grindStore.FeignClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    String code;
    String alphaCode;
    int numericCode;
    String name;
    double rate;
    double inverseRate;
    String date;
}
