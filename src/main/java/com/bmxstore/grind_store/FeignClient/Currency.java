package com.bmxstore.grind_store.FeignClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    String title;
    String code;
    double cb_price;
    double nbu_buy_price;
    double nbu_cell_price;
    String date;
}
