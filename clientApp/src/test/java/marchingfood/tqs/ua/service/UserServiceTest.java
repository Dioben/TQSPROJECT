package marchingfood.tqs.ua.service;

import lombok.SneakyThrows;
import marchingfood.tqs.ua.exceptions.AccessForbiddenException;
import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.repository.ClientRepository;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock(lenient = true)
    private ClientRepository userRepository;


    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void passwordEncryptionTest(){
        String password = "clear text";
        Client user = new Client();
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
        Client user = new Client();
        user.setAdmin(false);
        user.setName("user");
        user.setPassword("password");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        UserDetails details = userService.loadUserByUsername("user");
        Assertions.assertEquals(details.getAuthorities(), grantedAuthorities);
    }
    @Test
    void loadAdminTest(){
        Client user = new Client();
        user.setAdmin(true);
        user.setName("user");
        user.setPassword("password");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
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
    void getUserFromAuthNullUserTest(){
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(null);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(AccessForbiddenException.class, ()->userService.getUserFromAuthOrException());

    }

    @Test
    void getUserFromAuthNullAuthTest(){
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(AccessForbiddenException.class, ()->userService.getUserFromAuthOrException());

    }
    @Test
    @SneakyThrows
    void getUserFromAuthValidUserTest(){
        Client user = new Client();
        user.setName("user");
        user.setPassword("pass");
        user.setId(1);
        user.setAdmin(false);
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(user,userService.getUserFromAuthOrException());

    }

    @Test
    @SneakyThrows
    void getUserIfAdminSuccessTest(){
        Client user = new Client();
        user.setName("user");
        user.setPassword("pass");
        user.setId(1);
        user.setAdmin(true);
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertEquals(user,userService.getUserFromAuthIfAdmin());
    }
    @Test
    void getUserIfAdminFailTest(){
        Client user = new Client();
        user.setName("user");
        user.setPassword("pass");
        user.setId(1);
        user.setAdmin(false);
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(AccessForbiddenException.class, ()->userService.getUserFromAuthIfAdmin());
    }
}
