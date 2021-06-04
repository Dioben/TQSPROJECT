package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.services.UserService;
import org.junit.jupiter.api.Test;
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
    UserService userMock;

    ObjectMapper jsonParser = new ObjectMapper();

    @Test
    void whenRegisterAsDriverEmptyParametersReturnError() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is(400));


    }

    @Test
    void whenRegisterAsDriverBadEmailReturnError() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content("{" +"\"role\": \"DRIVER\","+
                            "\"name\": \"testcompanyname\","+
                            "\"email\": \"istonaotemarrobatextopontoextensao\","+
                            "\"password\": \"fakesafepassword\""+


                        "}"))
                .andExpect(status().is(400));
    }

    @Test
    void whenRegisterAsDriverCorrectParametersReturnOk() throws Exception {
        String email = "goodemail@ua.pt";
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content("{" +"\"role\": \"DRIVER\","+
                        "\"name\": \"testcompanyname\","+
                        "\"email\": \""+email+"\","+
                        "\"password\": \"fakesafepassword\""+


                        "}"))
                .andExpect(status().is(200)).andExpect(content().string(email));
    }


}