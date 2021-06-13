package logisticsmarshall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

@Data
@Entity
@Table(name = "reputation")
public class Reputation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "description", nullable = false)
    private String description;


    @OneToOne(mappedBy = "reputation", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

}