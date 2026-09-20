package com.example.parking.controller.web;

import com.example.parking.dto.response.RevenueResponse;
import com.example.parking.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/statistics")
public class StatisticsWebController {

    private final StatisticsService statisticsService;

    public StatisticsWebController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public String statistics(@RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                             Model model) {
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();
        RevenueResponse revenue = statisticsService.revenue(start, end);
        double maxDaily = revenue.daily().stream()
                .map(RevenueResponse.DailyRevenue::amount)
                .mapToDouble(BigDecimal::doubleValue)
                .max()
                .orElse(0d);

        model.addAttribute("occupancy", statisticsService.occupancy());
        model.addAttribute("revenue", revenue);
        model.addAttribute("maxDaily", maxDaily);
        model.addAttribute("stats", statisticsService.dashboard());
        model.addAttribute("from", start);
        model.addAttribute("to", end);
        model.addAttribute("activeMenu", "statistics");
        return "statistics";
    }
}
