package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.regex.Pattern;

@Controller
public class LogisticsWebController {

    @Autowired
    UserServiceImpl userServiceImpl;

    String redirectRoot = "redirect:/";

    @GetMapping("/register")
    public String registration(Model model) {
        if (userServiceImpl.isAuthenticated()) {
            return redirectRoot;
        }
        User user = new User();
        user.setCompany(new Company());
        user.setDriver(new Driver());
        model.addAttribute("user", user);

        return "signUp";
    }

    @PostMapping("/register")
    public String registration(UserDTO userDTO, CompanyDTO companyDTO, DriverDTO driverDTO) throws AccountDataException {
        User user = User.fromDTO(userDTO);
        Company company = Company.fromDTO(companyDTO);
        Driver driver = Driver.fromDTO(driverDTO);

        if (!User.validateNewUser(user, driver, company))
            throw new AccountDataException();
        if (userServiceImpl.isAuthenticated())
            return redirectRoot;

        if (user.getRole().equals("COMPANY"))
            user.setCompany(company);
        else if (user.getRole().equals("DRIVER"))
            user.setDriver(driver);
        else
            throw new AccountDataException();

        try {
            userServiceImpl.encryptPasswordAndStoreUser(user);
        } catch (DataIntegrityViolationException e) {
            throw new AccountDataException();
        }
        return redirectRoot;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (userServiceImpl.isAuthenticated()) {
            return redirectRoot;
        }
        if (error != null)
            model.addAttribute("error", error);
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }

    @GetMapping(path="/")
    public String index() {
        return "index";
    }


    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping(path="/companyDash")
    public String companyDash(Model model){
        return "mainDash";
    }

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping(path="/driverDash")
    public String workerDash(Model model){
        return "mainDash";
    }

    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping(path="/companyProfile")
    public String companyProfile(Model model){
        return "businessOwnerProfile";
    }

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping(path="/driverProfile")
    public String driverProfile(Model model){
        return "workerProfile";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path="/adminDashboard")
    public String adminDashboard(Model model){
        return "adminDash";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path="/grantApiAccess")
    public String adminDashboard(){
        return "redirect:/adminDash";
    }

}
