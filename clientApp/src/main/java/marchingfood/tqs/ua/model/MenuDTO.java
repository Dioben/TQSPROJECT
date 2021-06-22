package marchingfood.tqs.ua.model;

import lombok.Data;

import java.util.List;

@Data
public class MenuDTO {

    private String name;

    private double price;

    private String description;

    private String imageurl;

    private List<Delivery> orderEntities;

}
