package logisticsmarshall.tqs.ua.model;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.Set;

@Data
@Entity
@Table(name = "driver")
public class Driver {
    public enum Vehicle {
        BICLYCLE, CAR, MOTORCYCLE, ONFOOT;
    }

    @Id
    private long id;

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private  User user;



    @Column(name = "phoneNumber", nullable = false)
    private String phoneNo;

    @Column(name = "status", nullable = false)
    private Boolean status = false;

    @Column(name = "vehicle", nullable = false)
    @Enumerated(EnumType.STRING)
    private Vehicle vehicle = Vehicle.MOTORCYCLE;

    @OneToMany(mappedBy = "driver")
    private Set<Delivery> delivery;



    @OneToMany(mappedBy = "driver", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Reputation> reputation;

    static public Driver fromDTO(DriverDTO driverDTO){
        Driver driver = new Driver();
        driver.setUser(driverDTO.getUser());
        driver.setPhoneNo(driverDTO.getPhoneNo());
        driver.setStatus(driverDTO.getStatus());
        driver.setVehicle(driverDTO.getVehicle());
        driver.setDelivery(driverDTO.getDelivery());
        driver.setReputation(driverDTO.getReputation());
        return driver;

    }
}