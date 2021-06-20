package logisticsmarshall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reputation")
public class Reputation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "description", nullable = false)
    private String description;


    @OneToOne(mappedBy = "reputation", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Driver driver;

    @Override
    public String toString() {
        return "Reputation{" +
                "id=" + id +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                '}';
    }
}