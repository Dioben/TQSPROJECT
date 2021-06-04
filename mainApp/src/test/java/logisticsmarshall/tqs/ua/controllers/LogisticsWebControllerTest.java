package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

class LogisticsWebControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userMock;

    ObjectMapper jsonParser = new ObjectMapper();


    @Test
    void whenRegisterAsDriver_EmptyParameters_returnError() throws Exception {



    }

    @Test
    void whenRegisterAsDriver_InvalidParameters_returnError() throws Exception {
        //TODO
    }

    @Test
    void whenRegisterAsDriver_CorrectParameters_returnAPIKey() throws Exception {
        //TODO
    }

    @Test
    void whenRegisterAsCompany_EmptyParameters_returnError() throws Exception {
        //TODO
    }

    @Test
    void whenRegisterAsCompany_CorrectParameters_returnAPIKey() throws Exception {
        //TODO
    }

    @Test
    void whenRegisterAsAdmin_EmptyParameters_returnError() throws Exception {
        //TODO
    }

    @Test
    void whenRegisterAsAdmin_CorrectParameters_return200Ok() throws Exception {
        //TODO
    }

}