package server.server.service;

import server.server.dtos.response.TopProductsMonth;
import server.server.dtos.response.TopSellersMonth;

import java.util.List;

public interface TopPerformersService {
    List<TopSellersMonth> getTop3SellersOfTheMonth();

    List<TopProductsMonth> getTop3ProductsOfTheMonth();
}
