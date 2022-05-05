package com.bmxstore.grind_store.feign_client;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    private String title;
    private String code;
    @JsonAlias("cb_price")
    private double cbPrice;
    @JsonAlias("nbu_buy_price")
    private double nbuBuyPrice;
    @JsonAlias("nbu_cell_price")
    private double nbuCellPrice;
    private String date;
}
