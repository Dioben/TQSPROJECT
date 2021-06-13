package logisticsmarshall.tqs.ua.model;

import lombok.Data;

@Data
public class UserDTO {

    private String name;

    private String email;

    private String password;

    private String role;

    private Driver driver;

    private Company company;

}
