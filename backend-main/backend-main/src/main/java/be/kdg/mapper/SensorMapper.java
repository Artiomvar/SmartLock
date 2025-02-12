package be.kdg.mapper;

import be.kdg.domain.Sensor;
import be.kdg.viewmodels.SensorViewModel;

import java.util.Objects;

public class SensorMapper {

    public static Sensor toEntity(SensorViewModel viewModel) {
        if (Objects.isNull(viewModel)) {
            return null;
        }

        Sensor sensor = new Sensor();
        sensor.setId(viewModel.getId());
        sensor.setName(viewModel.getName());
        sensor.setIpAddress(viewModel.getIpAddress());
        sensor.setMac(viewModel.getMac());
        sensor.setStatus(viewModel.getStatus());
        sensor.setRfid(viewModel.isRfidCard());
        sensor.setNfc(viewModel.isNfc());
        sensor.setFingerprint(viewModel.isFingerPrint());

        return sensor;
    }

    public static SensorViewModel toViewModel(Sensor sensor) {
        if (Objects.isNull(sensor)) {
            return null;
        }

        SensorViewModel viewModel = new SensorViewModel();
        viewModel.setId(sensor.getId());
        viewModel.setIpAddress(sensor.getIpAddress());
        viewModel.setName(sensor.getName());
        viewModel.setMac(sensor.getMac());
        viewModel.setStatus(sensor.isStatus());
        //viewModel.isRfidCard(sensor.isRfid());
        viewModel.setNfc(sensor.isNfc());
        viewModel.setRfidCard(sensor.isRfid());
        viewModel.setFingerPrint(sensor.isFingerprint());


        return viewModel;
    }
}
