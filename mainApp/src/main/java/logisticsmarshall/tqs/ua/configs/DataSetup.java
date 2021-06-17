package logisticsmarshall.tqs.ua.configs;

import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.DeliveryService;
import logisticsmarshall.tqs.ua.services.ReputationService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class DataSetup {
    @Bean
    CommandLineRunner setUpData(UserServiceImpl userService, DeliveryService deliveryService, ReputationService reputationService){
        return args -> {
            User admin = new User();
            admin.setRole("ADMIN");
            admin.setEmail("admin@ua.pt");
            admin.setName("admin");
            admin.setPassword("admin");
            userService.encryptPasswordAndStoreUser(admin);

            User marchingFood = new User();
            marchingFood.setRole("COMPANY");
            marchingFood.setEmail("marchingfood@ua.pt");
            marchingFood.setName("marchingfood");
            marchingFood.setPassword("marchingfood");
            Company marchingFoodAsCompany = new Company();
            marchingFoodAsCompany.setAddress("In my heart");
            marchingFoodAsCompany.setDeliveryType("Food - Urgent");
            marchingFoodAsCompany.setPhoneNumber("123456789");
            marchingFoodAsCompany.setApiKey("12345678-1111-2222-3333-123456789000");
            marchingFood.setCompany(marchingFoodAsCompany);
            userService.encryptPasswordAndStoreUser(marchingFood);

            User rider = new User();
            rider.setRole("DRIVER");
            rider.setEmail("rider@ua.pt");
            rider.setName("rider");
            rider.setPassword("rider");
            Driver riderAsDriver = new Driver();
            riderAsDriver.setPhoneNo("987654321");
            riderAsDriver.setApiKey("12345678-1111-2222-3333-123456789999");
            riderAsDriver.setVehicle(Driver.Vehicle.MOTORCYCLE);
            rider.setDriver(riderAsDriver);
            userService.encryptPasswordAndStoreUser(rider);

            //Delivery Setup
            Delivery del1 = new Delivery();
            del1.setCompany(marchingFoodAsCompany);
            del1.setStage(Delivery.Stage.DELIVERED);
            del1.setDriver(riderAsDriver);
            del1.setAddress("The house with the balloons attached, can't miss it");
            del1.setId(1);
            del1.setOrderTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            del1.setPriority(Delivery.Priority.HIGHPRIORITY);
            marchingFoodAsCompany.setDelivery(new HashSet<>(Arrays.asList(del1)));

            Delivery del2 = new Delivery();
            del2.setCompany(marchingFoodAsCompany);
            del2.setStage(Delivery.Stage.REQUESTED);
            del2.setAddress("The house with the balloons attached, can't miss it");
            del2.setId(2);
            del2.setOrderTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            del2.setPriority(Delivery.Priority.HIGHPRIORITY);

            //Reputation Setup
            Reputation rep1 = new Reputation();
            rep1.setDescription("I really liked Rider, very chill dude, good driver, no complaints here");
            rep1.setId(1);
            rep1.setRating(5);
            rep1.setDelivery(del1);
            rep1.setDriver(riderAsDriver);
            del1.setReputation(rep1);




            reputationService.postReputation(rep1);
            deliveryService.postDelivery(del1);
            deliveryService.postDelivery(del2);
        };
    }
}
