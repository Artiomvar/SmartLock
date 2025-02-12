package be.kdg.controller;

import be.kdg.domain.Sensor;
import be.kdg.domain.UnlockRequest;
import be.kdg.domain.User;
import be.kdg.mapper.SensorMapper;
import be.kdg.service.Impl.UnlockService;
import be.kdg.service.SensorService;
import be.kdg.service.UserService;
import be.kdg.viewmodels.SensorViewModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sensor")
public class SensorController {
    private final SensorService sensorService;
    private final UnlockService unlockService; // Inject UnlockService
    private final UserService userService;

    @Autowired
    public SensorController(SensorService sensorService, UnlockService unlockService, UserService userService) {
        this.sensorService = sensorService;
        this.unlockService = unlockService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllSensor(Model model, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();
        Optional<User> user= userService.findByName(username);

        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            List<Sensor> sensors = sensorService.getAllSensors();
            model.addAttribute("sensors", sensors);
        } else if ("ROLE_USER".equalsIgnoreCase(role)) {
            List<Sensor> sensors = sensorService.findSensorsByUserId(user.get().getId());
            model.addAttribute("sensors", sensors);
        }

        return "sensor/sensor_list";
    }

    @GetMapping("/add-sensor")
    public String sensorAdd(Model model) {
        model.addAttribute("sensor", new SensorViewModel());
        return "sensor/add_sensor";
    }

    @PostMapping("/add")
    public String addSensor(@Valid @ModelAttribute("sensor") SensorViewModel viewModel, BindingResult bindingResult, Model model,Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user= userService.findByName(username);
        if (bindingResult.hasErrors()) {
            model.addAttribute("sensor", viewModel);
            return "sensor/add_sensor";
        }

        sensorService.save(viewModel,user.get());
        return "redirect:/sensor";
    }

    @GetMapping("/update/{id}")
    public String updateSensorForm(@PathVariable("id") Integer id, Model model) {
        Sensor sensor = sensorService.findById(id);
        SensorViewModel viewmodel = SensorMapper.toViewModel(sensor);
        model.addAttribute("sensor", viewmodel);
        return "sensor/update_sensor";
    }

    @PostMapping("/update/{id}")
    public String updateSensor(@PathVariable("id") Long id, @Valid @ModelAttribute("sensor") SensorViewModel viewModel,
                               BindingResult bindingResult, Model model,Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user= userService.findByName(username);
        if (bindingResult.hasErrors()) {
            model.addAttribute("sensor", viewModel);
            return "sensor/update_sensor";
        }
        sensorService.updateSensor(viewModel.getId(),viewModel);
        return "redirect:/sensor";
    }

    @GetMapping("/delete/{id}")
    public String deleteSensor(@PathVariable("id") Integer id) {
        sensorService.deleteById(id);
        return "redirect:/sensor";
    }




    @PostMapping("/update-status")
    public ResponseEntity<String> updateSensorStatus(@RequestBody String jsonPayload,Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user= userService.findByName(username);
        try {
            // Step 1: Parse the raw JSON using Jackson's ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonPayload);

            // Step 2: Extract the 'name' and 'status' fields manually
            String name = jsonNode.get("name").asText();    // Get the sensor's name from JSON
            boolean status = jsonNode.get("status").asBoolean();  // Get the status from JSON

            // Step 3: Find the sensor by its name
            Sensor sensor = sensorService.getSensorByName(name);

            SensorViewModel viewmodel = SensorMapper.toViewModel(sensor);
            if (sensor != null) {
                // Step 4: Update the sensor's status and save it
                sensor.setStatus(status);  // Update the sensor's status
                sensorService.save(viewmodel,user.get());  // Save the updated sensor

                return ResponseEntity.ok("Sensor status updated successfully for sensor: " + name);
            } else {
                return ResponseEntity.status(404).body("Sensor with name '" + name + "' not found");
            }
        } catch (Exception e) {
            // Handle any parsing or processing exceptions
            return ResponseEntity.status(400).body("Failed to parse JSON or update sensor: " + e.getMessage());
        }
    }

    @PostMapping("/unlock/{id}")
    public String unlockSensor(@PathVariable("id") Long sensorId) {
        // Logic to unlock the sensor by ID
        UnlockRequest request = new UnlockRequest();
        Sensor sensor = sensorService.findById(sensorId.intValue());
        request.setMac(sensor.getMac());
        request.setIpAddress(sensor.getIpAddress());
        request.setMethod("web");
        unlockService.handleUnlock(request);
        System.out.println();
        return "redirect:/sensor";
    }

}
