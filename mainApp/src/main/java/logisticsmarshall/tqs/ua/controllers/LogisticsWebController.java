package logisticsmarshall.tqs.ua.controllers;


import logisticsmarshall.tqs.ua.model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("")
public class LogisticsWebController {

    @GetMapping(path="/register")
    String getRegisterForm(Model model) {
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "signup_form";
    }

    @PostMapping(path="/register",consumes = "application/json")
    void processRegisterForm(User user) {
        //Placeholder
    }
    @GetMapping(path="/update")
    String getUpdateForm(Model model) {
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "signup_form";
    }
    @PostMapping(path="/update/company")
    String updateCompany(Model model) {
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "signup_form";
    }
    @PostMapping(path="/update/driver")
    String updateDriver(Model model) {
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "signup_form";
    }
    @PostMapping(path="/login",consumes = "application/json")
    void processLogin(
            @RequestParam(name="type") String type) {
        //Placeholder
    }

}
