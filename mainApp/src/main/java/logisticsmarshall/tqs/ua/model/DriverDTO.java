package logisticsmarshall.tqs.ua.model;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Set;

@Data
public class DriverDTO {

    private User user;

    private String phoneNo;

    private Boolean status = false;

    @Enumerated(EnumType.STRING)
    private Driver.Vehicle vehicle = Driver.Vehicle.MOTORCYCLE;

    private Set<Delivery> delivery;

    private Set<Reputation> reputation;

}