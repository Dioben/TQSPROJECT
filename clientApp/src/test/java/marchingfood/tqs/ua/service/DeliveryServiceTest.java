package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock(lenient = true)
    WebClient localApiClient;

    @Mock(lenient = true)
    RestTemplate restTemplate;

    @InjectMocks
    DeliveryService service;

//    @Test
//    void postDelivery_returnDeliveryTest(){
//        Delivery del = new Delivery();
//        del.setAddress("myPlace");
//        String deliveryJSON = "{'address' : 'myPlace','priority' : 'HIGHPRIORITY','APIKey' : '123SADASD1321'}";
//        Mockito.when(localApiClient
//                .post()
//                .uri("/api/delivery")
//                .bodyValue(deliveryJSON)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()).thenReturn(null);
//        assertEquals(service.postToLogisticsClient(del),del);
//    }
}