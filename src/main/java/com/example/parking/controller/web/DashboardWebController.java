package com.example.parking.controller.web;

import com.example.parking.service.ParkingService;
import com.example.parking.service.ParkingSpaceService;
import com.example.parking.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardWebController {

    private final StatisticsService statisticsService;
    private final ParkingService parkingService;
    private final ParkingSpaceService spaceService;

    public DashboardWebController(StatisticsService statisticsService,
                                  ParkingService parkingService,
                                  ParkingSpaceService spaceService) {
        this.statisticsService = statisticsService;
        this.parkingService = parkingService;
        this.spaceService = spaceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", statisticsService.dashboard());
        model.addAttribute("occupancy", statisticsService.occupancy());
        model.addAttribute("ongoing", parkingService.findOngoing());
        model.addAttribute("zonedSpaces", spaceService.findGroupedByZone());
        model.addAttribute("activeMenu", "dashboard");
        return "dashboard";
    }
}
