package marchingfood.tqs.ua.model;
import lombok.Data;

@Data
public class Review {
    private int rating;
    long deliveryId;
    String apiKey;
    String description;

    public String toJson() {
        return "{"+
                "\"rating\":"+"\""+rating+"\","+
                "\"deliveryId\":"+"\""+deliveryId+"\","+
                "\"apiKey\":"+"\""+apiKey+"\","+
                "\"description\":"+"\""+description+"\""
                +"}";

    }
}
