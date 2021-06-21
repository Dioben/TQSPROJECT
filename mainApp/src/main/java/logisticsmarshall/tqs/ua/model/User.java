package logisticsmarshall.tqs.ua.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import logisticsmarshall.tqs.ua.exceptions.AccessForbiddenException;
import lombok.Data;

import java.util.Objects;
import java.util.regex.Pattern;

@Data
@Entity
@Table(name = "logistics_user")
public class User {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, role);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @OneToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "driver_id", nullable = true)
    @JsonIgnore
    private Driver driver;

    @OneToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "company_id", nullable = true)
    @JsonIgnore
    private Company company;

    public User(String name, String email, String password, String role){
        this.name=name;
        this.email=email;
        //encrypt first
        this.password=password;
        this.role=role;
    }

    public User(String name, String email, String password, String role, Driver driver,Company company){
        this.name=name;
        this.email=email;
        //encrypt first
        this.password=password;
        this.role=role;
        this.driver=driver;
        this.company=company;
    }





    public User() {

    }
    public static User fromDTO(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setDriver(userDTO.getDriver());
        user.setCompany(userDTO.getCompany());
        return user;
    }

    public static boolean validateNewUser(User user, Driver driver, Company company) {
        // https://github.com/Baeldung/spring-security-registration/blob/master/src/main/java/com/baeldung/validation/EmailValidator.java
        String emailRegex = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*+@[A-Za-z0-9-]{2,}(\\.[A-Za-z0-9]{2,})*+$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        // https://regexr.com/2to9u
        String phoneRegex = "([+(\\d]{1})(([\\d() \\-.]){0,11})(\\d{5,})";
        Pattern phonePattern = Pattern.compile(phoneRegex);

        return user.getName() != null
                && user.getEmail() != null
                && user.getPassword() != null
                && user.getRole() != null
                && emailPattern.matcher(user.getEmail()).matches()
                && ((user.getRole().equals("DRIVER")
                && driver.getPhoneNo() != null
                && driver.getVehicle() != null
                && phonePattern.matcher(driver.getPhoneNo()).matches())
                || (user.getRole().equals("COMPANY")
                && company.getPhoneNumber() != null
                && company.getAddress() != null
                && company.getDeliveryType() != null
                && phonePattern.matcher(company.getPhoneNumber()).matches()));
    }
}
