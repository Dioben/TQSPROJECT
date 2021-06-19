package logisticsmarshall.tqs.ua.repository;


import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.DriverAdminView;
import logisticsmarshall.tqs.ua.model.Reputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReputationRepository extends JpaRepository<Reputation, Long> {
    @Query("SELECT x.driver,avg(x.rating) as rating,count(rating) as reviewcount from Reputation x group by x.driver" +
            " having avg(x.rating)<= :ratingMax and count(rating) >= :reviewMin")
    List<DriverAdminView> findAllByRatingMaximum(@Param("ratingMax") double ratingMax,@Param("reviewMin") int minReviews);
}
