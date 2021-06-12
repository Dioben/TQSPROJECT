package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogisticsWebController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @GetMapping("/register")
    public String registration(Model model) {
        if (userServiceImpl.isAuthenticated()) {
            return "redirect:/";
        }
        User user = new User();
        user.setCompany(new Company());
        user.setDriver(new Driver());
        model.addAttribute("user", user);

        return "signUp";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("user") User user,@ModelAttribute("company") Company company,@ModelAttribute("driver") Driver driver , BindingResult bindingResult,  Model model) throws AccountDataException {
        if (user == null){ throw new AccountDataException("Invalid username or password."); }
        if (userServiceImpl.isAuthenticated()) {return "redirect:/"; }
        switch (user.getRole()){
            case "COMPANY": user.setCompany(company);break;
            case "DRIVER": user.setDriver(driver);break;
        }
        userServiceImpl.encryptPasswordAndStoreUser(user);
        System.out.println(user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (userServiceImpl.isAuthenticated()) {
            return "redirect:/";
        }
        if (error != null)
            model.addAttribute("error", error);
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }

    @GetMapping(path="/")
    String index() {
        return "index";
    }

}
