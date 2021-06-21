package marchingfood.tqs.ua.model;
import lombok.Data;

@Data
public class Review {
    private int rating;
    long deliveryId;
    String apiKey;
    String description;
}
