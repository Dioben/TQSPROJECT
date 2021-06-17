package marchingfood.tqs.ua.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class DeliveryService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DeliveryRepository deliveryRepository;

    private static final String LOGISTICS_MARSHALL_APIKEY = "12345678-1111-2222-3333-123456789000";

    @Autowired
    ObjectMapper objectMapper;


    public void saveDelivery(Delivery delivery){
        deliveryRepository.save(delivery);
    }

    public Delivery postToLogisticsClient(Delivery delivery) throws BadParameterException {
        String deliveryJSON = getPostMapFromDelivery(delivery);
        final String uri = "http://backendmain:8080/api/delivery";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(deliveryJSON, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri,entity,String.class);
        if (result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            throw  new BadParameterException("Mal-formed delivery request");
        }

        return delivery;
    }

    private String getPostMapFromDelivery(Delivery delivery) {
        Map<String,Object> map = new HashMap<>();
        map.put("apiKey",LOGISTICS_MARSHALL_APIKEY);
        //This is a restaurant, we serve food
        map.put("priority","HIGHPRIORITY");

        map.put("address",delivery.getAddress());



        String result = "";
        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
