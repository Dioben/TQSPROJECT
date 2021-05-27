package logisticsmashall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "order")
public class Order {

    @Id //logistics_id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp orderTimestamp;

    @Column(name = "delivered", nullable = false)
    private Boolean delivered;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "priority", nullable = false)
    private Boolean priority;


    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Driver driver;


    @OneToOne
    @JoinColumn(name = "reputation_id", nullable = false)
    private Reputation reputation;


    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;


    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    public Order() {

    }
}