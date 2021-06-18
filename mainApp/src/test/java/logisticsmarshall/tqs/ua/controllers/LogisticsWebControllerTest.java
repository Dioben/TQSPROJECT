package logisticsmarshall.tqs.ua.controllers;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

/*    @Test
    void whenLogoutAndIsAuthenticatedThenSuccess() throws Exception {
        when(userService.isAuthenticated()).thenReturn(true);
        mvc.perform(get("/login?logout")).
                andExpect(status().is(200));
    }*/

    @Test
    void whenGetIndexPageAndUserIsNullThenRedirect() throws Exception {
        mvc.perform(get("/")).
                andExpect(status().is(302));
    }

    @Test
    void whenGetIndexPageAndUserIsCompanyThenRedirect() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuth()).thenReturn(u1);

        mvc.perform(get("/")).
                andExpect(status().is(302));
    }

    @Test
    void whenGetIndexPageAndUserIsDriverThenRedirect() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                null,
                null
        );

        when(userService.getUserFromAuth()).thenReturn(u1);

        mvc.perform(get("/")).
                andExpect(status().is(302));
    }

    @Test
    void whenGetIndexPageAndUserIsAdminThenRedirect() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "ADMIN",
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

    @Test
    void whenGetCompanyDashPageAndUserCompanyIsNullThenThrowException() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("COMPANY")).thenReturn(u1);

        mvc.perform(get("/companyDash")).
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
    void whenGetDriverDashPageAndUserDriverIsNullThenThrowException() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverDash")).
                andExpect(status().is(403));
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
    void whenGetCompanyProfilePageAndUserCompanyIsNullThenThrowException() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("COMPANY")).thenReturn(u1);

        mvc.perform(get("/companyProfile")).
                andExpect(status().is(403));
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
                "COMPANY",
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
                "COMPANY",
                d1,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverProfile")).
                andExpect(status().is(200));
    }

    @Test
    void whenGetDriverProfilePageAndUserDriverIsNullThenThrowException() throws Exception {
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "COMPANY",
                null,
                null
        );

        when(userService.getUserFromAuthAndCheckCredentials("DRIVER")).thenReturn(u1);

        mvc.perform(get("/driverProfile")).
                andExpect(status().is(403));
    }

    @Test
    void whenAcceptDeliveryAndUserHasDriverAndUserHasReputationThenSuccess() throws Exception {
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
                        new BasicNameValuePair("action", "accept"),
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
    void whenPickUpDeliveryAndUserHasDriverAndUserHasReputationThenSuccess() throws Exception {
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
                        new BasicNameValuePair("action", "pickup"),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(200));
    }

    @Test
    void whenFinishDeliveryAndUserHasDriverAndUserHasReputationThenSuccess() throws Exception {
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
                        new BasicNameValuePair("action", "finish"),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(200));
    }

    @Test
    void whenCancelDeliveryAndUserHasDriverAndUserHasReputationThenSuccess() throws Exception {
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
                        new BasicNameValuePair("action", "cancel"),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(200));
    }

    @Test
    void whenChangeDeliveryAndUserHasDriverAndUserHasReputationAndActionIsInvalidThenThrowException() throws Exception {
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
                        new BasicNameValuePair("action", "none"),
                        new BasicNameValuePair("deliveryId", "1050")
                ))))).
                andExpect(status().is(200));
    }
}
