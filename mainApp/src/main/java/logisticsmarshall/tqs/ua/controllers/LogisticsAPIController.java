package logisticsmarshall.tqs.ua.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.services.CompanyService;
import logisticsmarshall.tqs.ua.exceptions.*;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.DriverService;
import logisticsmarshall.tqs.ua.services.ReputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static logisticsmarshall.tqs.ua.utils.Utils.getStateMapList;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class LogisticsAPIController {
    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private ReputationService reputationService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    ObjectMapper objectMapper;


    @PostMapping(path="/delivery",consumes = "application/json")
    public ResponseEntity<Delivery> postDelivery(@RequestBody NewDelivery newDelivery) {
        Company companyFromapiKey = deliveryService.getApiKeyHolderCompany(newDelivery.getApiKey());
        if (companyFromapiKey == null) return ResponseEntity.status(403).build();
        Delivery delivery = Delivery.fromNewPost(newDelivery,companyFromapiKey);
        deliveryService.postDelivery(delivery);
        return ResponseEntity.ok(delivery);
    }



    @GetMapping(path="/delivery_state")
    public ResponseEntity<String> getDeliveriesStates(@RequestParam(name="apiKey") String apikey) {
        Company companyFromapiKey = deliveryService.getApiKeyHolderCompany(apikey);
        if (companyFromapiKey == null) return ResponseEntity.status(400).build();
        List<Delivery> deliveries = deliveryService.getDeliveriesByCompany(companyFromapiKey);
        List<Map<String, String>> infoList = getStateMapList(deliveries);
        try {
            String contentJson = objectMapper.writeValueAsString(infoList);
            return ResponseEntity.ok(contentJson);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).build();
        }
    }



    @GetMapping(path="/delivery_state/{id}")
    public ResponseEntity<String> getDeliveryState(
            @PathVariable(name="id") long deliveryId,
            @RequestParam(name="apiKey") String apikey) {
        Company companyFromapiKey = deliveryService.getApiKeyHolderCompany(apikey);
        if (companyFromapiKey == null) return ResponseEntity.status(400).build();
        Delivery del = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(del.getStage().name());
    }

    @GetMapping(path="/delivery")
    public ResponseEntity<List<Delivery>> getDeliveries(
            @RequestParam(name = "role") String role,
            @RequestParam(name="apiKey") String apikey) {
        if (role.equalsIgnoreCase("company")) {
            Company company = deliveryService.getApiKeyHolderCompany(apikey);
            if (company == null)
                return ResponseEntity.status(400).build();
            List<Delivery> deliveries = deliveryService.getDeliveriesByCompany(company);
            return ResponseEntity.ok(deliveries);
        }
        if (role.equalsIgnoreCase("driver")) {
            Driver driver = deliveryService.getApiKeyHolderDriver(apikey);
            if (driver == null)
                return ResponseEntity.status(400).build();
            List<Delivery> deliveries = deliveryService.getDeliveriesByStage(Delivery.Stage.REQUESTED);
            deliveries.addAll(deliveryService.getDeliveriesByDriver(driver));
            return ResponseEntity.ok(deliveries);
        }
        return ResponseEntity.status(400).build();
    }

    @GetMapping(path="/delivery/{id}")
    public ResponseEntity<Delivery> getDelivery(
            @PathVariable(name="id") long deliveryId,
            @RequestParam(name="apiKey") String apikey) {
        if (!deliveryService.apiKeyCanQuery(apikey,deliveryId)) return ResponseEntity.status(400).build();
        Delivery del = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(del);
    }



    @GetMapping(path="/average_reputation/list")
    public ResponseEntity<String> getAllAverageRatings(
            @RequestParam(name="APIKey") String apikey) throws JsonProcessingException {

        if (!companyService.apiKeyExits(apikey)) return ResponseEntity.status(400).build();

        List<Driver> driverList= driverService.getAllDrivers();
        if(driverList == null || driverList.isEmpty())return ResponseEntity.noContent().build();


        List<Object> response = new ArrayList<>();
        for(Driver dr : driverList){
            Set<Reputation> repList = reputationService.getReputationsByDriver(dr);
            double averageReputation = 0;
            for(Reputation rep: repList)averageReputation+=rep.getRating();
            averageReputation/=repList.size();
            Map<String,Object> map = new HashMap<>();
            map.put("id",dr.getId());
            map.put("average_reputation",averageReputation);
            response.add(map);
        }

        String respJSON = objectMapper.writeValueAsString(response);

        return ResponseEntity.ok(respJSON);
    }


    @GetMapping(path="/average_reputation")
    public ResponseEntity<Double> getAverageRatingByDriverKey(
            @RequestParam(name="APIKey") String apikey) {
        if (!driverService.apiKeyExits(apikey)) return ResponseEntity.status(400).build();
        Driver driver = driverService.getDriverByApiKey(apikey);
        Set<Reputation> repList = reputationService.getReputationsByDriver(driver);
        if(repList == null || repList.isEmpty())return ResponseEntity.status(400).build();
        double averageReputation = 0;
        for(Reputation rep: repList)averageReputation+=rep.getRating();
        averageReputation/=repList.size();

        return ResponseEntity.ok(averageReputation);
    }

    @GetMapping(path="/reputation/{delivery_id}")
    public ResponseEntity<Reputation> getRatingByDeliveryId(
            @PathVariable(name="delivery_id") long deliveryId,
            @RequestParam(name="APIKey") String apikey) {
        if (!deliveryService.apiKeyCanQuery(apikey,deliveryId)) return ResponseEntity.status(400).build();
        Delivery del = deliveryService.getDeliveryById(deliveryId);
        Reputation rep = reputationService.getReputationByDelivery(del);
        if(rep == null)return ResponseEntity.status(400).build();
        return ResponseEntity.ok(rep);
    }

    @PostMapping(path="/reputation",consumes = "application/json")
    public ResponseEntity<Reputation> postRating(Integer rating,
                                                 String apiKey,
                                                 String description,
                                                 Long deliveryId) {
        Company companyFromapiKey = deliveryService.getApiKeyHolderCompany(apiKey);
        if (companyFromapiKey == null) return ResponseEntity.status(403).build();
        Delivery delRequested = deliveryService.getDeliveryById(deliveryId);
        Driver driverAssigned = delRequested.getDriver();

        Reputation rep = new Reputation();
        rep.setDelivery(delRequested);
        rep.setRating(rating);
        delRequested.setReputation(rep);
        deliveryService.postDelivery(delRequested);

        rep.setDriver(driverAssigned);
        rep.setDescription(description);
        rep.setRating(rating);

        reputationService.postReputation(rep);
        return ResponseEntity.ok(rep);
    }

    @PostMapping(path = "/delivery/{id}")
    public ResponseEntity<Delivery> changeDelivery(
            @PathVariable(name = "id") long deliveryId,
            @RequestParam(name = "action") String action,
            @RequestParam(name = "apiKey") String apikey) {
        Driver driver = deliveryService.getApiKeyHolderDriver(apikey);
        if (driver == null)
            return ResponseEntity.status(400).build();
        if (!deliveryService.driverCanQuery(driver, deliveryId))
            return ResponseEntity.status(400).build();
        User user = driver.getUser();
        try {
            switch (action) {
                case "accept":
                    deliveryService.acceptDelivery(user, deliveryId);
                    break;
                case "pickup":
                    deliveryService.pickUpDelivery(user, deliveryId);
                    break;
                case "finish":
                    deliveryService.finishDelivery(user, deliveryId);
                    break;
                case "cancel":
                    deliveryService.cancelDelivery(user, deliveryId);
                    break;
                default:
                    throw new InvalidDeliveryActionException();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        }
        Delivery delivery = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(delivery);
    }
}
