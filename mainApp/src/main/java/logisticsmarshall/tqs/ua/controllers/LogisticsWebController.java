package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.exceptions.AccessForbiddenException;
import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
        User user = userServiceImpl.getUserFromAuth();
        if (user==null){return "redirect:/info";}
        if (user.getRole().equals("COMPANY")){return "redirect:/companyDash";}
        if (user.getRole().equals("DRIVER")){return "redirect:/driverDash";}
        if (user.getRole().equals("ADMIN")){return "redirect:/adminDash";}
        return "redirect:/info";
    }

    @GetMapping(path="/info")
    public String info() {
        return "info";
    }


    @GetMapping(path="/companyDash")
    public String companyDash(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials("COMPANY");
        return "mainDash";
    }


    @GetMapping(path="/driverDash")
    public String workerDash(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials("DRIVER");
        return "mainDash";
    }


    @GetMapping(path="/companyProfile")
    public String companyProfile(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials("COMPANY");
        return "businessOwnerProfile";
    }


    @GetMapping(path="/driverProfile")
    public String driverProfile(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials("DRIVER");
        return "workerProfile";
    }

    @GetMapping(path="/adminDash")
    public String adminDashboard(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials("ADMIN");
        return "adminDash";
    }

    @PostMapping(path="/grantApiAccess")
    public String adminDashboard() throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials("ADMIN");
        return "redirect:/adminDash";
    }

}
