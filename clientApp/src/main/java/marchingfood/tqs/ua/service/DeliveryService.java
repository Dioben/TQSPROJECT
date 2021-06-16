package marchingfood.tqs.ua.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.repository.ClientRepository;
import marchingfood.tqs.ua.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
public class DeliveryService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    @Autowired
    private final WebClient localApiClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    DeliveryRepository deliveryRepository;


    //TODO:REPLACE THIS FOR A VALUE IN LM DATABASE, SET IT UP
    private static final String LOGISTICS_MARSHALL_APIKEY = "12312312SDAFSD";

    ObjectMapper objectMapper = new ObjectMapper();


    public DeliveryService(WebClient localApiClient) {
        this.localApiClient = localApiClient;
    }

    public void saveDelivery(Delivery delivery){
        deliveryRepository.save(delivery);
    }

    public Delivery postToLogisticsClient(Delivery delivery) {
        String deliveryJSON = getPostMapFromDelivery(delivery,LOGISTICS_MARSHALL_APIKEY);
        System.out.println(deliveryJSON);
        final String uri = "http://backendmain:8080/api/delivery";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(deliveryJSON, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri,entity,String.class);

        System.out.println(result.getStatusCode());
        System.out.println(result.getBody());

        return delivery;
    }

    private String getPostMapFromDelivery(Delivery delivery, String logisticsMarshallApikey) {
        Map<String,Object> map = new HashMap<>();

        map.put("address",delivery.getAddress());
        //This is a restaurant, we serve food
        map.put("priority","HIGHPRIORITY");
        map.put("apiKey",logisticsMarshallApikey);

        String result = "";
        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
