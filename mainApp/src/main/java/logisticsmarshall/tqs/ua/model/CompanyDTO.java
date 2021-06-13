package logisticsmarshall.tqs.ua.model;


import lombok.Data;

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