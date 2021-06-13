package marchingfood.tqs.ua.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marchingfood.tqs.ua.model.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
public class DeliveryService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    @Autowired
    private final WebClient localApiClient;

    //TODO:REPLACE THIS FOR A VALUE IN LM DATABASE, SET IT UP
    private static final String LOGISTICS_MARSHALL_APIKEY = "12312312SDAFSD";

    ObjectMapper objectMapper = new ObjectMapper();


    public DeliveryService(WebClient localApiClient) {
        this.localApiClient = localApiClient;
    }

    public Delivery postToLogisticsClient(Delivery delivery) {
        String deliveryJSON = getPostMapFromDelivery(delivery,LOGISTICS_MARSHALL_APIKEY);
        localApiClient
                .post()
                .uri("/api/delivery")
                .bodyValue(deliveryJSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve();
        return delivery;
    }

    private String getPostMapFromDelivery(Delivery delivery, String logisticsMarshallApikey) {
        Map<String,Object> map = new HashMap<>();

        map.put("address",delivery.getAddress());
        //This is a restaurant, we serve food
        map.put("priority","HIGHPRIORITY");
        map.put("APIKey",logisticsMarshallApikey);

        String result = "";
        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
