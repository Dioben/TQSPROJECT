package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                getUserRoles(user));
    }

    private Collection<? extends GrantedAuthority> getUserRoles(User user){
        ArrayList<GrantedAuthority> authorityArrayList = new ArrayList<>();
        authorityArrayList.add(new SimpleGrantedAuthority(user.getRole()));
        return  authorityArrayList;
    }
}