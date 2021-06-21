package marchingfood.tqs.ua.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.*;
import marchingfood.tqs.ua.repository.DeliveryRepository;
import marchingfood.tqs.ua.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Delivery> getDeliveries(){return deliveryRepository.findAll();}

    public List<Delivery> getDeliveriesByClient(Client client){return deliveryRepository.findByClient(client);}

    public void saveDelivery(Delivery delivery){
        deliveryRepository.save(delivery);
    }

    public Delivery postToLogisticsClient(Delivery delivery) throws BadParameterException {



        String deliveryJSON = getPostMapFromDelivery(delivery);
        final String uri = "http://localhost:8080/api/delivery";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(deliveryJSON, headers);
        ResponseEntity<ProviderDelivery> result = restTemplate.postForEntity(uri,entity,ProviderDelivery.class);
        if (result.getStatusCode().equals(HttpStatus.BAD_REQUEST) || result.getBody()==null){
            throw  new BadParameterException("Mal-formed delivery request");
        }
        delivery.setId(result.getBody().getId());
        return delivery;
    }

    public List<ProviderDelivery> getClientDeliveriesFromLogistics(Client client){
        ArrayList<ProviderDelivery> providerDeliveries = new ArrayList<>();
        for(Delivery delivery: client.getOrderEntity()){
            providerDeliveries.add(this.getDeliveryFromLogistics(delivery.getId()));
        }
        return providerDeliveries;
    }

    private ProviderDelivery getDeliveryFromLogistics(long id) {
        final String uri = "http://localhost:8080/api/delivery/"+id+"?apiKey="+LOGISTICS_MARSHALL_APIKEY;
        ResponseEntity<ProviderDelivery> result = restTemplate.getForEntity(uri,ProviderDelivery.class);
        return  result.getBody();
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

    public void postReview(Review review) throws BadParameterException {
        final String uri = "http://localhost:8080/api/reputation";
        review.setApiKey(LOGISTICS_MARSHALL_APIKEY);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(review.toJson(), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri,entity,String.class);
        if (result.getStatusCode()!=HttpStatus.OK){throw new BadParameterException("Review Post Failed");}
    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
