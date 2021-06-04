package logisticsmarshall.tqs.ua.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class UserConfigs {
    @Bean
    BCryptPasswordEncoder passwordEncoder(){ return  new BCryptPasswordEncoder();}
}
