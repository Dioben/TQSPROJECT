package logisticsmarshall.tqs.ua.model;

import javax.persistence.*;
import lombok.Data;

import java.util.Collection;

@Data
@Entity
@Table(name = "logistics_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @OneToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    @OneToOne
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

    public User(String name, String email, String password, String role){
        this.name=name;
        this.email=email;
        //encrypt first
        this.password=password;
        this.role=role;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;




    public User() {

    }
}
