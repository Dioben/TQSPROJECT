package marchingfood.tqs.ua.controller;

import marchingfood.tqs.ua.exceptions.BadParameterException;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.model.MenuDTO;
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

    String redirectAdmin = "redirect:/admin/dashboard";

    @GetMapping("/dashboard")
    public String adminDashboard(Model model){
        model.addAttribute("menus",menuService.getMenus());
        return "restaurantDash";
    }
    @PostMapping(path="/menu", consumes = {"application/x-www-form-urlencoded"})
    public String postMenu(MenuDTO menuDTO) throws BadParameterException {
        Menu menu = new Menu();
        menu.setName(menuDTO.getName());
        menu.setPrice(menuDTO.getPrice());
        menu.setDescription(menuDTO.getDescription());
        menu.setImageurl(menuDTO.getImageurl());
        menu.setOrderEntities(menuDTO.getOrderEntities());
        menu.validate();
        menuService.save(menu);
        return redirectAdmin;
    }
    @PostMapping(path="/menu/{id}", consumes = {"application/x-www-form-urlencoded"})
    public String editMenu(MenuDTO menuDTO, @PathVariable long id) throws BadParameterException {
        Menu menu = new Menu();
        menu.setName(menuDTO.getName());
        menu.setPrice(menuDTO.getPrice());
        menu.setDescription(menuDTO.getDescription());
        menu.setImageurl(menuDTO.getImageurl());
        menu.setOrderEntities(menuDTO.getOrderEntities());
        menu.validate();
        menuService.edit(id,menu);
        return redirectAdmin;
    }
    @GetMapping(path = "/menu/delete/{id}")
    public String deleteMenu(@PathVariable long id){
        menuService.tryDelete(id);
        return redirectAdmin;
    }

}
