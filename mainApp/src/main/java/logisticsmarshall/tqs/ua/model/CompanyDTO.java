package logisticsmarshall.tqs.ua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
public class CompanyDTO {

    private User user;

    private String address;

    private String phoneNumber;

    private String deliveryType;

    private String apiKey;

    private Set<Delivery> delivery;

}