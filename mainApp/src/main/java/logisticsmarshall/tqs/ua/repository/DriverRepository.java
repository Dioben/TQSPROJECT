package logisticsmarshall.tqs.ua.repository;

import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {
    List<Driver> findAllByApiKey(String apiKey);
}
