package logisticsmarshall.tqs.ua.controllers;


import logisticsmarshall.tqs.ua.model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/website/")
public class LogisticsWebController {

    @GetMapping(path="/register")
    String getRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping(path="/register",consumes = "application/json")
    void processRegisterForm(
            @RequestParam(name="APIKey") String apikey,
            User user) {
        //Placeholder
    }
    @PostMapping(path="/login",consumes = "application/json")
    void processLogin(
            @RequestParam(name="type") String type) {
        //Placeholder
    }

}
