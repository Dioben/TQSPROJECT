package logisticsmarshall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "company")
public class Company {

    @Id
    private long id;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private  User user;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phoneNumber", nullable = false)
    private Integer phoneNumber;

    @Column(name = "deliveryType", nullable = false)
    private String deliveryType;

    @Column(name = "apiKey", nullable = true, unique = true)
    private String apiKey;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Delivery> delivery;

    public Company(long id, User user, String address, Integer phoneNumber, String deliveryType, String apiKey) {
        this.id = id;
        this.user = user;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.deliveryType = deliveryType;
        this.apiKey = apiKey;
    }

    public Company(){};
    public Company(User user){
        this.user=user;
    };

}