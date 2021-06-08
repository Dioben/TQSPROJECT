package marchingfood.tqs.ua.controller;

import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
//TODO: RESTRICT THIS WHOLE CLASS TO ADMIN ONLY
public class AdminController {

    @Autowired
    MenuService menuService;

    @GetMapping("/dashboard")
    String adminDashboard(Model model){
        model.addAttribute("menus",menuService.getMenus());
        return "restaurantDash";
    }
    @PostMapping(path="/menu", consumes = {"application/x-www-form-urlencoded"})
    String postMenu(Menu menu) throws BadParameterException {
        menu.validate();
        menuService.save(menu);
        return "redirect:/admin/dashboard";
    }
    @PostMapping(path="/menu/{id}", consumes = {"application/x-www-form-urlencoded"})
    String editMenu(Menu menu, @PathVariable long id) throws BadParameterException {
        menu.validate();
        menuService.edit(id,menu);
        return "redirect:/admin/dashboard";
    }
    @GetMapping(path = "/menu/delete/{id}")
    String deleteMenu(@PathVariable long id){
        menuService.tryDelete(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/menus")
    String getMenus(Model model){
        model.addAttribute("menus", menuService.getMenus());
        return "restaurantDash";
    }


}
