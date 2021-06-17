package logisticsmarshall.tqs.ua.repository;


import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Reputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReputationRepository extends JpaRepository<Reputation, Long> {
}
