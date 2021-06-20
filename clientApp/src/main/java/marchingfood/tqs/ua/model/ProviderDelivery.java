package marchingfood.tqs.ua.model;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class ProviderDelivery {
    private long id;
    private Timestamp orderTimestamp;
    private String stage;
    private String pickupAddress;
    private String address;
    private String priority;

}
