package logisticsmarshall.tqs.ua.model;

import lombok.Data;
import java.util.Set;

@Data
public class DriverDTO {

    private User user;

    private String phoneNo;

    private Boolean status = false;

    private Driver.Vehicle vehicle = Driver.Vehicle.MOTORCYCLE;

    private Set<Delivery> delivery;

    private Set<Reputation> reputation;

}