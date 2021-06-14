package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.exceptions.AccessForbiddenException;
import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogisticsWebController {
    static final String ADMINROLE = "ADMIN";
    static final String COMPANYROLE = "COMPANY";
    static final String DRIVERROLE = "DRIVER";
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

        if (user.getRole().equals(COMPANYROLE))
            user.setCompany(company);
        else if (user.getRole().equals(DRIVERROLE))
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
        if (user.getRole().equals(COMPANYROLE)){return "redirect:/companyDash";}
        if (user.getRole().equals(DRIVERROLE)){return "redirect:/driverDash";}
        if (user.getRole().equals(ADMINROLE)){return "redirect:/adminDash";}
        return "redirect:/info";
    }

    @GetMapping(path="/info")
    public String info() {
        return "info";
    }


    @GetMapping(path="/companyDash")
    public String companyDash(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials(COMPANYROLE);
        return "mainDash";
    }


    @GetMapping(path="/driverDash")
    public String workerDash(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials(DRIVERROLE);
        return "mainDash";
    }


    @GetMapping(path="/companyProfile")
    public String companyProfile(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials(COMPANYROLE);
        return "businessOwnerProfile";
    }


    @GetMapping(path="/driverProfile")
    public String driverProfile(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials(DRIVERROLE);
        return "workerProfile";
    }

    @GetMapping(path="/adminDash")
    public String adminDashboard(Model model) throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials(ADMINROLE);
        return "adminDash";
    }

    @PostMapping(path="/grantApiAccess")
    public String adminDashboard() throws AccessForbiddenException {
        User user = userServiceImpl.getUserFromAuthAndCheckCredentials(ADMINROLE);
        return "redirect:/adminDash";
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping(path="/delivery/{id}/accept")
    public String acceptDelivery(
            @PathVariable(name="id") long deliveryId,
            Model model) {
        if (!userServiceImpl.isAuthenticated())
            return redirectRoot;
        try {
            userServiceImpl.acceptDelivery(deliveryId);
        } catch (Exception e) {
            return redirectRoot;
        }
        return "mainDash";
    }

}
