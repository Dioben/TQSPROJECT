package logisticsmarshall.tqs.ua.services;
import logisticsmarshall.tqs.ua.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(User registration);
}
