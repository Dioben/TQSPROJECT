package logisticsmashall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price", nullable = false)
    private Double price;


    @Column(name = "payment_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp paymentTimestamp;


    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Order order;



}