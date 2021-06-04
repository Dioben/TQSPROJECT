package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;
import logisticsmarshall.tqs.ua.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    DeliveryRepository deliveryRepository;

    //TODO
    public Delivery getDeliveryById(long id) {
        return deliveryRepository.findDeliveryById(id);
    }

    //TODO
    public List<Delivery> getDeliveriesByDriver() {
        return null;
    }
    public List<Delivery> getDeliveriesByCompany() {
        return null;
    }
    public Company getApiKeyHolder(String apiKey) {
        return companyRepository.findCompanyByApiKey(apiKey);
    }
    public boolean driverCanQuery(Driver driver, long deliveryId){
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        return  delivery.getDriver().equals(driver);
    }
    public boolean apiKeyCanQuery(String apiKey, Long deliveryId){
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        return delivery.getCompany().getApiKey().equals(apiKey);
    }
}
