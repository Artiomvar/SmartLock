package be.kdg.controller;

import be.kdg.domain.User;
import be.kdg.dto.UserLastEntryDto;
import be.kdg.dto.UserLastEntryOwnDto;
import be.kdg.repository.UserRepository;
import be.kdg.service.DashboardService;
import be.kdg.service.LogService;
import be.kdg.service.UserService;
import be.kdg.viewmodels.RegistrationViewModel;
import be.kdg.viewmodels.UserViewModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final LogService logService;
    private final UserService userService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, LogService logService, UserService userService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.logService = logService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails, Principal principal, Authentication authentication,
                           HttpServletRequest request) {
        long userCount = dashboardService.getUserCount();
        long sensorCount = dashboardService.getSensorCount();
        long rfidCount = dashboardService.getRfidCount();
        long fingerprintCount = dashboardService.getFingerprintCount();
        long nfcCount = dashboardService.getNfcCount();


        Optional<User> user = userService.getUserByUsername(userDetails.getUsername());
     
        List<UserLastEntryDto> lastEntryTimes = logService.getLastEntryTimes();
        if(user.isPresent()){
            List<UserLastEntryOwnDto> userLastEntryOwnDto = logService.getLastEntryByUserId(user.get().getId());
        model.addAttribute("userLastEntryOwnDto", userLastEntryOwnDto);
            //userbased
            model.addAttribute("fingerprintCountForUser", dashboardService.getFingerprintCountForUser(user.get().getId()));
            model.addAttribute("nfcCountForUser", dashboardService.getNfcCountForUser(user.get().getId()));
            model.addAttribute("sensorCountForUser", dashboardService.getSensorCountForUser(user.get().getId()));
            model.addAttribute("rfidCountForUser", dashboardService.getRfidCountForUser(user.get().getId()));
        }
        model.addAttribute("lastEntryTimes", lastEntryTimes);


        model.addAttribute("userCount", userCount);
        model.addAttribute("sensorCount", sensorCount);
        model.addAttribute("rfidCount", rfidCount);
        model.addAttribute("fingerprintCount", fingerprintCount);
        model.addAttribute("nfcCount", nfcCount);

        model.addAttribute("chartData", getChartData(userCount, sensorCount, rfidCount));

        return "index";
    }



    private List<List<Object>> getChartData(long userCount, long sensorCount, long rfidCount) {
        return List.of(
                List.of("Users", userCount),
                List.of("Sensors", sensorCount),
                List.of("RFID Cards", rfidCount)
        );
    }
}
