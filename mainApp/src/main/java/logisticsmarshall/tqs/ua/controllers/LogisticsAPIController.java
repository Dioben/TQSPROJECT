package logisticsmarshall.tqs.ua.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.Reputation;
import logisticsmarshall.tqs.ua.services.CompanyService;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.DriverService;
import logisticsmarshall.tqs.ua.services.ReputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(path="/delivery",consumes = "application/json")
    public ResponseEntity<Delivery> postDelivery(
                    @RequestBody String content
                    //TODO:Maybe include vehicle
    ) {
        System.out.println("Delivery Posting controller");
        Map<String, String> contentMap = null;
        try {
            contentMap = objectMapper.readValue(content, new TypeReference<Map<String,String>>(){});
        } catch (JsonProcessingException e) {
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
        delivery.setStage(Delivery.Stage.REQUESTED);
        deliveryService.postDelivery(delivery);
        return ResponseEntity.ok(delivery);
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
            @RequestParam(name="APIKey") String apikey) throws JsonProcessingException {
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


}
