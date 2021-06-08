package marchingfood.tqs.ua.model;


import lombok.Data;
import marchingfood.tqs.ua.exceptions.BadParameterException;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "imageurl", nullable = true)
    private String imageurl;

    @ManyToMany(mappedBy = "menus")
    private List<Delivery> orderEntities;


    public Menu(String name, double price, String description){
        this.name = name;
        this.price=price;
        this.description=description;
    }
    public Menu(){
        //framework required
    }
    public void validate() throws BadParameterException {
        if (price<=0){
            throw new BadParameterException("Price must be larger than 0");
        }
        if (name.isBlank()){
            throw new BadParameterException("Menu name must not be empty");
        }
        if (description.isBlank()){
            throw new BadParameterException("Menu description must not be empty");
        }
        if(imageurl!=null && imageurl.length()>255){
            throw new BadParameterException("Menu Image URL must be shorter than 255 chars");
        }
    }
}
