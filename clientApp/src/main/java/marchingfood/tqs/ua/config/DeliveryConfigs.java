package marchingfood.tqs.ua.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DeliveryConfigs {

    final String LOGISTICS_MARSHALL_ENDPOINT = "http://localhost:8080/api/v3";

    @Bean
    public WebClient logisticsMarshallApiClient() {
        return WebClient.create(LOGISTICS_MARSHALL_ENDPOINT);
    }

}
