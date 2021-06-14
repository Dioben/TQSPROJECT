package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.model.Client;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Client findByEmail(String email);

    Client save(Client registration);
}

