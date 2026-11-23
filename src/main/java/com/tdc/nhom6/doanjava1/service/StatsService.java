package com.tdc.nhom6.doanjava1.service;
import java.util.List;
import java.util.Map;
public interface StatsService {
    List<Map<String,Object>> getMostBorrowed(int limit);
    Map<String,Object> getInventory();
    Map<String,Object> getMonthlyActivity();
    Map<String,Object> getRevenue();
    long countOverdue();
}
