package marchingfood.tqs.ua.controller;

import marchingfood.tqs.ua.exceptions.AccessForbiddenException;
import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.model.MenuDTO;
import marchingfood.tqs.ua.service.DeliveryService;
import marchingfood.tqs.ua.service.MenuService;
import marchingfood.tqs.ua.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/admin")
//TODO: RESTRICT THIS WHOLE CLASS TO ADMIN ONLY
public class AdminController {

    @Autowired
    DeliveryService deliveryService;
    @Autowired
    MenuService menuService;
    @Autowired
    UserServiceImpl userService;
    String redirectAdmin = "redirect:/admin/dashboard";

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) throws AccessForbiddenException {
        userService.getUserFromAuthIfAdmin();
        model.addAttribute("menus",menuService.getMenus());
        List<Delivery> deliveries = deliveryService.getDeliveries();
        model.addAttribute("deliveries",deliveries);

        return "adminDash";
    }
    @PostMapping(path="/menu", consumes = {"application/x-www-form-urlencoded"})
    public String postMenu(MenuDTO menuDTO) throws BadParameterException, AccessForbiddenException {
        userService.getUserFromAuthIfAdmin();
        Menu menu = Menu.fromDTO(menuDTO);
        menu.validate();
        menuService.save(menu);
        return redirectAdmin;
    }
    @PostMapping(path="/menu/{id}", consumes = {"application/x-www-form-urlencoded"})
    public String editMenu(MenuDTO menuDTO, @PathVariable long id) throws BadParameterException, AccessForbiddenException {
        userService.getUserFromAuthIfAdmin();
        Menu menu = Menu.fromDTO(menuDTO);
        menu.validate();
        menuService.edit(id,menu);
        return redirectAdmin;
    }
    @GetMapping(path = "/menu/delete/{id}")
    public String deleteMenu(@PathVariable long id) throws AccessForbiddenException {
        userService.getUserFromAuthIfAdmin();
        menuService.tryDelete(id);
        return redirectAdmin;
    }



}
