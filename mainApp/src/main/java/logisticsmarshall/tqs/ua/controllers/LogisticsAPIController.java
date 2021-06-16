package logisticsmarshall.tqs.ua.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.NewDelivery;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Company companyFromAPIKey = deliveryService.getApiKeyHolder(newDelivery.getApiKey());
        if (companyFromAPIKey == null) return ResponseEntity.status(403).build();
        Delivery delivery = Delivery.fromNewPost(newDelivery,companyFromAPIKey);
        deliveryService.postDelivery(delivery);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping(path="/delivery_state")
    public ResponseEntity<String> getDeliveriesStates(@RequestParam(name="APIKey") String apikey) {
        System.out.println("Log /delivery_state");
        Company companyFromAPIKey = deliveryService.getApiKeyHolder(apikey);
        if (companyFromAPIKey == null) return ResponseEntity.status(400).build();
        List<Delivery> deliveries = deliveryService.getDeliveriesByCompany(companyFromAPIKey);
        List<Map<String, String>> infoList = getStateMapList(deliveries);
        try {
            String contentJson = objectMapper.writeValueAsString(infoList);
            System.out.println("Content in JSON:");
            System.out.println(contentJson);
            return ResponseEntity.ok(contentJson);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).build();
        }
    }



    @GetMapping(path="/delivery_state/{id}")
    public ResponseEntity<String> getDeliveryState(
            @PathVariable(name="id") long deliveryId,
            @RequestParam(name="APIKey") String apikey) {
        System.out.println("Log /delivery_state/{id}");
        Company companyFromAPIKey = deliveryService.getApiKeyHolder(apikey);
        if (companyFromAPIKey == null) return ResponseEntity.status(400).build();
        Delivery del = deliveryService.getDeliveryById(deliveryId);
        Map<String,String> infoDelivery = new HashMap<>();
        infoDelivery.put(del.getAddress(),del.getStage().name());
        try {
            String contentJson = objectMapper.writeValueAsString(infoDelivery);
            System.out.println("Content in JSON:");
            System.out.println(contentJson);
            return ResponseEntity.ok(contentJson);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping(path="/delivery")
    public ResponseEntity<List<Delivery>> getDeliveries(@RequestParam(name="APIKey") String apikey) {
        Company companyFromAPIKey = deliveryService.getApiKeyHolder(apikey);
        if (companyFromAPIKey == null) return ResponseEntity.status(400).build();
        List<Delivery> deliveries = deliveryService.getDeliveriesByCompany(companyFromAPIKey);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping(path="/delivery/{id}")
    public ResponseEntity<Delivery> getDelivery(
            @PathVariable(name="id") long deliveryId,
            @RequestParam(name="APIKey") String apikey) {
        if (!deliveryService.apiKeyCanQuery(apikey,deliveryId)) return ResponseEntity.status(400).build();
        Delivery del = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(del);

    }
}
