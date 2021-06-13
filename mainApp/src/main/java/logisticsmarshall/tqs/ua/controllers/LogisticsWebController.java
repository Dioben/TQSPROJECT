package logisticsmarshall.tqs.ua.controllers;

import logisticsmarshall.tqs.ua.exceptions.AccountDataException;
import logisticsmarshall.tqs.ua.model.*;
import logisticsmarshall.tqs.ua.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        User user = convertUserDTOtoUser(userDTO);
        Company company = convertCompanyDTOtoCompany(companyDTO);
        Driver driver = convertDriverDTOtoDriver(driverDTO);

        if (!validateNewUser(user, driver, company))
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

    private boolean validateNewUser(User user, Driver driver, Company company) {
        // https://github.com/Baeldung/spring-security-registration/blob/master/src/main/java/com/baeldung/validation/EmailValidator.java
        String emailRegex = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*+@[A-Za-z0-9-]{2,}(\\.[A-Za-z0-9]{2,})*+$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        // https://regexr.com/2to9u
        String phoneRegex = "([+(\\d]{1})(([\\d() \\-.]){0,11})(\\d{5,})";
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

    private User convertUserDTOtoUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setDriver(userDTO.getDriver());
        user.setCompany(userDTO.getCompany());
        return user;
    }

    private Driver convertDriverDTOtoDriver(DriverDTO driverDTO) {
        Driver driver = new Driver();
        driver.setUser(driverDTO.getUser());
        driver.setPhoneNo(driverDTO.getPhoneNo());
        driver.setStatus(driverDTO.getStatus());
        driver.setVehicle(driverDTO.getVehicle());
        driver.setDelivery(driverDTO.getDelivery());
        driver.setReputation(driverDTO.getReputation());
        return driver;
    }

    private Company convertCompanyDTOtoCompany(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setUser(companyDTO.getUser());
        company.setAddress(companyDTO.getAddress());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setDeliveryType(companyDTO.getDeliveryType());
        company.setApiKey(companyDTO.getApiKey());
        company.setDelivery(companyDTO.getDelivery());
        return company;
    }
}
