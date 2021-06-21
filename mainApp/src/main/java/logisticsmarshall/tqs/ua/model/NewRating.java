package logisticsmarshall.tqs.ua.model;

import lombok.Data;

@Data
public class NewRating {
    private int rating;
    private String apiKey;
    private String description;
    private long deliveryId;
}
