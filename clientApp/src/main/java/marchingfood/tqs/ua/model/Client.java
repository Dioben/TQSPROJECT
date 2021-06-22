package marchingfood.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;
import java.util.regex.Pattern;

@Data
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "isadmin", nullable = false)
    private boolean admin=false;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Delivery> orderEntity;

    public Client() {

    }

    public static Client fromDTO(ClientDTO userDTO) {
        Client client = new Client();
        client.setAddress(userDTO.getAddress());
        client.setName(userDTO.getName());
        client.setPassword(userDTO.getPassword());
        client.setEmail(userDTO.getEmail());
        client.setAdmin(false);
        return client;
    }

    public static boolean validateNewUser(Client user) {
        // https://github.com/Baeldung/spring-security-registration/blob/master/src/main/java/com/baeldung/validation/EmailValidator.java
        String emailRegex = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*+@[A-Za-z0-9-]{2,}(\\.[A-Za-z0-9]{2,})*+$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        return user.getName() != null
                && user.getEmail() != null
                && user.getPassword() != null
                && emailPattern.matcher(user.getEmail()).matches();
    }
}