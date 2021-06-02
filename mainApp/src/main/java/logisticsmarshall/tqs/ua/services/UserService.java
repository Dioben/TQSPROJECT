package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.UserRegistration;
import logisticsmarshall.tqs.ua.exceptions.ExceptionDetails;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@ComponentScan("logisticsmarshall.tqs.ua.services")
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User save(UserRegistration registration) throws ExceptionDetails {
        if (userRepository.findByEmail(registration.getEmail()) != null) {
            throw new ExceptionDetails("There is already a user with that email");
        }
        User user = new User("x", "y", "password","ADMIN");

        return userRepository.save(user);
    }

    public User getUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkIfValidAPIKey(String anyString) {
        return false;
    }


}
