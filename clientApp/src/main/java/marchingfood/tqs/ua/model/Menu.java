package marchingfood.tqs.ua.model;


import lombok.Data;
import marchingfood.tqs.ua.exceptions.BadParameterException;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    public static Menu fromDTO(MenuDTO menuDTO) {
            Menu menu = new Menu();
            menu.setName(menuDTO.getName());
            menu.setPrice(menuDTO.getPrice());
            menu.setDescription(menuDTO.getDescription());
            menu.setImageurl(menuDTO.getImageurl());
            menu.setOrderEntities(menuDTO.getOrderEntities());
            return menu;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return id == menu.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
