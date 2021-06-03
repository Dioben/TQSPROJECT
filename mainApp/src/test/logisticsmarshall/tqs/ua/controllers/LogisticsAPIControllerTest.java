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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

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
        del.setId(1050L);
        Mockito.when(serviceMock.checkIfValidAPIKey(Mockito.anyString())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyInt())).thenReturn(del);

        mvc.perform(get("/api/delivery/1050")
                .param("APIKey", apiKey)
                .content(jsonParser.writeValueAsString(del))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.logisticsId",is(del.getId())));
    }

    @Test
    void whenGetDelivery_invalidAPIKeyOrDeliveryId_returnError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyInt())).thenReturn(del);

        mvc.perform(get("/api/delivery/1050")
                .param("APIKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenGetDeliveries_validAPIKeyAndDeliveryId_returnListDeliveries() throws Exception {
        ArrayList<Delivery> delLst = new ArrayList<Delivery>();
        Delivery del = new Delivery();
        del.setId(1050L);
        Delivery del2 = new Delivery();
        del2.setId(1051L);

        String apiKey = "12SDF341G6";

        Mockito.when(serviceMock.checkIfValidAPIKey(Mockito.anyString())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveries()).thenReturn(delLst);

        delLst.add(del);
        delLst.add(del2);
        mvc.perform(get("/api/delivery/")
        .param("APIKey", apiKey)
        .content(jsonParser.writeValueAsString(del))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(200))
        .andExpect(jsonPath("$", hasSize(delLst.size())));
    }

    @Test
    void whenGetDeliveries_invalidAPIKey_returnError() throws Exception {
        mvc.perform(get("/api/delivery/")
        .param("APIKey", "invalid")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400));
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