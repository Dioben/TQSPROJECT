package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    @Autowired
    DriverRepository driverRepository;

    public boolean apiKeyCanQuery(String apiKey, Long driverId){
        Driver driver = driverRepository.findDriverById(driverId);
        return driver.getApiKey().equals(apiKey);
    }

    public boolean apiKeyExits(String apiKey){
        Driver driver = driverRepository.findDriverByApiKey(apiKey);
        return driver != null;
    }

    public Driver getDriverById(long driverId) {
        return driverRepository.findDriverById(driverId);
    }

    public Driver getDriverByApiKey(String apikey) {
        return driverRepository.findDriverByApiKey(apikey);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
}
