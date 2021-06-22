package logisticsmarshall.tqs.ua.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DeliveryWithReview {
    private long id;
    private Timestamp orderTimestamp;
    private String stage;
    private String pickupAddress;
    private String address;
    private String priority;
    private String description;
    private int rating;

    static DeliveryWithReview fromDelivery(Delivery delivery){
        DeliveryWithReview self = new DeliveryWithReview();
        self.id = delivery.getId();
        self.orderTimestamp = delivery.getOrderTimestamp();
        self.stage = delivery.getStage().toString();
        self.pickupAddress = delivery.getPickupAddress();
        self.address = delivery.getAddress();
        self.priority = delivery.getPriority().toString();
        if (delivery.getReputation()!=null){
            self.description = delivery.getReputation().getDescription();
            self.rating = delivery.getReputation().getRating();
        }

    }
}
