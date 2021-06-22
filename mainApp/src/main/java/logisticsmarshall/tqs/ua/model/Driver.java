package logisticsmarshall.tqs.ua.model;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "driver")
public class Driver {
    public enum Vehicle {
        BICYCLE, CAR, MOTORCYCLE, ONFOOT;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id &&
                Objects.equals(phoneNo, driver.phoneNo) &&
                Objects.equals(busy, driver.busy) &&
                vehicle == driver.vehicle &&
                Objects.equals(apiKey, driver.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneNo, busy, vehicle, apiKey);
    }

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private  User user;



    @Column(name = "phoneNumber", nullable = false)
    private String phoneNo;

    @Column(name = "busy", nullable = false)
    private boolean busy = false;

    @Column(name = "vehicle", nullable = false)
    @Enumerated(EnumType.STRING)
    private Vehicle vehicle = Vehicle.MOTORCYCLE;

    @Column(name = "apiKey", nullable = true, unique = true)
    private String apiKey;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Delivery> delivery;



    @OneToMany(mappedBy = "driver", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Reputation> reputation;

    public static  Driver fromDTO(DriverDTO driverDTO){
        Driver driver = new Driver();
        driver.setUser(driverDTO.getUser());
        driver.setPhoneNo(driverDTO.getPhoneNo());
        driver.setBusy(driverDTO.getStatus());
        driver.setVehicle(driverDTO.getVehicle());
        driver.setDelivery(driverDTO.getDelivery());
        driver.setReputation(driverDTO.getReputation());
        driver.setApiKey(UUID.randomUUID().toString());
        return driver;

    }
}