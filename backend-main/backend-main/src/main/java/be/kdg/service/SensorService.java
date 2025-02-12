package be.kdg.service;

import be.kdg.domain.Sensor;
import be.kdg.domain.User;
import be.kdg.viewmodels.SensorViewModel;

import java.util.List;
import java.util.Optional;

public interface SensorService {
    List<Sensor> getAllSensors();

    Sensor getSensorByName(String name);

    Sensor findById(Integer id);
    Sensor getSensorByMac(String mac);

    void save(SensorViewModel viewModel, User user);

    void updateSensor(Integer sensorId, SensorViewModel viewModel);

    long getSensorCount();
    void deleteById(Integer id);
    long getSensorCountByUser(Long userId);

    List<Sensor> getSensorsByUserId(Long userId);  

    List<Sensor> findSensorsByUserId(Long userId);
    
    
}
