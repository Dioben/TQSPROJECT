package logisticsmarshall.tqs.ua.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import logisticsmarshall.tqs.ua.services.CompanyService;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.DriverService;
import logisticsmarshall.tqs.ua.services.ReputationService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
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

    @MockBean
    UserServiceImpl userService;

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
    void whenGetDeliveriesByCompany_validapiKey_returnListDeliveries() throws Exception {
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
        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(com);
        Mockito.when(serviceMock.getDeliveriesByCompany(Mockito.any())).thenReturn(delLst);


        mvc.perform(get("/api/delivery/")
        .param("apiKey", apiKey)
        .param("role", "company")
        .content(jsonParser.writeValueAsString(del))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(200))
        .andExpect(jsonPath("$", hasSize(delLst.size())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"company", "driver", "invalid"})
    void whenGetDeliveries_invalidapiKey_returnError(String role) throws Exception {
        mvc.perform(get("/api/delivery/")
        .param("apiKey", "invalid")
        .param("role", role)
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
        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(company);

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

        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(null);

        mvc.perform(post("/api/delivery").contentType(MediaType.APPLICATION_JSON)
                .content("{\"priority\":\"HIGHPRIORITY\"," +
                        "\"address\": "+ address + ","+
                        "\"apiKey\":\""+ apiKey +
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
                .param("APIKey", "invalid"))
                .andExpect(status().is(400));
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
        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(com);
        Mockito.when(serviceMock.getDeliveriesByCompany(Mockito.any())).thenReturn(delLst);

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
                .param("APIKey", "invalid"))
                .andExpect(status().is(400));
    }
    @Test
    void whenGetDeliveryState_validApiKey_returnState() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        String apiKey = "12SDF341G6";
        Company com = new Company();
        com.setApiKey(apiKey);
        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(com);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);

        mvc.perform(get("/api/delivery_state/"+del.getId())
                .param("apiKey", apiKey)
                .content(jsonParser.writeValueAsString(del))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(del.getStage().name()));

    }

    @Test
    void whenGetDeliveryState_invalidApiKey_returnState() throws Exception {
        mvc.perform(get("/api/delivery_state/1050")
                .param("apiKey", "invalid")
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
    void whenPostRating_validapiKeyAndParameters_returnReputation() throws Exception {
        Reputation rep = new Reputation();
        String description = "Really liked my driver";
        String apiKey = "12312SADF213";
        Company company = new Company();
        company.setApiKey(apiKey);
        Delivery delRequested = new Delivery();
        Driver driverAssigned = new Driver();
        rep.setDelivery(delRequested);
        rep.setRating(5);
        rep.setDriver(driverAssigned);
        rep.setDescription(description);

        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(company);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(delRequested);
        Mockito.when(driverServiceMock.getDriverById(Mockito.anyLong())).thenReturn(driverAssigned);
        mvc.perform(post("/api/reputation")
                .param("apiKey", apiKey)
                .param("rating", String.valueOf(5))
                .param("deliveryId", String.valueOf(1))
                .param("description", description)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParser.writeValueAsString(rep)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("rating", Matchers.equalTo(rep.getRating())));
    }

    @Test
    void whenPostRating_invalidapiKey_returnError() throws Exception {
        Reputation rep = new Reputation();
        String description = "Really liked my driver";
        String apiKey = "12312SADF213";
        Company company = new Company();
        company.setApiKey(apiKey);
        Delivery delRequested = new Delivery();
        Driver driverAssigned = new Driver();
        rep.setDelivery(delRequested);
        rep.setRating(5);
        rep.setDriver(driverAssigned);
        rep.setDescription(description);

        Mockito.when(serviceMock.getApiKeyHolderCompany(Mockito.anyString())).thenReturn(null);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyInt())).thenReturn(delRequested);
        Mockito.when(driverServiceMock.getDriverById(Mockito.anyInt())).thenReturn(driverAssigned);

        mvc.perform(post("/api/reputation")
                .param("apiKey", "invalid")
                .param("rating", String.valueOf(5))
                .param("delivery_id", String.valueOf(1))
                .param("driver_id", String.valueOf(1))
                .param("description", description)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParser.writeValueAsString(rep)))
                .andExpect(status().is(403));
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
    @Test
    void whenGetDeliveriesByDriver_validapiKey_returnListDeliveries() throws Exception {
        ArrayList<Delivery> delLst = new ArrayList<>();
        Delivery del = new Delivery();
        del.setId(1050L);
        Delivery del2 = new Delivery();
        del2.setId(1051L);
        delLst.add(del);
        delLst.add(del2);

        ArrayList<Delivery> delLst2 = new ArrayList<>();
        Delivery del3 = new Delivery();
        del3.setId(1052L);
        delLst2.add(del3);

        String apiKey = "12SDF341G6";
        Driver driver = new Driver();
        driver.setApiKey(apiKey);
        Mockito.when(serviceMock.getApiKeyHolderDriver(Mockito.anyString())).thenReturn(driver);
        Mockito.when(serviceMock.getDeliveriesByStage(Delivery.Stage.REQUESTED)).thenReturn(delLst);
        Mockito.when(serviceMock.getDeliveriesByDriver(Mockito.any())).thenReturn(delLst2);

        int size = delLst.size()+delLst2.size();

        mvc.perform(get("/api/delivery/")
                .param("apiKey", apiKey)
                .param("role", "driver")
                .content(jsonParser.writeValueAsString(del))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(size))).andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {"accept", "pickup", "finish", "cancel"})
    void whenChangeDelivery_validApiKey_thenSuccess(String action) throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setApiKey("12SDF341G6");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        Mockito.when(serviceMock.getApiKeyHolderDriver(d1.getApiKey())).thenReturn(d1);
        Mockito.when(serviceMock.driverCanQuery(d1, del.getId())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);

        mvc.perform(post("/api/delivery/1050").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", action),
                        new BasicNameValuePair("apiKey", d1.getApiKey())
                ))))).
                andExpect(status().is(200));
    }

    @Test
    void whenAcceptDelivery_invalidApiKey_thenError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setApiKey("12SDF341G6");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        Mockito.when(serviceMock.getApiKeyHolderDriver(d1.getApiKey())).thenReturn(d1);
        Mockito.when(serviceMock.driverCanQuery(d1, del.getId())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);

        mvc.perform(post("/api/delivery/1050").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", "accept"),
                        new BasicNameValuePair("apiKey", "invalidApiKey")
                ))))).
                andExpect(status().is(400));
    }

    @Test
    void whenAcceptDelivery_driverCannotQuery_thenError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setApiKey("12SDF341G6");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        Mockito.when(serviceMock.getApiKeyHolderDriver(d1.getApiKey())).thenReturn(d1);
        Mockito.when(serviceMock.driverCanQuery(d1, del.getId())).thenReturn(false);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);

        mvc.perform(post("/api/delivery/1050").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", "accept"),
                        new BasicNameValuePair("apiKey", d1.getApiKey())
                ))))).
                andExpect(status().is(400));
    }

    @Test
    void whenAcceptDelivery_invalidAction_thenError() throws Exception {
        Delivery del = new Delivery();
        del.setId(1050L);
        Driver d1 = new Driver();
        d1.setPhoneNo("933333333");
        d1.setVehicle(Driver.Vehicle.ONFOOT);
        d1.setDelivery(new HashSet<>());
        d1.setApiKey("12SDF341G6");
        User u1 = new User(
                "name",
                "email@gmail.com",
                "randompass",
                "DRIVER",
                d1,
                null
        );

        Mockito.when(serviceMock.getApiKeyHolderDriver(d1.getApiKey())).thenReturn(d1);
        Mockito.when(serviceMock.driverCanQuery(d1, del.getId())).thenReturn(true);
        Mockito.when(serviceMock.getDeliveryById(Mockito.anyLong())).thenReturn(del);

        mvc.perform(post("/api/delivery/1050").
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("action", "invalid"),
                        new BasicNameValuePair("apiKey", d1.getApiKey())
                ))))).
                andExpect(status().is(400));
    }

}
