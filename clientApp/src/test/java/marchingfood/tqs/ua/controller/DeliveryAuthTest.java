package marchingfood.tqs.ua.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import lombok.SneakyThrows;
import marchingfood.tqs.ua.service.CartService;
import marchingfood.tqs.ua.service.DeliveryService;
import marchingfood.tqs.ua.service.MenuService;
import marchingfood.tqs.ua.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = DeliveryController.class)
class DeliveryAuthTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    MenuService menuService;

    @MockBean
    CartService cartService;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    DeliveryService deliveryService;

    ObjectMapper jsonParser = new ObjectMapper();

    @Test
    @SneakyThrows
    void registerOkTest(){
        Mockito.when(userService.isAuthenticated()).thenReturn(false);
        mvc.perform(get("/register"))
                .andExpect(status().isOk());
    }
    @Test
    @SneakyThrows
    void registerRedirectTest(){
        Mockito.when(userService.isAuthenticated()).thenReturn(true);
        mvc.perform(get("/register"))
                .andExpect(status().isFound());
    }
    @Test
    @SneakyThrows
    void registerPostBadDataTest(){
        Mockito.when(userService.isAuthenticated()).thenReturn(true);
        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "i am not very valid"),
                        new BasicNameValuePair("name", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("address", "test")
                )))))
                .andExpect(status().isBadRequest());
    }
    @Test
    @SneakyThrows
    void registerPostRedirectTest(){
        Mockito.when(userService.isAuthenticated()).thenReturn(true);
        mvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "validemail@ua.pt"),
                        new BasicNameValuePair("name", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("address", "test")
                )))))
                .andExpect(status().isFound());
    }
    @Test
    @SneakyThrows
    void registerPostOkTest(){
        Mockito.when(userService.isAuthenticated()).thenReturn(false);
        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "validemail@ua.pt"),
                        new BasicNameValuePair("name", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("address", "test")
                )))))
                .andExpect(status().isOk());
    }

}
