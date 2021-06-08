package logisticsmarshall.tqs.ua.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class LogisticsAPIController {
    @Autowired
    private DeliveryService deliveryService;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(path="/delivery",consumes = "application/json")
    ResponseEntity<Delivery> postDelivery(
                    @RequestBody String content
                    //TODO:Maybe include vehicle
    ) {
        Map<String, String> contentMap = null;
        try {
            contentMap = objectMapper.readValue(content, new TypeReference<Map<String,String>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).build();
        }
        String address = contentMap.get("address");
        String priority = contentMap.get("priority");
        String apikey = contentMap.get("APIKey");
        if(address.isEmpty() || priority.isEmpty() || apikey.isEmpty()){
            return ResponseEntity.status(403).build();
        }
        Company companyFromAPIKey = deliveryService.getApiKeyHolder(apikey);
        if (companyFromAPIKey == null) return ResponseEntity.status(403).build();
        Delivery delivery = new Delivery();
        Delivery.Priority priorityEnum = Delivery.Priority.valueOf(priority);
        delivery.setAddress(address);
        delivery.setPriority(priorityEnum);
        delivery.setCompany(companyFromAPIKey);
        delivery.setDelivered(false);
        delivery.setPaid(false);
        deliveryService.postDelivery(delivery);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping(path="/delivery")
    ResponseEntity<List<Delivery>> getDeliveries(@RequestParam(name="APIKey") String apikey) {
        Company companyFromAPIKey = deliveryService.getApiKeyHolder(apikey);
        if (companyFromAPIKey == null) return ResponseEntity.status(400).build();
        List<Delivery> deliveries = deliveryService.getDeliveriesByCompany(companyFromAPIKey);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping(path="/delivery/{id}")
    ResponseEntity<Delivery> getDelivery(
            @PathVariable(name="id") String delivery_id,
            @RequestParam(name="APIKey") String apikey) {
        long deliveryId = Long.parseLong(delivery_id);
        if (!deliveryService.apiKeyCanQuery(apikey,deliveryId)) return ResponseEntity.status(400).build();
        Delivery del = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(del);

    }
}
