package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.exceptions.AccountCantDeliverException;
import logisticsmarshall.tqs.ua.exceptions.DeliveryAlreadyHasDriverException;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.DeliveryRepository;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    DeliveryRepository deliveryRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User getUserByName(String username) {
        return userRepository.findByName(username);
    }

    public User save(User newUser) {
        return userRepository.save(newUser);
    }




    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByName(username);
        if (user == null) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();


        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));



        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), grantedAuthorities);
    }

    @Transactional
    public void autoLogin(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    public void encryptPasswordAndStoreUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    @Transactional
    public void acceptDelivery(long deliveryId) throws DeliveryAlreadyHasDriverException, AccountCantDeliverException {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        if (delivery.getDriver() == null || delivery.getPaid())
            throw new DeliveryAlreadyHasDriverException();
        if (!isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("User is not logged in");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Driver driver = user.getDriver();
        if (driver == null
                || driver.getPhoneNo() == null
                || driver.getPhoneNo().isEmpty()
                || driver.getVehicle() == null
                || !driver.getStatus())
            throw new AccountCantDeliverException();
        delivery.setDriver(driver);
        deliveryRepository.save(delivery);
    }

}