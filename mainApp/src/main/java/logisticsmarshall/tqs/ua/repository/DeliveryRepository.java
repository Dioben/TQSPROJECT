package logisticsmarshall.tqs.ua.repository;

import logisticsmarshall.tqs.ua.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Delivery findDeliveryById(Long id);

    List<Delivery> findAllDeliveriesByCompanyId(Long id);
}
