package marchingfood.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "logistics_id", nullable = false)
    private Long logisticsID;

    @Column(name = "client_id", nullable = false)
    private Long clientID;

    @Column(name = "order_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp orderTimestamp;

    @Column(name = "delivered", nullable = false)
    private Boolean delivered;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "address", nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Client client;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToMany
    private List<Menu> menus;


    public Order() {

    }
}