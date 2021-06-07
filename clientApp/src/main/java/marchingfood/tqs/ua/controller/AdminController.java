package marchingfood.tqs.ua.controller;

import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin")
//TODO: RESTRICT THIS WHOLE CLASS TO ADMIN ONLY
public class AdminController {

    @Autowired
    MenuService menuService;

    @GetMapping("/dashboard")
    String adminDashboard(Model model){

        return "restaurantDash";
    }
    @PostMapping(path="/menu", consumes = {"application/x-www-form-urlencoded"})
    String postMenu(Menu menu) throws BadParameterException {
        if (menu.getPrice()<=0){
           throw new BadParameterException("Price must be larger than 0");
        }
        if (menu.getName().isBlank()){
            throw new BadParameterException("Menu name must not be empty");
        }
        if (menu.getDescription().isBlank()){
            throw new BadParameterException("Menu description must not be empty");
        }
        menuService.save(menu);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/menus")
    String getMenus(Model model){
        model.addAttribute("menus", menuService.getMenus());
        return "restaurantDash";
    }


}
