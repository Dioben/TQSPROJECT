package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.DeliveryRepository;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock(lenient = true)
    private AuthenticationManager authenticationManager;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private BCryptPasswordEncoder passwordEncoder;

    @Mock(lenient = true)
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setVehicle(Driver.Vehicle.CAR);
        driver.setPhoneNo("933333333");
        driver.setStatus(true);

        user = new User();
        user.setId(1L);
        user.setName("driver");
        user.setEmail("driver@mail.com");
        user.setPassword("password");
        user.setDriver(driver);

        delivery = new Delivery();
        delivery.setId(1L);
        delivery.setAddress("A place");


        Mockito.when(deliveryRepository.findDeliveryById(1L)).thenReturn(delivery);
        Mockito.when(deliveryRepository.save(delivery)).thenReturn(delivery);
    }

    @Test
    void whenValidInAcceptDeliveryThenSuccess() {
        try {
            userService.acceptDelivery(user, delivery.getId());
        } catch (Exception e) {
            fail(e);
        }
        System.out.println(delivery.getStage());
    }

    @Test
    void whenInvalidDeliveryIdInAcceptDeliveryThenThrowException() {
        try {
            userService.acceptDelivery(user, -1L);
        } catch (Exception e) {
            return;
        }
        fail();
    }

    @Test
    void whenDeliveryHasDriverInAcceptDeliveryThenThrowException() {
        delivery.setDriver(new Driver());
        try {
            userService.acceptDelivery(user, delivery.getId());
        } catch (Exception e) {
            return;
        }
        fail();
    }

    @Test
    void whenDriverIsNullInAcceptDeliveryThenThrowException() {
        user.setDriver(null);
        try {
            userService.acceptDelivery(user, delivery.getId());
        } catch (Exception e) {
            return;
        }
        fail();
    }
}
