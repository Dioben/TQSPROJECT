package logisticsmarshall.tqs.ua.controllers;


import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import logisticsmarshall.tqs.ua.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.stream.Collectors;


@Controller
public class LogisticsWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private UserService userService;

    //Security
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), null);
    }

    /*
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<User> user){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(user.getName()))
                .collect(Collectors.toList());
    } */

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

        if (user.getRole().equals("COMPANY")){
            System.out.println("C OWNER TRUE");
            return "updateCompany"; }

        if (user.getRole().equals("ADMINISTRATOR")){
            System.out.println("ADMIN");
            return "index"; }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "updateCompany";
    }



    @GetMapping(path="/update/company")
    String getCompanyForm(Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
        User user = userRepository.findByName(username);
        System.out.print(user.toString());
        /*
        Company company = new Company(user);
        model.addAttribute("company",company);

         company.setAddress("visue"); */
        return "updateCompany";
    }


    @GetMapping(path="/update/driver")
    String getDriverForm(Model model) {
        Driver driver = new Driver();
        model.addAttribute("driver",driver);

        return "updateDriver";
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
        return "login";
    }

    @GetMapping(path="/")
    String index() {
        return "index";
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
