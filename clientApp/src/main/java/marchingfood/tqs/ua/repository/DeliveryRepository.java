package marchingfood.tqs.ua.repository;

import marchingfood.tqs.ua.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    Delivery findById(long id);
}
