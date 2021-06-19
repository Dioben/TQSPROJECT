package logisticsmarshall.tqs.ua.controllers;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ParseException;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LogisticsWebController.class)
class LogisticsWebControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    DeliveryService deliveryService;

    @Test
    void whenRegisterEmptyParametersReturnError() throws Exception {
        mvc.perform(post("/register").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name",""),
                        new BasicNameValuePair("email",""),
                        new BasicNameValuePair("password",""),
                        new BasicNameValuePair("role",""),
                        new BasicNameValuePair("deliveryType",""),
                        new BasicNameValuePair("address",""),
                        new BasicNameValuePair("phoneNumber",""),
                        new BasicNameValuePair("phoneNo",""),
                        new BasicNameValuePair("vehicle","")
                ))))).
                andExpect(status().is(400));
    }

    @Test
    void whenRegisterBadEmailReturnError() throws Exception {
        mvc.perform(post("/register").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name","name"),
                        new BasicNameValuePair("email","invalidemailformat"),
                        new BasicNameValuePair("password","randompass"),
                        new BasicNameValuePair("role","DRIVER"),
                        new BasicNameValuePair("deliveryType",""),
                        new BasicNameValuePair("address",""),
                        new BasicNameValuePair("phoneNumber",""),
                        new BasicNameValuePair("phoneNo","933333333"),
                        new BasicNameValuePair("vehicle","ONFOOT")
                ))))).
                andExpect(status().is(400));
    }

    @Test
    void whenDriverRegisterCorrectParametersReturnOk() throws Exception {
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        when(userService.save(u1)).thenReturn(u1);

        mvc.perform(post("/register").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", u1.getName()),
                        new BasicNameValuePair("email", u1.getEmail()),
                        new BasicNameValuePair("password", u1.getPassword()),
                        new BasicNameValuePair("role", u1.getRole()),
                        new BasicNameValuePair("deliveryType", ""),
                        new BasicNameValuePair("address", ""),
                        new BasicNameValuePair("phoneNumber", ""),
                        new BasicNameValuePair("phoneNo", u1.getDriver().getPhoneNo().toString()),
                        new BasicNameValuePair("vehicle", u1.getDriver().getVehicle().toString())
                ))))).
                andExpect(status().is(302));
    }

    @Test
    void whenCompanyRegisterCorrectParametersReturnOk() throws Exception {
        Company c1 = new Company();
        c1.setDeliveryType("food");
        c1.setAddress("here");
        c1.setPhoneNumber("933333333");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                c1
        );

        when(userService.save(u1)).thenReturn(u1);

        mvc.perform(post("/register").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", u1.getName()),
                        new BasicNameValuePair("email", u1.getEmail()),
                        new BasicNameValuePair("password", u1.getPassword()),
                        new BasicNameValuePair("role", u1.getRole()),
                        new BasicNameValuePair("deliveryType", u1.getCompany().getDeliveryType()),
                        new BasicNameValuePair("address", u1.getCompany().getAddress()),
                        new BasicNameValuePair("phoneNumber", u1.getCompany().getPhoneNumber().toString()),
                        new BasicNameValuePair("phoneNo", ""),
                        new BasicNameValuePair("vehicle", "")
                ))))).
                andExpect(status().is(302));
    }

    @Test
    void whenRegisterAndSaveFailsThenThrowException() throws Exception {
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        when(userService.save(u1)).thenReturn(u1);
        doThrow(DataIntegrityViolationException.class).
                when(userService).
                encryptPasswordAndStoreUser(Mockito.any());

        mvc.perform(post("/register").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", u1.getName()),
                        new BasicNameValuePair("email", u1.getEmail()),
                        new BasicNameValuePair("password", u1.getPassword()),
                        new BasicNameValuePair("role", u1.getRole()),
                        new BasicNameValuePair("deliveryType", ""),
                        new BasicNameValuePair("address", ""),
                        new BasicNameValuePair("phoneNumber", ""),
                        new BasicNameValuePair("phoneNo", u1.getDriver().getPhoneNo().toString()),
                        new BasicNameValuePair("vehicle", u1.getDriver().getVehicle().toString())
                ))))).
                andExpect(status().is(400));
    }

    @Test
    void whenRegisterAndIsAuthenticatedThenRedirect() throws Exception {
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        when(userService.save(u1)).thenReturn(u1);
        when(userService.isAuthenticated()).thenReturn(true);

        mvc.perform(post("/register").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", u1.getName()),
                        new BasicNameValuePair("email", u1.getEmail()),
                        new BasicNameValuePair("password", u1.getPassword()),
                        new BasicNameValuePair("role", u1.getRole()),
                        new BasicNameValuePair("deliveryType", ""),
                        new BasicNameValuePair("address", ""),
                        new BasicNameValuePair("phoneNumber", ""),
                        new BasicNameValuePair("phoneNo", u1.getDriver().getPhoneNo().toString()),
                        new BasicNameValuePair("vehicle", u1.getDriver().getVehicle().toString())
                ))))).
                andExpect(status().is(302));
    }

    @Test
    void whenGetRegisterPageAndIsNotAuthenticatedThenSuccess() throws Exception {
        mvc.perform(get("/register")).
                andExpect(status().isOk());
    }

    @Test
    void whenGetRegisterPageAndIsAuthenticatedThenRedirect() throws Exception {
        when(userService.isAuthenticated()).thenReturn(true);
        mvc.perform(get("/register")).
                andExpect(status().is(302));
    }

    @Test
    void whenGetLoginPageAndIsNotAuthenticatedThenSuccess() throws Exception {
        mvc.perform(get("/login")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetLoginPageAndIsAuthenticatedThenRedirect() throws Exception {
        when(userService.isAuthenticated()).thenReturn(true);
        mvc.perform(get("/login")).
                andExpect(status().is(302));
    }

    @Test
    void whenGetIndexPageAndUserIsNullThenRedirect() throws Exception {
        mvc.perform(get("/")).
                andExpect(status().is(302));
    }

    @ParameterizedTest
    @ValueSource(strings = {"COMPANY", "DRIVER", "ADMIN"})
    void whenGetIndexPageAndUserIsRoleThenRedirect(String role) throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                role,
                null,
                null
        );

        when(userService.getUserFromAuth()).thenReturn(u1);

        mvc.perform(get("/")).
                andExpect(status().is(302));
    }

    @Test
    void whenGetInfoPageThenSuccess() throws Exception {
        mvc.perform(get("/info")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetCompanyDashPageAndUserHasCompanyThenSuccess() throws Exception {
        Company c1 = new Company();
        c1.setDeliveryType("food");
        c1.setAddress("here");
        c1.setPhoneNumber("933333333");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                c1
        );

        when(userService.getUserFromAuthAndCheckCredentials("COMPANY")).thenReturn(u1);

        mvc.perform(get("/companyDash")).
                andExpect(status().is(200));
    }

    @ParameterizedTest
    @CsvSource({
            "COMPANY, /companyDash",
            "COMPANY, /companyProfile",
            "DRIVER, /driverDash",
            "DRIVER, /driverProfile",
    })
    void whenGetDashOrProfilePageAndUserCompanyOrDriverIsNullThenThrowException(String role, String url) throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials(role)).thenReturn(u1);

        mvc.perform(get(url)).
                andExpect(status().is(403));
    }

    @Test
    void whenGetDriverDashPageAndUserHasDriverAndUserHasReputationThenSuccess() throws Exception {
        Reputation r1 = new Reputation();
        r1.setRating(5);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setReputation(new HashSet<>(Arrays.asList(r1, r1)));
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverDash")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetDriverDashPageAndUserHasDriverAndUserDoesntHaveReputationThenSuccess() throws Exception {
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setReputation(new HashSet<>());
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverDash")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetCompanyProfilePageAndUserHasCompanyThenSuccess() throws Exception {
        Company c1 = new Company();
        c1.setDeliveryType("food");
        c1.setAddress("here");
        c1.setPhoneNumber("933333333");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                c1
        );

        when(userService.getUserFromAuthAndCheckCredentials("COMPANY")).thenReturn(u1);

        mvc.perform(get("/companyProfile")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetDriverProfilePageAndUserHasDriverAndUserHasReputationThenSuccess() throws Exception {
        Reputation r1 = new Reputation();
        r1.setRating(5);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setReputation(new HashSet<>(Arrays.asList(r1, r1)));
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverProfile")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetDriverProfilePageAndUserHasDriverAndUserDoesntHaveReputationThenSuccess() throws Exception {
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setReputation(new HashSet<>());
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverProfile")).
                andExpect(status().is(200));
    }

    @ParameterizedTest
    @ValueSource(strings = {"accept", "pickup", "finish", "cancel", "invalid"})
    void whenChangeDeliveryAndUserHasDriverAndUserHasReputationThenSuccess(String action) throws Exception {
        Reputation r1 = new Reputation();
        r1.setRating(5);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setReputation(new HashSet<>(Arrays.asList(r1, r1)));
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(post("/driverDash").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", action),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(200));
    }

    @Test
    void whenAcceptDeliveryAndUserHasDriverAndUserDoesntHaveReputationThenSuccess() throws Exception {
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setReputation(new HashSet<>());
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(post("/driverDash").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", "accept"),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(200));
    }

    @Test
    void whenAcceptDeliveryAndUserDriverIsNullThenThrowException() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(post("/driverDash").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", "accept"),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(403));
    }

    @Test

    void grantBadRoleKeyTest() throws Exception {
        when(userService.getUserFromAuthAndCheckCredentials(Mockito.anyString())).thenReturn(null);
        mvc.perform(post("/grantKey").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("id", "1"),
                        new BasicNameValuePair("type", "ADMIN")
                ))))).
                andExpect(status().is(400));

    }
    @Test
    void grantGoodRoleKeyTest() throws  Exception{
        when(userService.getUserFromAuthAndCheckCredentials(Mockito.anyString())).thenReturn(null);
        mvc.perform(post("/grantKey").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("id", "1"),
                        new BasicNameValuePair("type", "COMPANY")
                ))))).
                andExpect(status().is(302));
    }
    @Test
    @SneakyThrows
    void requestKeyTest(){
        User user = new User();
        user.setRole("COMPANY");
        Company company = new Company();
        company.setUser(user);
        user.setCompany(company);
        when(userService.getUserFromAuth()).thenReturn(user);
        mvc.perform(post("/requestReset")).
                andExpect(status().is(302)).andExpect(redirectedUrl("/companyProfile"));

    }
}
