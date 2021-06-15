package logisticsmarshall.tqs.ua.controllers;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LogisticsWebController.class)
class LogisticsWebControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserServiceImpl service;

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

        when(service.save(u1)).thenReturn(u1);

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

        when(service.save(u1)).thenReturn(u1);

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
/*
    @Test
    @Disabled
    //TODO
    void whenUpdateAsDriverBadDataReturn400() throws Exception{
        mvc.perform(post("/update/driver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"vehicle\": \"Lockheed SR-71 Blackbird\"}"))
                .andExpect(status().is(400));
    }
    @Test
    @Disabled
        //TODO
    void whenUpdateAsDriverWrongTypeReturn400() throws Exception{
        User user = new User("a","b","c","COMPANY");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/driver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"vehicle\": \"MOTORCYCLE\"}"))
                .andExpect(status().is(400));
    }
    @Test
    @Disabled
        //TODO
    void whenUpdateAsDriverReturn200() throws Exception{
        User user = new User("a","b","c","DRIVER");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/driver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"vehicle\": \"MOTORCYCLE\"}"))
                .andExpect(status().is(200));
    }
    @Test
    @Disabled
        //TODO
    void whenUpdateAsCompanyBadDataReturn400() throws Exception{
        mvc.perform(post("/update/company").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"address\": \"Nowhere St.\", \"deliveryType\":\"Not a valid type\"}"))
                .andExpect(status().is(400));
    }
    @Test
    @Disabled
        //TODO
    void whenUpdateAsCompanyWrongTypeReturn400() throws Exception{
        User user = new User("a","b","c","DRIVER");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/company").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"address\": \"Nowhere St.\", \"deliveryType\":\"ASAP\"}"))
                .andExpect(status().is(400));

    }
    @Test
    @Disabled
        //TODO
    void whenUpdateAsCompanyReturn200() throws Exception{
        User user = new User("a","b","c","COMPANY");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/company").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"address\": \"Nowhere St.\", \"deliveryType\":\"ASAP\"}"))
                .andExpect(status().is(200));
    }

*/
}
