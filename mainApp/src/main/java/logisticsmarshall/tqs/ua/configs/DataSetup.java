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

            final String DRIVERROLE  = "DRIVER";
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

            User stoppedFood = new User();
            stoppedFood.setRole("COMPANY");
            stoppedFood.setEmail("stoppedfood@ua.pt");
            stoppedFood.setName("stoppedFood");
            stoppedFood.setPassword("stoppedFood");

            Company stoppedFoodAsCompany = new Company();
            stoppedFoodAsCompany.setAddress("We do not actually exist");
            stoppedFoodAsCompany.setDeliveryType("No hurry");
            stoppedFoodAsCompany.setPhoneNumber("1515151515");
            stoppedFood.setCompany(stoppedFoodAsCompany);
            userService.encryptPasswordAndStoreUser(stoppedFood);
            
            User rider = new User();
            rider.setRole(DRIVERROLE);
            rider.setEmail("rider@ua.pt");
            rider.setName("rider");
            rider.setPassword("rider");
            Driver riderAsDriver = new Driver();
            riderAsDriver.setPhoneNo("987654321");
            riderAsDriver.setApiKey("12345678-1111-2222-3333-123456789999");
            riderAsDriver.setVehicle(Driver.Vehicle.MOTORCYCLE);
            rider.setDriver(riderAsDriver);
            userService.encryptPasswordAndStoreUser(rider);


            User keylessRider = new User();
            keylessRider.setRole(DRIVERROLE);
            keylessRider.setEmail("keylessRider@ua.pt");
            keylessRider.setName("keylessRider");
            keylessRider.setPassword("keylessRider");
            Driver keylessRiderAsDriver = new Driver();
            keylessRiderAsDriver.setPhoneNo("987654321");
            keylessRiderAsDriver.setVehicle(Driver.Vehicle.MOTORCYCLE);
            keylessRider.setDriver(keylessRiderAsDriver);
            userService.encryptPasswordAndStoreUser(keylessRider);

            //Delivery Setup
            Delivery del1 = new Delivery();
            del1.setCompany(marchingFoodAsCompany);
            del1.setStage(Delivery.Stage.DELIVERED);
            del1.setDriver(riderAsDriver);
            del1.setAddress("The house with the balloons attached, can't miss it");
            del1.setPickupAddress(marchingFoodAsCompany.getAddress());
            del1.setId(1);
            del1.setOrderTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            del1.setPriority(Delivery.Priority.HIGHPRIORITY);
            marchingFoodAsCompany.setDelivery(new HashSet<>(Arrays.asList(del1)));

            Delivery del2 = new Delivery();
            del2.setCompany(marchingFoodAsCompany);
            del2.setStage(Delivery.Stage.REQUESTED);
            del2.setAddress("The house with the balloons attached, can't miss it");
            del2.setId(2);
            del2.setPickupAddress(marchingFoodAsCompany.getAddress());
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
            
            
            //bad driver setup
            User badRider = new User();
            badRider.setRole(DRIVERROLE);
            badRider.setEmail("badRider@ua.pt");
            badRider.setName("badRider");
            badRider.setPassword("badRider");
            Driver badRiderAsDriver = new Driver();
            badRiderAsDriver.setPhoneNo("999999999");
            badRiderAsDriver.setApiKey("12345678-1111-2222-3333-000000000000");
            badRiderAsDriver.setVehicle(Driver.Vehicle.MOTORCYCLE);
            badRider.setDriver(badRiderAsDriver);
            userService.encryptPasswordAndStoreUser(badRider);


            Delivery baddel1 = new Delivery();
            baddel1.setCompany(stoppedFoodAsCompany);
            baddel1.setStage(Delivery.Stage.DELIVERED);
            baddel1.setDriver(badRiderAsDriver);
            baddel1.setAddress("The retirement home down the street");
            baddel1.setPickupAddress(stoppedFoodAsCompany.getAddress());
            baddel1.setPriority(Delivery.Priority.LOWPRIORITY);

            Delivery baddel2 = new Delivery();
            baddel2.setCompany(stoppedFoodAsCompany);
            baddel2.setStage(Delivery.Stage.DELIVERED);
            baddel2.setDriver(badRiderAsDriver);
            baddel2.setAddress("Grandma");
            baddel2.setPickupAddress(stoppedFoodAsCompany.getAddress());
            baddel2.setPriority(Delivery.Priority.LOWPRIORITY);

            Delivery baddel3 = new Delivery();
            baddel3.setCompany(stoppedFoodAsCompany);
            baddel3.setStage(Delivery.Stage.DELIVERED);
            baddel3.setDriver(badRiderAsDriver);
            baddel3.setAddress("UP, the movie");
            baddel3.setPickupAddress(stoppedFoodAsCompany.getAddress());
            baddel3.setPriority(Delivery.Priority.LOWPRIORITY);
            
            stoppedFoodAsCompany.setDelivery(new HashSet<>(Arrays.asList(baddel1,baddel2,baddel3)));

            //Reputation Setup
            Reputation badRep1 = new Reputation();
            badRep1.setDescription("I really liked Rider, very chill dude, good driver, no complaints here");
            badRep1.setRating(2);
            badRep1.setDelivery(baddel1);
            badRep1.setDriver(badRiderAsDriver);
            del1.setReputation(badRep1);

            Reputation badRep2 = new Reputation();
            badRep2.setDescription("he was too french");
            badRep2.setRating(2);
            badRep2.setDelivery(baddel2);
            badRep2.setDriver(badRiderAsDriver);
            del1.setReputation(badRep2);

            Reputation badRep3 = new Reputation();
            badRep3.setDescription("His father smelt of elderberries");
            badRep3.setRating(2);
            badRep3.setDelivery(baddel3);
            badRep3.setDriver(badRiderAsDriver);
            del1.setReputation(badRep3);

            reputationService.postReputation(badRep1);
            reputationService.postReputation(badRep2);
            reputationService.postReputation(badRep3);
            deliveryService.postDelivery(baddel1);
            deliveryService.postDelivery(baddel2);
            deliveryService.postDelivery(baddel3);
        };
    }
}
