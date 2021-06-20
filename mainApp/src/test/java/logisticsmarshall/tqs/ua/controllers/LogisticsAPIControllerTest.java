package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.Reputation;
import logisticsmarshall.tqs.ua.services.CompanyService;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.DriverService;
import logisticsmarshall.tqs.ua.services.ReputationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogisticsAPIController.class)
class LogisticsAPIControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DeliveryService serviceMock;

    @MockBean
    ReputationService reputationServiceMock;

    @MockBean
    DriverService driverServiceMock;

    @MockBean
    CompanyService companyServiceMock;

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
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);
        mvc.perform(get("/api/delivery/1050")
                .param("APIKey", apiKey)
                .content(jsonParser.writeValueAsString(del))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id",is(Integer.valueOf(String.valueOf(del.getId())))));
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
    void whenGetDeliveries_validAPIKey_returnListDeliveries() throws Exception {
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
                .content("{\"priority\":\"HIGHPRIORITY\"," +
                        "\"address\": "+ address + ","+
                        "\"APIKey\":\""+ apiKey +
                        "\"}")
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
    void whenPostDelivery_invalidAPIKey_returnError() throws Exception {
        String address = "\"sampletext\"";
        String apiKey = "apikeytext";
        Company company = new Company();
        company.setApiKey(apiKey);
        company.setAddress(address);

        Mockito.when(serviceMock.getApiKeyHolder(Mockito.anyString())).thenReturn(null);

        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("{\"priority\":\"HIGHPRIORITY\"," +
                        "\"address\": "+ address + ","+
                        "\"APIKey\":\""+ apiKey +
                        "\"}")
        ).andExpect(status().is(403));
    }

    @Test
    void whenGetRatingByDeliveryId_validAPIKeyAndDeliveryId_returnRating() throws Exception {
        Delivery del = new Delivery();
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        reputation.setDelivery(del);
        String apiKey = "12SDF341G6";
        del.setId(1050L);
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);
        Mockito.when(reputationServiceMock.getReputationByDelivery(del)).thenReturn(reputation);
        mvc.perform(get("/api/reputation/1050")
                .param("APIKey", apiKey)
                .content(jsonParser.writeValueAsString(reputation))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.rating",is(Integer.valueOf(String.valueOf(reputation.getRating())))));
    }

    @Test
    void whenGetRatingByDeliveryId_invalidAPIKey_returnError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        reputation.setDelivery(del);
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(false);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);
        Mockito.when(reputationServiceMock.getReputationByDelivery(del)).thenReturn(reputation);
        mvc.perform(get("/api/reputation/1050")
                .param("APIKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenGetRatingByDeliveryId_invalidDeliveryId_returnError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        reputation.setDelivery(del);
        String apiKey = "12SDF341G6";
        Mockito.when(serviceMock.apiKeyCanQuery(Mockito.anyString(),Mockito.anyLong())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(reputationServiceMock.getReputationByDelivery(null)).thenReturn(null);
        mvc.perform(get("/api/reputation/10000000")
                .param("APIKey", "apiKey")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }


    @Test
    void whenAverageRatingByDriverKey_validAPIKey_returnAverageRating() throws Exception {
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        Driver driver = new Driver();

        Set<Reputation> reputationList = new HashSet<>();
        reputationList.add(reputation);
        driver.setReputation(reputationList);

        String apiKey = "12SDF341G6";
        Mockito.when(driverServiceMock.apiKeyExits(Mockito.anyString())).thenReturn(true);
        Mockito.when(driverServiceMock.getDriverByApiKey(Mockito.anyString())).thenReturn(driver);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver)).thenReturn(reputationList);
        mvc.perform(get("/api/average_reputation")
                .param("APIKey", apiKey)
                .content(jsonParser.writeValueAsString(5))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    void whenAverageRatingByDriverKey_invalidAPIKey_returnError() throws Exception {
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        Driver driver = new Driver();

        Set<Reputation> reputationList = new HashSet<>();
        reputationList.add(reputation);
        driver.setReputation(reputationList);

        Mockito.when(driverServiceMock.apiKeyExits(Mockito.anyString())).thenReturn(false);
        Mockito.when(driverServiceMock.getDriverByApiKey(Mockito.anyString())).thenReturn(driver);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver)).thenReturn(reputationList);
        mvc.perform(get("/api/average_reputation")
                .param("APIKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenAverageRatingByDriverKey_driverHasNoRatings_returnError() throws Exception {
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        Driver driver = new Driver();

        Mockito.when(driverServiceMock.apiKeyExits(Mockito.anyString())).thenReturn(false);
        Mockito.when(driverServiceMock.getDriverByApiKey(Mockito.anyString())).thenReturn(driver);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver)).thenReturn(null);
        mvc.perform(get("/api/average_reputation")
                .param("APIKey", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }


    @Test
    void whenAllAverageRatingByCompanyKey_validAPIKey_returnListAverageRatings() throws Exception {
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        Driver driver = new Driver();
        Driver driver2 = new Driver();

        List<Driver> driverList = new ArrayList<>();
        driverList.add(driver);
        driverList.add(driver2);

        Set<Reputation> reputationList = new HashSet<>();
        reputationList.add(reputation);
        driver.setReputation(reputationList);
        driver2.setReputation(reputationList);

        String apiKey = "12SDF341G6";
        Mockito.when(companyServiceMock.apiKeyExits(Mockito.anyString())).thenReturn(true);
        Mockito.when(driverServiceMock.getAllDrivers()).thenReturn(driverList);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver)).thenReturn(reputationList);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver2)).thenReturn(reputationList);

        List<Object> response = new ArrayList<>();
        HashMap<String,Object> map1 = new HashMap<>();
        map1.put("id",driver.getId());
        map1.put("average_reputation",5);
        HashMap<String,Object> map2 = new HashMap<>();
        map2.put("id",driver.getId());
        map2.put("average_reputation",5);
        response.add(map1);
        response.add(map2);

        mvc.perform(get("/api/average_reputation/list")
                .param("APIKey", apiKey)
                .content(jsonParser.writeValueAsString(response))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }


    @Test
    void whenAllAverageRatingByCompanyKey_invalidAPIKey_returnError() throws Exception {
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        Driver driver = new Driver();
        Driver driver2 = new Driver();

        List<Driver> driverList = new ArrayList<>();
        driverList.add(driver);
        driverList.add(driver2);

        Set<Reputation> reputationList = new HashSet<>();
        reputationList.add(reputation);
        driver.setReputation(reputationList);
        driver2.setReputation(reputationList);

        String apiKey = "12SDF341G6";
        Mockito.when(companyServiceMock.apiKeyExits(Mockito.anyString())).thenReturn(false);
        Mockito.when(driverServiceMock.getAllDrivers()).thenReturn(driverList);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver)).thenReturn(reputationList);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver2)).thenReturn(reputationList);


        mvc.perform(get("/api/average_reputation/list")
                .param("APIKey", apiKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void whenAllAverageRatingByCompanyKey_noDrivers_returnError() throws Exception {
        Reputation reputation = new Reputation();
        reputation.setRating(5);
        Driver driver = new Driver();
        Driver driver2 = new Driver();


        Set<Reputation> reputationList = new HashSet<>();
        reputationList.add(reputation);
        driver.setReputation(reputationList);
        driver2.setReputation(reputationList);

        String apiKey = "12SDF341G6";
        Mockito.when(companyServiceMock.apiKeyExits(Mockito.anyString())).thenReturn(true);
        Mockito.when(driverServiceMock.getAllDrivers()).thenReturn(new ArrayList<>());
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver)).thenReturn(reputationList);
        Mockito.when(reputationServiceMock.getReputationsByDriver(driver2)).thenReturn(reputationList);


        mvc.perform(get("/api/average_reputation/list")
                .param("APIKey", apiKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }
}
