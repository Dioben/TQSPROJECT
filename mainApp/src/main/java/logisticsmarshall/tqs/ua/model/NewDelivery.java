package logisticsmarshall.tqs.ua.model;

import lombok.Data;

@Data
public class NewDelivery {
    private String APIKey;
    private String address;
    private String priority;
}
