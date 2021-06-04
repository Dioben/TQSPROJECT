package logisticsmarshall.tqs.ua.repository;

import logisticsmarshall.tqs.ua.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByName(String username);
}
