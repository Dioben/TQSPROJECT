package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.UserService;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static logisticsmarshall.tqs.ua.utils.Utils.getStateMapList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LogisticsAPIController.class)
class LogisticsAPIControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DeliveryService serviceMock;

    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper jsonParser;

    @SneakyThrows
    @Test
     void whenGetDelivery_validapiKeyAndDeliveryId_returnDelivery() {
        Delivery del = new Delivery();
        String apiKey = "12SDF341G6";
        del.setId(1050L);
        del.setAddress("somewhere");
        del.setPickupAddress("somewhere else");
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);
        mvc.perform(get("/api/delivery/1050")
                .param("apiKey", apiKey)
                .content("{\"apiKey\":\"12SDF341G6\",\"address\":\"somewhere\",\"priority\":\"HIGHPRIORITY\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id",is(Integer.valueOf(String.valueOf(del.getId())))));
    }

    @Test
    void whenGetDelivery_invalidapiKeyOrDeliveryId_returnError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyInt())).thenReturn(del);
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(false);
        mvc.perform(get("/api/delivery/1050")
                .param("apiKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenGetDeliveries_validapiKey_returnListDeliveries() throws Exception {
        ArrayList<Delivery> delLst = new ArrayList<>();
        Delivery del = new Delivery();
        del.setId(1050L);
        Delivery del2 = new Delivery();
        del2.setId(1051L);

        delLst.add(del);
        delLst.add(del2);

        String apiKey = "12SDF341G6";
        Company com = new Company();
        com.setApiKey(apiKey);
        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(com);
        Mockito.when(serviceMock.getDeliveriesByCompany(Mockito.any())).thenReturn(delLst);


        mvc.perform(get("/api/delivery/")
        .param("apiKey", apiKey)
        .content(jsonParser.writeValueAsString(del))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(200))
        .andExpect(jsonPath("$", hasSize(delLst.size())));
    }

    @Test
    void whenGetDeliveries_invalidapiKey_returnError() throws Exception {
        mvc.perform(get("/api/delivery/")
        .param("apiKey", "invalid")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400));
    }

    @Test
    void whenPostDelivery_validapiKeyAndParameters_returnDelivery() throws Exception {
        String address = "\"sampletext\"";
        String apiKey = "apikeytext";
        Company company = new Company();
        company.setApiKey(apiKey);
        company.setAddress(address);
        String requestcontent = "{\"priority\":\"HIGHPRIORITY\"," +
                "\"address\": "+ address + ","+
                "\"apiKey\":\""+ apiKey +
                "\"}";
        System.out.println(requestcontent);
        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(company);

        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content(requestcontent)
        ).andExpect(status().is(200))
                .andExpect(jsonPath("address", Matchers.equalToIgnoringCase(address.replaceAll("\"",""))));

    }

    @Test
    void whenPostDelivery_emptyParameters_returnError() throws Exception {
        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("what is a jason?")
        ).andExpect(status().is(400));
    }

    @Test
    void whenPostDelivery_invalidapiKey_returnError() throws Exception {
        String address = "\"sampletext\"";
        String apiKey = "apikeytext";
        Company company = new Company();
        company.setApiKey(apiKey);
        company.setAddress(address);

        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(null);

        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("{\"priority\":\"HIGHPRIORITY\"," +
                        "\"address\": "+ address + ","+
                        "\"apiKey\":\""+ apiKey +
                        "\"}")
        ).andExpect(status().is(403));
    }


    @Test
    void whenGetDeliveriesState_validapiKey_returnListStates() throws Exception {
        ArrayList<Delivery> delLst = new ArrayList<>();
        Delivery del = new Delivery();
        del.setId(1050L);
        Delivery del2 = new Delivery();
        del2.setId(1051L);

        delLst.add(del);
        delLst.add(del2);

        String apiKey = "12SDF341G6";
        Company com = new Company();
        com.setApiKey(apiKey);
        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(com);
        Mockito.when(serviceMock.getDeliveriesByCompany(Mockito.any())).thenReturn(delLst);

        List<Map<String, String>> infoList = getStateMapList(delLst);
        String contentJson = jsonParser.writeValueAsString(infoList);

        mvc.perform(get("/api/delivery_state")
                .param("apiKey", apiKey)
                .content(jsonParser.writeValueAsString(del))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(delLst.size())))
                .andExpect(jsonPath("$[0].id", is(String.valueOf(delLst.get(0).getId()))))
                .andExpect(jsonPath("$[0].state", is(delLst.get(0).getStage().name())));
    }
    @Test
    void whenGetDeliveriesState_invalidapiKey_returnError() throws Exception {
        mvc.perform(get("/api/delivery_state")
                .param("apiKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

}
