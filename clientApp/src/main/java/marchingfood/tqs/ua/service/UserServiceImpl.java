package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.exceptions.AccessForbiddenException;
import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Review;
import marchingfood.tqs.ua.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
    private ClientRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Client findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Client getUserByName(String username) {
        return userRepository.findByName(username);
    }

    public Client save(Client newUser) {
        return userRepository.save(newUser);
    }




    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Client user = userRepository.findByName(username);
        if (user == null) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        if (user.isAdmin()){ grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));}
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), grantedAuthorities);
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    public Client getUserFromAuthOrException() throws AccessForbiddenException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            throw new AccessForbiddenException("You must log in");
        }
        Client user =userRepository.findByName(authentication.getName());
        if (user==null){throw new AccessForbiddenException("You must log in");}

        return user;
    }

    public Client getUserFromAuthIfAdmin() throws AccessForbiddenException {
        Client user = getUserFromAuthOrException();
        if (!user.isAdmin()){throw new AccessForbiddenException("You are not admin");}
        return  user;
    }
    public void encryptPasswordAndStoreUser(Client user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

}