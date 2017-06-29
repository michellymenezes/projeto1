package com.projeto1.projeto1;

import com.projeto1.projeto1.models.Sale;

import java.util.ArrayList;


public interface SaleListener {
    void OnGetSalesReady(boolean ready, ArrayList<Sale> sales);
    void OnPostSaleFinished(boolean finished);
}
