package marchingfood.tqs.ua.controller;

import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.service.CartService;
import marchingfood.tqs.ua.service.DeliveryService;
import marchingfood.tqs.ua.service.MenuService;
import marchingfood.tqs.ua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DeliveryController {

    @Autowired
    MenuService menuService;

    @Autowired
    CartService cartService;

    @Autowired
    UserService userService;

    @Autowired
    DeliveryService deliveryService;


    @GetMapping("/restaurant")
    public String restaurantMenuGet(Model model) {
        //Get all menus
        //Make list with thymeleaf from those
        model.addAttribute("menus",menuService.getMenus());
        return "restaurant";
    }

    @PostMapping("/restaurant")
    public ResponseEntity<Object> restaurantMenuAddToCart(@RequestBody int menu_id) {
        //Add post to cart map
        System.out.println("post to cart");
        System.out.println(menu_id);

        Menu gotten = menuService.getMenuById(menu_id);
        //TODO: REPLACE THIS FOR SOMETHING COMING FROM THE WEBSITE
        Client client = userService.getClientById(2);
        if(client == null)return ResponseEntity.status(404).build();
        System.out.println(client);
        System.out.println(gotten);
        cartService.addMenu(gotten,client);
        System.out.println("Added to cart");
        return null;
    }


    @GetMapping("/cart")
    public String deliveryFromCartGet(Model model) {
        //TODO: REPLACE THIS FOR SOMETHING COMING FROM THE WEBSITE
        Client client = userService.getClientById(2);
        List<Menu> menuCart = cartService.getClientCart(client);
        double total = 0;
        for(Menu menu : menuCart)total+=menu.getPrice();
        model.addAttribute("menus",menuCart);
        model.addAttribute("total_price",total);
        return "cart";
    }

    @PostMapping("/cart")
    public void deliveryFromCartPost() {
        //TODO: REPLACE THIS FOR SOMETHING COMING FROM THE WEBSITE
        System.out.println(userService.getAllClients());
        Client client = userService.getClientById(2);
        if(client == null)return;
        System.out.println(client);

        List<Menu> menuCart = cartService.getClientCart(client);
        double total = 0;
        for(Menu menu : menuCart)total+=menu.getPrice();
        System.out.println("Delivery from menu items");
        Delivery deliveryMade = new Delivery();
        List<Menu> menusInDelivery = new ArrayList<Menu>(menuCart);
        deliveryMade.setMenus(menusInDelivery);
        deliveryMade.setAddress(client.getAddress());
        deliveryMade.setClient(client);
        deliveryMade.setDelivered(false);
        deliveryMade.setPaid(false);
        deliveryService.postToLogisticsClient(deliveryMade);
        deliveryService.saveDelivery(deliveryMade);
        System.out.println("Delivery After post");
        cartService.cleanClientCart(client);
    }


}