package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.services.UserService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.Column;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogisticsWebController.class)
class LogisticsWebControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserServiceImpl userMock;

    ObjectMapper jsonParser = new ObjectMapper();

    @Test
    void whenRegisterEmptyParametersReturnError() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is(400));


    }

    @Test
    void whenRegisterBadEmailReturnError() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content("{" +"\"role\": \"DRIVER\","+
                            "\"name\": \"testcompanyname\","+
                            "\"email\": \"istonaotemarrobatextopontoextensao\","+
                            "\"password\": \"fakesafepassword\""+


                        "}"))
                .andExpect(status().is(400));
    }

    @Test
    void whenRegisterCorrectParametersReturnOk() throws Exception {
        String email = "goodemail@ua.pt";
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content("{" +"\"role\": \"DRIVER\","+
                        "\"name\": \"testcompanyname\","+
                        "\"email\": \""+email+"\","+
                        "\"password\": \"fakesafepassword\""+


                        "}"))
                .andExpect(status().is(200)).andExpect(content().string(email));
    }

    @Test
    void whenUpdateAsDriverBadDataReturn400() throws Exception{
        mvc.perform(post("/update/driver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"vehicle\": \"Lockheed SR-71 Blackbird\"}"))
                .andExpect(status().is(400));
    }
    @Test
    void whenUpdateAsDriverWrongTypeReturn400() throws Exception{
        User user = new User("a","b","c","COMPANY");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/driver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"vehicle\": \"MOTORCYCLE\"}"))
                .andExpect(status().is(400));
    }
    @Test
    void whenUpdateAsDriverReturn200() throws Exception{
        User user = new User("a","b","c","DRIVER");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/driver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"vehicle\": \"MOTORCYCLE\"}"))
                .andExpect(status().is(200));
    }
    @Test
    void whenUpdateAsCompanyBadDataReturn400() throws Exception{
        mvc.perform(post("/update/company").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"address\": \"Nowhere St.\", \"deliveryType\":\"Not a valid type\"}"))
                .andExpect(status().is(400));
    }
    @Test
    void whenUpdateAsCompanyWrongTypeReturn400() throws Exception{
        User user = new User("a","b","c","DRIVER");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/company").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"address\": \"Nowhere St.\", \"deliveryType\":\"ASAP\"}"))
                .andExpect(status().is(400));

    }
    @Test
    void whenUpdateAsCompanyReturn200() throws Exception{
        User user = new User("a","b","c","COMPANY");
        Mockito.when(userMock.getUserByName(Mockito.anyString())).thenReturn(user);
        mvc.perform(post("/update/company").contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\": \"939939939\", \"address\": \"Nowhere St.\", \"deliveryType\":\"ASAP\"}"))
                .andExpect(status().is(200));
    }


}