package logisticsmarshall.tqs.ua.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfigs {

    @Bean
    public ObjectMapper jsonParser(){ return new ObjectMapper();}
}
