package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.regex.Pattern;

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
        System.out.println("/register");
        if (!validateNewUser(user, driver, company))
            throw new AccountDataException("Invalid username or password.");
        if (userServiceImpl.isAuthenticated())
            return "redirect:/";
        switch (user.getRole()){
            case "COMPANY": user.setCompany(company);break;
            case "DRIVER": user.setDriver(driver);break;
        }
        try {
            userServiceImpl.encryptPasswordAndStoreUser(user);
        } catch (DataIntegrityViolationException e) {
            throw new AccountDataException("Invalid username or password");
        }
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

    public boolean validateNewUser(User user, Driver driver, Company company) {
        // https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        // https://regexr.com/38pvb
        String phoneRegex = "^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$";
        Pattern phonePattern = Pattern.compile(phoneRegex);

        return user.getName() != null
                && user.getEmail() != null
                && user.getPassword() != null
                && user.getRole() != null
                && emailPattern.matcher(user.getEmail()).matches()
                && ((user.getRole().equals("DRIVER")
                        && driver.getPhoneNo() != null
                        && driver.getVehicle() != null
                        && phonePattern.matcher(driver.getPhoneNo()).matches())
                    || (user.getRole().equals("COMPANY")
                        && company.getPhoneNumber() != null
                        && company.getAddress() != null
                        && company.getDeliveryType() != null
                        && phonePattern.matcher(company.getPhoneNumber()).matches()));
    }
}
