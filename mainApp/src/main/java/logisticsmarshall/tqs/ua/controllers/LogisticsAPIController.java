package logisticsmarshall.tqs.ua.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.exceptions.*;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static logisticsmarshall.tqs.ua.utils.Utils.getStateMapList;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class LogisticsAPIController {
    @Autowired
    private DeliveryService deliveryService;
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
