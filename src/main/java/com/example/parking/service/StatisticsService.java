package com.example.parking.service;

import com.example.parking.dto.response.DashboardResponse;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.RevenueResponse;

import java.time.LocalDate;

public interface StatisticsService {

    DashboardResponse dashboard();

    OccupancyResponse occupancy();

    RevenueResponse revenue(LocalDate from, LocalDate to);
}
