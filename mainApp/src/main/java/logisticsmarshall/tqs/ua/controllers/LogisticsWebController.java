package logisticsmarshall.tqs.ua.controllers;


import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.UserRepository;
import logisticsmarshall.tqs.ua.services.UserService;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LogisticsWebController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public void autoLogin(String username, String password) {
        UserDetails userDetails = userServiceImpl.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    @GetMapping("/register")
    public String registration(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        User user = new User();
        user.setCompany(new Company());
        user.setDriver(new Driver());
        model.addAttribute("user", user);

        return "signUp";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("user") User user, BindingResult bindingResult,  Model model) throws AccountDataException {
        System.out.println(user.toString());

        if (user == null){ throw new AccountDataException("Invalid username or password."); }
        if (isAuthenticated()) {return "redirect:/"; }
        //autoLogin(user.getName(), user.getPassword());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }

    @GetMapping(path="/")
    String index() {
        return "index";
    }














    /*
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

        if (user.getRole().equals("COMPANY")){
            System.out.println("C OWNER TRUE");
            return "updateCompany"; }

        if (user.getRole().equals("ADMINISTRATOR")){
            System.out.println("ADMIN");
            return "index"; }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "updateCompany";
    } */








    /*
    //Login
    @GetMapping(path="/login")
    String getLoginForm(Model model) {
        return "login";
    }

    @GetMapping(path="/")
    String index() {
        return "index";
    }
    */
    /*
    @PostMapping(path="/login",consumes = "application/json")
    String processLogin(@ModelAttribute("user") User user,  Model model, BindingResult result) {
        User user1 = userRepository.findByEmail(user.getEmail());
        if(user1.getEmail().equals(user.getEmail()) && user1.getPassword().equals(passwordEncoder.encode(user.getPassword()))){
            return "index";
        }else{
            return "login";
        }
    } */




    /*
    @GetMapping(path="/update")
    String getUpdateForm(Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
        User user = userRepository.findByName(username);
        System.out.print(user.toString());
        model.addAttribute("user", new User("x","y","d", "ADMIN"));
        return "updateCompany";
    } */





}
