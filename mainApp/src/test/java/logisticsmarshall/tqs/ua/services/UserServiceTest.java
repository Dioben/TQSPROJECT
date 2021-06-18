package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.exceptions.AccessForbiddenException;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import lombok.SneakyThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock(lenient = true)
    private UserRepository userRepository;


    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void passwordEncryptionTest(){
        String password = "clear text";
        User user = new User();
        user.setPassword(password);
        userService.encryptPasswordAndStoreUser(user);
        Assertions.assertNotEquals(password,user.getPassword());
    }

    @Test
    void loadNobodyTest(){
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class,()->userService.loadUserByUsername("user"));
    }

    @Test
    void loadUserTest(){
        User user = new User();
        user.setRole("COMPANY");
        user.setName("user");
        user.setPassword("password");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("COMPANY"));
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        UserDetails details = userService.loadUserByUsername("user");
        Assertions.assertEquals(details.getAuthorities(), grantedAuthorities);
    }
    @Test
    void loadAdminTest(){
        User user = new User();
        user.setRole("ADMIN");
        user.setName("user");
        user.setPassword("password");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        UserDetails details = userService.loadUserByUsername("user");
        Assertions.assertEquals(details.getAuthorities(), grantedAuthorities);
    }

    @Test
    void isAuthenticatedTest(){
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(true,userService.isAuthenticated());
    }

    @Test
    void isAuthenticatedNullTest(){
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(false,userService.isAuthenticated());
    }


    @Test
    void getUserFromAuthNullAuthTest(){
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(null,userService.getUserFromAuth());

    }
    @Test
    @SneakyThrows
    void getUserFromAuthValidUserTest(){
        User user = new User();
        user.setName("user");
        user.setPassword("pass");
        user.setId(1);
        user.setRole("DRIVER");
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(user,userService.getUserFromAuth());

    }

    @Test
    @SneakyThrows
    void getUserIfAdminSuccessTest(){
        User user = new User();
        user.setName("user");
        user.setPassword("pass");
        user.setId(1);
        user.setRole("ADMIN");
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(user,userService.getUserFromAuthAndCheckCredentials("ADMIN"));
    }
    @Test
    void getUserIfAdminFailTest(){
        User user = new User();
        user.setName("user");
        user.setPassword("pass");
        user.setId(1);
        user.setRole("DRIVER");
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(AccessForbiddenException.class, ()->userService.getUserFromAuthAndCheckCredentials("ADMIN"));
    }
}
