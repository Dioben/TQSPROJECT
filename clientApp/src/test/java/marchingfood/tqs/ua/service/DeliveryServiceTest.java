package marchingfood.tqs.ua.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Delivery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock(lenient = true)
    WebClient localApiClient;

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
        Mockito.when(
                restTemplate.postForEntity(Mockito.anyString(),Mockito.any(),Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertDoesNotThrow(()->service.postToLogisticsClient(delivery));
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