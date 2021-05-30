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
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @ManyToMany(mappedBy = "menus")
    private List<Order> orders;

    @Column(name = "order_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp orderTimestamp;


}
