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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNumber", nullable = false)
    private Integer phoneNumber;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "vehicle", nullable = false)
    @Enumerated(EnumType.STRING)
    private Vehicle vehicle;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Reputation> reputation;

}