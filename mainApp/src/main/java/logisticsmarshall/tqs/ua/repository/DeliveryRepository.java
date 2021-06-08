package logisticsmarshall.tqs.ua.repository;

import logisticsmarshall.tqs.ua.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Delivery findDeliveryById(Long id);

    List<Delivery> findAllDeliveriesByCompany_id(Long id);
}
