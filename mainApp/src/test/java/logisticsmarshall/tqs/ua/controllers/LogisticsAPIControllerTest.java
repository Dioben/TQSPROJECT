package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import org.checkerframework.checker.units.qual.C;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        Company comp = new Company();
        String apiKey = "12SDF341G6";
        comp.setApiKey(apiKey);
        del.setId(1050L);
        del.setCompany(comp);
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(true);
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
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(false);
        mvc.perform(get("/api/delivery/1050")
                .param("APIKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenGetDeliveries_validAPIKeyAndDeliveryId_returnListDeliveries() throws Exception {
        ArrayList<Delivery> delLst = new ArrayList<>();
        Delivery del = new Delivery();
        del.setId(1050L);
        Delivery del2 = new Delivery();
        del2.setId(1051L);

        delLst.add(del);
        delLst.add(del2);

        String apiKey = "12SDF341G6";
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(false);
        Mockito.when(serviceMock.getDeliveriesByCompany()).thenReturn(delLst);


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
        String address = "\"sampletext\"";
        String apiKey = "apikeytext";
        Company company = new Company();
        company.setApiKey(apiKey);
        company.setAddress(address);

        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(company);

        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("{\"priority\":\"HIGH\"," +
                        "\"address\": "+ address + "\""+
                        "\"APIKey\":\""+ apiKey +
                        "\"}")
        ).andExpect(status().is(200))
                .andExpect(jsonPath("address", Matchers.equalToIgnoringCase(address)));

    }

    @Test
    void whenPostDelivery_emptyParameters_returnError() throws Exception {
        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("what is a jason?")
        ).andExpect(status().is(400));
    }

    @Test
    void whenPostDelivery_invalidAPIKey_returnError() throws Exception {
        String address = "\"sampletext\"";
        String apiKey = "apikeytext";
        Company company = new Company();
        company.setApiKey(apiKey);
        company.setAddress(address);

        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(null);

        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("{\"priority\":\"HIGH\"," +
                        "\"address\": "+ address + "\""+
                        "\"APIKey\":\""+ apiKey +
                        "\"}")
        ).andExpect(status().is(403));
    }
}