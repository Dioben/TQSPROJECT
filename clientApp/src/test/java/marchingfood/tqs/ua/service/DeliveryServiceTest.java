package marchingfood.tqs.ua.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.model.ProviderDelivery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock(lenient = true)
    RestTemplate restTemplate;

    @Mock(lenient = true) //I have no clue why but @Spy does not work
    ObjectMapper objectMapper ;

    @InjectMocks
    DeliveryService service;


    @SneakyThrows
    @Test
    void deliveryPostTestOk(){
        Delivery delivery  = new Delivery();
        delivery.setAddress("Right here");
        delivery.setId(2);
        ProviderDelivery logisticsDelivery= new ProviderDelivery();
        logisticsDelivery.setId(1);
        logisticsDelivery.setOrderTimestamp(Timestamp.from(Instant.now()));
        logisticsDelivery.setPriority("HIGHPRIORITY");
        logisticsDelivery.setStage("PICKEDUP");
        logisticsDelivery.setPickupAddress("here");
        logisticsDelivery.setAddress("there");
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"xd\":\"xd\"}");
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(),Mockito.any(HttpEntity.class),Mockito.any()))
                .thenReturn(new ResponseEntity(logisticsDelivery,HttpStatus.OK));
        service.postToLogisticsClient(delivery);
        Assertions.assertEquals(logisticsDelivery.getId(),delivery.getId());

    }

    @Test
    @SneakyThrows
    void deliveryPostTestBadData(){
        Delivery delivery  = new Delivery();
        delivery.setAddress("Right here");
        Mockito.when(
                restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        assertThrows(BadParameterException.class,()->service.postToLogisticsClient(delivery));
    }
}