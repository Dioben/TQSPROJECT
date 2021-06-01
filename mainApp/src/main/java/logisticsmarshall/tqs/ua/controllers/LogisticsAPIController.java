package logisticsmarshall.tqs.ua.controllers;


import org.springframework.web.bind.annotation.*;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/")
public class LogisticsAPIController {
    
    @PostMapping(path="/delivery/post",consumes = "application/json")
    void postDelivery(
                    //TODO:Maybe include vehicle
                    @RequestParam(name="address") String address,
                    @RequestParam(name="priority") String priority,
                    @RequestParam(name="APIKey") String apikey) {
        //Placeholder
    }

    @GetMapping(path="/delivery/get")
    void getDeliveries(@RequestParam(name="APIKey") String apikey) {
        //Placeholder
    }

    @GetMapping(path="/deliveries/get")
    void getDelivery(
            @RequestParam(name="id") String delivery_id,
            @RequestParam(name="APIKey") String apikey) {
        //Placeholder
    }
}
