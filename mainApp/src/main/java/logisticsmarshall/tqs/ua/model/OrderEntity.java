package logisticsmarshall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "order_entity")
public class OrderEntity {

    public enum Priority {
        HIGHPRIORITY,
        REGULARPRIORITY,
        LOWPRIORITY;
    }

    @Id //logistics_id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "logistics_id", nullable = false)
    private Long logisticsId;


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
    @Enumerated(EnumType.STRING)
    private Priority priority;



    @OneToOne
    @JoinColumn(name = "reputation_id", nullable = false)
    private Reputation reputation;


    @OneToOne
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;


    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


}