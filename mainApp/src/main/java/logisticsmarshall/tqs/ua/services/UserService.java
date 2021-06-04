package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.UserRegistration;
import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User save(UserRegistration registration) throws AccountDataException {
        if (userRepository.findByEmail(registration.getEmail()) != null) {
            throw new AccountDataException("Email is already in use");
        }
        User user = new User("x", "y", "password","ADMIN");

        return userRepository.save(user);
    }

    public User getUserByName(String username) {
        return userRepository.findByName(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Company getApiKeyHolder(String apiKey) {
        return companyRepository.findCompanyByApiKey(apiKey);
    }


}
