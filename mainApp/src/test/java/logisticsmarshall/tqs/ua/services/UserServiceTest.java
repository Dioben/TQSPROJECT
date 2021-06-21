package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.exceptions.AccessForbiddenException;
import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;
import logisticsmarshall.tqs.ua.repository.DeliveryRepository;
import logisticsmarshall.tqs.ua.repository.DriverRepository;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private CompanyRepository companyRepository;

    @Mock(lenient = true)
    private DriverRepository driverRepository;

    @Mock(lenient = true)
    private DeliveryRepository deliveryRepository;
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

    @Test
    void grantKeyNXCompanyTest(){
        Mockito.when(companyRepository.findCompanyById(Mockito.anyLong())).thenReturn(null);
        Assertions.assertThrows(AccountDataException.class, ()->userService.grantCompanyKey(1l));
    }
    @Test
    void grantKeyNXDriverTest(){
        Mockito.when(driverRepository.findDriverById(Mockito.anyLong())).thenReturn(null);
        Assertions.assertThrows(AccountDataException.class, ()->userService.grantDriverKey(1l));
    }
    @Test
    @SneakyThrows
    void grantKeyCompSuccessTest(){
        String key = "abcde";
        Company company = new Company();
        company.setApiKey(key);
        Mockito.when(companyRepository.findCompanyById(Mockito.anyLong())).thenReturn(company);
        userService.grantCompanyKey(1l);
        Assertions.assertNotEquals(key,company.getApiKey());
    }
    @Test
    @SneakyThrows
    void grantKeyDriverSuccessTest(){
        String key = "abcde";
        Driver driver = new Driver();
        driver.setApiKey(key);
        Mockito.when(driverRepository.findDriverById(Mockito.anyLong())).thenReturn(driver);
        userService.grantDriverKey(1l);
        Assertions.assertNotEquals(key,driver.getApiKey());
    }
    @Test
    @SneakyThrows
    void banDriverSuccessTest(){
        User user = new User();
        Driver driver = new Driver();
        user.setDriver(driver);
        driver.setUser(user);
        Delivery del = new Delivery();
        del.setDriver(driver);
        driver.setDelivery(new HashSet<>(Arrays.asList(new Delivery[]{del})));
        Mockito.when(driverRepository.findDriverById(Mockito.anyLong())).thenReturn(driver);
        userService.banDriver(1l);
        Assertions.assertEquals(null,del.getDriver());
        Assertions.assertEquals("BANNED DRIVER", user.getRole());
    }
    @Test
    void banDriverNXTest(){
        Mockito.when(driverRepository.findDriverById(Mockito.anyLong())).thenReturn(null);
        Assertions.assertThrows(AccountDataException.class, ()->userService.banDriver(1l));
    }

    @Test
    void validatePasswordOkTest(){
        User user = new User();
        String pass = "password";
        user.setPassword(passwordEncoder.encode(pass));
       Assertions.assertDoesNotThrow(()->userService.validatePassword(user,pass));
    }
    @Test
    void validatePasswordFailTest(){
        User user = new User();
        String pass = "password";
        user.setPassword(passwordEncoder.encode(pass));
        Assertions.assertThrows(AccountDataException.class, ()->userService.validatePassword(user,"xpto"));
    }

    @Test
    void editCompanyGoodDataTest(){
        User user = new User();
        user.setName("guy");
        user.setEmail("man@ua.pt");
        user.setRole("COMPANY");
        user.setPassword("test");
        Company company = new Company();
        company.setAddress("aaaaaaaaaaaaaaaa");
        company.setDeliveryType("urg");
        company.setPhoneNumber("111111111");
        user.setCompany(company);
        Assertions.assertDoesNotThrow(()->userService.editCompany(user,"user","test","123456789","test","test"));
    }
    @Test
    void editCompanyBadDataTest(){
        User user = new User();
        user.setCompany(new Company());
        Assertions.assertThrows(AccountDataException.class,()->userService.editCompany(user,"user","test","bad","test","test"));

    }
    @Test
    void editDriverGoodDataTest(){
        User user = new User();
        user.setName("guy");
        user.setEmail("man@ua.pt");
        user.setRole("DRIVER");
        user.setPassword("test");
        Driver driver = new Driver();
        driver.setVehicle(Driver.Vehicle.MOTORCYCLE);
        driver.setPhoneNo("123456789");
        user.setDriver(new Driver());
        Assertions.assertDoesNotThrow(()->userService.editDriver(user,"user","test","123456789","MOTORCYCLE"));
    }
    @Test
    void editDriverBadDataTest(){
        User user = new User();
        user.setDriver(new Driver());
        Assertions.assertThrows(AccountDataException.class,
                ()->userService.editDriver(user,"user","test","1abc","CAR"));
    }
    @Test
    void editDriverBadVehicleTest(){
        User user = new User();
        user.setDriver(new Driver());
        Assertions.assertThrows(IllegalArgumentException.class,
                ()->userService.editDriver(user,"user","test","1abc","MIKE"));
    }

}
