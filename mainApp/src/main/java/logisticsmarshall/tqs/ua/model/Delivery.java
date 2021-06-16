package logisticsmarshall.tqs.ua.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "delivery")
public class Delivery {
    public static Delivery fromNewPost(NewDelivery newDelivery,Company company) {
        Delivery delivery = new Delivery();
        delivery.setAddress(newDelivery.getAddress());
        delivery.setPriority(Priority.valueOf(newDelivery.getPriority()));
        delivery.setCompany(company);
        delivery.setStage(Delivery.Stage.REQUESTED);
        delivery.pickupAddress = company.getAddress();
        return delivery;
    }

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

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;
    @Column(name = "address", nullable = false)
    private String address;


    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @OneToOne
    @JoinColumn(name = "reputation_id", nullable = true)
    private Reputation reputation;



    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;


}