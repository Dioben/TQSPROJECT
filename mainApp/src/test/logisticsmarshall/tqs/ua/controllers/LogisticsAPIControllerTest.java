package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(LogisticsAPIController.class)
class LogisticsAPIControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DeliveryService serviceMock;

    ObjectMapper jsonParser = new ObjectMapper();

    @Test
    void whenGetDelivery_validAPIKeyAndDeliveryId_returnDelivery() throws Exception {
        Delivery del = new Delivery();
        String apiKey = "12SDF341G6";
        del.setLogisticsId(1050L);
        Mockito.when(serviceMock.checkIfValidAPIKey(Mockito.anyString())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyInt())).thenReturn(del);

        mvc.perform(get("/delivery/get")
                .param("id", String.valueOf(1050L))
                .param("APIKey", apiKey)
                .content(jsonParser.writeValueAsString(del))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.logisticsId",is(del.getLogisticsId())));
    }

    @Test
    void whenGetDelivery_invalidAPIKeyOrDeliveryId_returnError() throws Exception {
        Delivery del = new Delivery();
        del.setLogisticsId(1050L);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyInt())).thenReturn(del);

        mvc.perform(get("/delivery/get")
                .param("id", String.valueOf(1050L))
                .param("APIKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenGetDeliveries_validAPIKeyAndDeliveryId_returnListDeliveries() throws Exception {
        //TODO
    }

    @Test
    void whenGetDeliveries_invalidAPIKeyOrDeliveryId_returnError() throws Exception {
        //TODO
    }

    @Test
    void whenPostDelivery_validAPIKeyAndParameters_returnDelivery() throws Exception {
        //TODO
    }

    @Test
    void whenPostDelivery_emptyParameters_returnError() throws Exception {
        //TODO
    }

    @Test
    void whenPostDelivery_invalidAPIKey_returnError() throws Exception {
        //TODO
    }
}