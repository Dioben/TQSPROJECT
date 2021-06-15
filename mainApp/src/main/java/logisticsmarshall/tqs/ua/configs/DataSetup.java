package logisticsmarshall.tqs.ua.configs;

import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSetup {
    @Bean
    CommandLineRunner setUpData(UserServiceImpl userService){
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
        };
    }
}
