package logisticsmarshall.tqs.ua.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "delivery")
public class Delivery {
    public enum Priority {
        HIGHPRIORITY,
        REGULARPRIORITY,
        LOWPRIORITY;
    }

    public enum Stage {
        REQUESTED,
        ACCEPTED,
        PICKEDUP,
        DELIVERED,
        CANCELED
    }

    @Id //logistics_id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp orderTimestamp;

    @Column(name="stage")
    @Enumerated(EnumType.STRING)
    private Stage stage = Stage.REQUESTED;

    @Column(name = "address", nullable = false)
    private String address;


    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @OneToOne
    @JoinColumn(name = "reputation_id", nullable = true)
    private Reputation reputation;


    @OneToOne
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;


    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;


}