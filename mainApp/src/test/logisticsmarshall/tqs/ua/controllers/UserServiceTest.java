package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.UserRegistration;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import logisticsmarshall.tqs.ua.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    private User user;
    private UserRegistration userRegistration;

    @BeforeEach
    void setup() {
        user = new User("john", "john@email.com", "1334", "ADMIN");
    }

    @Test
    void whenUserEmailNotExists_receiveNothing() {
        assertNull(userService.getUserByEmail("mail@mail.com"));
    }

    @Test
    void whenUserNameExists_receiveCorrectUserName() {
        given(userRepository.findByUsername("john")).willReturn(user);
        assertEquals(user, userService.getUserByName("john"));
    }

    @Test
    void whenSaveUser_returnAllInfoUser() {
        String name = "john",
                email = "john@email.com",
                password ="1334",
                role = "ADMIN";
        given(userRepository.findByEmail(anyString())).willReturn(null);
        assertEquals(name,user.getName());
        assertEquals(email,user.getEmail());
        assertEquals(password,user.getPassword());
        assertEquals(role,user.getRole());

    }




}
