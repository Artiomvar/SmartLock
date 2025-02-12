package be.kdg.service.Impl;

import be.kdg.domain.Sensor;
import be.kdg.domain.User;
import be.kdg.mapper.SensorMapper;
import be.kdg.repository.SensorRepository;
import be.kdg.service.SensorService;
import be.kdg.viewmodels.SensorViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;

    @Autowired
    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }


    @Override
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @Override
    public Sensor getSensorByName(String name) {
        return sensorRepository.findByName(name);
    }

    @Override
    public Sensor findById(Integer id) {
      return sensorRepository.findById(id)
              .orElseThrow(() -> new IllegalArgumentException("Sensor not found with id : " + id));
    }

    @Override
    public Sensor getSensorByMac(String mac) {
        return sensorRepository.findByMac(mac)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found with MAC address: " + mac));
    }

    @Override
    public void save(SensorViewModel viewModel, User user) {
        Sensor sensor = SensorMapper.toEntity(viewModel);

        if (Objects.nonNull(sensor)) {
            if (Objects.isNull(sensor.getAuthorizedUsers())) {
                sensor.setAuthorizedUsers(Set.of(user));
            } else {
                sensor.getAuthorizedUsers().add(user);
            }
            user.getAccessibleSensors().add(sensor);
            sensorRepository.save(sensor);
        }
    }

    @Override
    public void updateSensor(Integer sensorId, SensorViewModel viewModel) {
        Sensor existingSensor = findById(sensorId);

        if (Objects.nonNull(viewModel.getName()) && !viewModel.getName().isBlank()) {
            existingSensor.setName(viewModel.getName());
        }
        if (Objects.nonNull(viewModel.getMac()) && !viewModel.getMac().isBlank()) {
            existingSensor.setMac(viewModel.getMac());
        }
        if (Objects.nonNull(viewModel.getStatus())) {
            existingSensor.setStatus(viewModel.getStatus());
        }
        existingSensor.setFingerprint(viewModel.isFingerPrint());
        existingSensor.setNfc(viewModel.isNfc());
        existingSensor.setRfid(viewModel.isRfidCard());
        if (Objects.nonNull(viewModel.getIpAddress()) && !viewModel.getIpAddress().isBlank()) {
            existingSensor.setIpAddress(viewModel.getIpAddress());
        }

        sensorRepository.save(existingSensor);
    }

    @Override
    public long getSensorCount() {
        return sensorRepository.count();
    }

    @Override
    public void deleteById(Integer id) {
        Sensor sensor = findById(id);

        for(User user : sensor.getAuthorizedUsers()){
            user.getAccessibleSensors().remove(sensor);
        }

        sensor.getAuthorizedUsers().clear();

        sensorRepository.delete(sensor);


    }

    @Override
    public long getSensorCountByUser(Long userId) {
        return sensorRepository.countSensorsByUser(userId);
    }

    @Override
    public List<Sensor> getSensorsByUserId(Long userId) {
        return sensorRepository.findSensorsByUserId(userId);
    }

    @Override
    public List<Sensor> findSensorsByUserId(Long userId) {
        return sensorRepository.findSensorsByUserId(userId);
    }

}
