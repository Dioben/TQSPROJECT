package logisticsmarshall.tqs.ua.repository;


import logisticsmarshall.tqs.ua.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {
    Driver findDriverByApiKey(String apiKey);
    List<Driver> findAllByApiKey(String apiKey);

    Driver findDriverById(Long id);

}
