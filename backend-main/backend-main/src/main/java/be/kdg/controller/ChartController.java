package be.kdg.controller;

import be.kdg.domain.User;
import be.kdg.enums.Country;
import be.kdg.enums.UserType;
import be.kdg.service.DashboardService;
import be.kdg.service.LogService;
import be.kdg.service.UserService;
import ch.qos.logback.core.model.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
public class ChartController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final LogService logService;

    @Autowired
    public ChartController(DashboardService dashboardService, UserService userService, LogService logService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.logService = logService;
    }

    @GetMapping("api/admin/bar-chart-data")
    public int[] getAdminChartData() {
        long userCount = dashboardService.getUserCount();
        long sensorCount = dashboardService.getSensorCount();
        long rfidCount = dashboardService.getRfidCount();
        long fingerprintCount = dashboardService.getFingerprintCount();
        long nfcCount = dashboardService.getNfcCount();
        return new int[]{(int) userCount, (int) sensorCount, (int) rfidCount,(int) fingerprintCount,(int) nfcCount};
        
    }

    @GetMapping("api/user/bar-chart-data")
    public int[] getUserChartData(Model model, Authentication authentication) {
      String name=authentication.getName();
      Optional<User> user = userService.getUserByUsername(name);
        long sensorCount = dashboardService.getSensorCountForUser(user.get().getId());
        long rfidCount = dashboardService.getRfidCountForUser(user.get().getId());
        long nfcCount = dashboardService.getNfcCountForUser(user.get().getId());
        long fingerprintCount = dashboardService.getFingerprintCountForUser(user.get().getId());
        return new int[]{(int) sensorCount, (int) rfidCount, (int) nfcCount, (int) fingerprintCount};
    }

    @GetMapping("api/user/line-chart-data")
    public int[] getUserLineChartData(Model model, Authentication authentication) {
        String name = authentication.getName();
        Optional<User> user = userService.getUserByUsername(name);
        long userId = user.get().getId();

        List<Object[]> weekDays = logService.getWeeklyLoginStats(userId);
        return weekDays.stream()
                .mapToInt(day -> ((Number) day[1]).intValue())
                .toArray();
    }

    @GetMapping("api/admin/line-chart-data")
    public int[] getAdminLineChartData() {
        List<Object[]> weekDays = logService.getWeeklyLoginStatsAllUsers();
        return weekDays.stream()
                .mapToInt(day -> ((Number) day[1]).intValue())
                .toArray();
    }

    @GetMapping("api/admin/pie-chart-data")
    public int[] getAdminPieChartData() {
        Map<UserType, Double> percentages = userService.findUserTypePercentages();
        return percentages.values().stream().mapToInt(Double::intValue).toArray();
    }

    @GetMapping("api/user/pie-chart-data")
    public int[] getUserPieChartData(Model model, Authentication authentication) {
//        String name = authentication.getName();
//        Optional<User> user = userService.getUserByUsername(name);
//        long userId = user.get().getId();
//        List<Object[]> logsByDaytime = logService.findLogsGroupedByDaytimeForAllUsers(userId);
        return new int[]{30, 40, 20, 10}; // Bu, Morning, Afternoon, Evening ve Night için olmalı.
    }


    @GetMapping("api/admin/map")
    public List<Map<String, Object>> getAdminMap() {
        List<String> countries = userService.findAllUsersWithCountries();
        List<Map<String, Object>> arr = new ArrayList<>();

        for (String countryName : countries) {
            try {
                Country country = Country.valueOf(countryName.toUpperCase());
                arr.add(Map.of(
                        "label", countryName,
                        "lat", country.getLatitude(),
                        "lng", country.getLongitude()
                ));
            } catch (IllegalArgumentException e) {
                // Ignore countries not found in the Country enum
            }
        }

        return arr;
    }
}