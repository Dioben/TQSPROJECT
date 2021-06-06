package logisticsmarshall.tqs.ua.controllers;


import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class LogisticsWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //Security
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    @GetMapping(path="/register")
    String getRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "signUp";
    }

    @PostMapping(path="/register")
    String processRegisterForm(@ModelAttribute("user") User user,  Model model) throws AccountDataException {
        System.out.println("ROLE:  " + user.getRole());
        if (user == null){ throw new AccountDataException("Invalid username or password."); }
        if (isAuthenticated()) {return "redirect:/index"; }


        if (user.getRole().equals("DRIVER")){
            System.out.println("DRIVER TRUE");
            return "updateDriver"; }

        if (user.getRole().equals("COMPANYOWNER")){
            System.out.println("C OWNER TRUE");
            return "updateCompany"; }

        if (user.getRole().equals("ADMINISTRATOR")){
            System.out.println("ADMIN");
            return "index"; }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "updateCompany";
    }



    @GetMapping(path="/update/company")
    String getCompanyForm(Model model) {
        Company company = new Company();
        model.addAttribute("company",company);
        return "updateCompany";
    }

    @PostMapping(path="/update/company")
    String updateCompany(Model model,@ModelAttribute("company") Company company, @CurrentSecurityContext(expression="authentication.name") String username) {
        /*
        System.out.print(username);
        System.out.print("AQUI");
        User user = userRepository.findByName(username);
        System.out.print(user.toString());

        company.setUser(user); */
        //company.setApiKey();//?????
        //company.setId(); //????



        return "updateCompany";
    }



    @PostMapping(path="/update/driver")
    String updateDriver(Model model) {
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "updateDriver";
    }


    //Login
    @GetMapping(path="/login")
    String getLoginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping(path="/login",consumes = "application/json")
    String processLogin(@ModelAttribute("user") User user,  Model model, BindingResult result) {
        User user1 = userRepository.findByEmail(user.getEmail());
        if(user1.getEmail().equals(user.getEmail()) && user1.getPassword().equals(passwordEncoder.encode(user.getPassword()))){
            return "index";
        }else{
            return "login";
        }
    }




    /*
    @GetMapping(path="/update")
    String getUpdateForm(Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
        User user = userRepository.findByName(username);
        System.out.print(user.toString());
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "updateCompany";
    } */





}
