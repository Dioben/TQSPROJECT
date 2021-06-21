package marchingfood.tqs.ua.controller;

import marchingfood.tqs.ua.exceptions.AccessForbiddenException;
import marchingfood.tqs.ua.exceptions.AccountDataException;
import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.*;
import marchingfood.tqs.ua.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DeliveryController {

    @Autowired
    MenuService menuService;

    @Autowired
    CartService cartService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    DeliveryService deliveryService;

    String redirectRestaurant = "redirect:/restaurant";

    @GetMapping("/register")
    public String registration(Model model) {
        if (userService.isAuthenticated()) {
            return redirectRestaurant;
        }
        Client user = new Client();
        model.addAttribute("user", user);

        return "signUp";
    }

    @PostMapping("/register")
    public String registration(ClientDTO userDTO) throws AccountDataException {
        Client user = Client.fromDTO(userDTO);
        if (!Client.validateNewUser(user))
            throw new AccountDataException();
        if (userService.isAuthenticated())
            return redirectRestaurant;
        try {
            userService.encryptPasswordAndStoreUser(user);
        } catch (DataIntegrityViolationException e) {
            throw new AccountDataException();
        }
        return "login";
    }

    @GetMapping("/restaurant")
    public String restaurantMenuGet(Model model) {
        //Get all menus
        //Make list with thymeleaf from those
        model.addAttribute("menus",menuService.getMenus());
        return "restaurant";

    }
    @GetMapping("/review")
    public String reviewReload(){
        return "redirect:/profile";
    }
    @PostMapping("/review")
    public String postReview(Model model, Review review) throws AccessForbiddenException, BadParameterException {
        Client user = userService.getUserFromAuthOrException();
        deliveryService.postReview(review);
        model.addAttribute("user",user);
        model.addAttribute("deliveries",deliveryService.getClientDeliveriesFromLogistics(user));
        model.addAttribute("message","Your Review has been posted successfully");
        return "profile";
    }


    @GetMapping("/profile")
    public String userProfile(Model model) throws AccessForbiddenException {
        Client user = userService.getUserFromAuthOrException();
        model.addAttribute("user",user);
        model.addAttribute("deliveries",deliveryService.getClientDeliveriesFromLogistics(user));
        return "profile";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (userService.isAuthenticated()) {
            return redirectRestaurant;
        }
        if (error != null)
            model.addAttribute("error", error);
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }

    @GetMapping(path = "/")
    public String index(){
    return "index";
    }

    @PostMapping("/restaurant")
    public ResponseEntity<Object> restaurantMenuAddToCart(@RequestBody int menuId) throws AccessForbiddenException {
        //Add post to cart map
        Menu gotten = menuService.getMenuById(menuId);
        Client client = userService.getUserFromAuthOrException();
        if(client == null)return ResponseEntity.status(404).build();
        cartService.addMenu(gotten,client);
        return null;
    }


    @GetMapping("/cart")
    public String deliveryFromCartGet(Model model) throws AccessForbiddenException {
        Client client = userService.getUserFromAuthOrException();
        List<Menu> menuCart = cartService.getClientCart(client);
        if(menuCart.isEmpty()){
            model.addAttribute("empty_cart",true);
            return "cart";
        }
        model.addAttribute("empty_cart",false);
        double total = 0;
        for(Menu menu : menuCart)total+=menu.getPrice();
        model.addAttribute("menus",menuCart);
        model.addAttribute("total_price",total);
        return "cart";
    }

    @PostMapping("/cart")
    @ResponseStatus(HttpStatus.OK)
    public void deliveryFromCartPost() throws AccessForbiddenException, BadParameterException {
        Client client = userService.getUserFromAuthOrException();
        List<Menu> menuCart = cartService.getClientCart(client);
        double total = 0;
        for(Menu menu : menuCart)total+=menu.getPrice();
        Delivery deliveryMade = new Delivery();
        List<Menu> menusInDelivery = new ArrayList<>(menuCart);
        deliveryMade.setMenus(menusInDelivery);
        deliveryMade.setAddress(client.getAddress());
        deliveryMade.setClient(client);
        deliveryMade.setDelivered(false);
        deliveryMade.setPaid(false);

        Payment payment = new Payment();
        payment.setPrice(total);
        deliveryMade.setPayment(payment);

        deliveryService.savePayment(payment);

        deliveryMade = deliveryService.postToLogisticsClient(deliveryMade);
        deliveryService.saveDelivery(deliveryMade);
        cartService.cleanClientCart(client);
    }


}