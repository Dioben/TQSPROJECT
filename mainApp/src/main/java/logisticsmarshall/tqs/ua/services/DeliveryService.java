package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.exceptions.*;
import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;
import logisticsmarshall.tqs.ua.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryService {

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    DeliveryRepository deliveryRepository;

    public Delivery getDeliveryById(long id) {
        return deliveryRepository.findDeliveryById(id);
    }

    public void postDelivery(Delivery delivery){
        deliveryRepository.save(delivery);
    }
    public List<Delivery> getDeliveriesByDriver(Driver driver) {
        return deliveryRepository.findAllByDriverId(driver.getId());
    }
    public List<Delivery> getDeliveriesByCompany(Company company) {
        return deliveryRepository.findAllDeliveriesByCompanyId(company.getId());
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


    @Transactional
    public void acceptDelivery(User user, long deliveryId) throws DeliveryAlreadyHasDriverException, AccountCantDeliverException, DeliveryDoesntHaveSameDriverException, DeliveryHasNoDriverException, DeliveryCantSkipStagesException {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        Driver driver = validateDeliveryChange(user, delivery, false);
        if (delivery.getStage() != Delivery.Stage.REQUESTED && delivery.getStage() != Delivery.Stage.CANCELED)
            throw new DeliveryCantSkipStagesException();
        delivery.setDriver(driver);
        delivery.setStage(Delivery.Stage.ACCEPTED);
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void cancelDelivery(User user, long deliveryId) throws DeliveryDoesntHaveSameDriverException, AccountCantDeliverException, DeliveryAlreadyHasDriverException, DeliveryHasNoDriverException {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        validateDeliveryChange(user, delivery, true);
        delivery.setDriver(null);
        delivery.setStage(Delivery.Stage.CANCELED);
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void pickUpDelivery(User user, long deliveryId) throws DeliveryDoesntHaveSameDriverException, AccountCantDeliverException, DeliveryAlreadyHasDriverException, DeliveryHasNoDriverException, DeliveryCantSkipStagesException {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        validateDeliveryChange(user, delivery, true);
        if (delivery.getStage() != Delivery.Stage.ACCEPTED)
            throw new DeliveryCantSkipStagesException();
        delivery.setStage(Delivery.Stage.PICKEDUP);
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void finishDelivery(User user, long deliveryId) throws DeliveryDoesntHaveSameDriverException, AccountCantDeliverException, DeliveryAlreadyHasDriverException, DeliveryHasNoDriverException, DeliveryCantSkipStagesException {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        validateDeliveryChange(user, delivery, true);
        if (delivery.getStage() != Delivery.Stage.PICKEDUP)
            throw new DeliveryCantSkipStagesException();
        delivery.setStage(Delivery.Stage.DELIVERED);
        deliveryRepository.save(delivery);
    }

    private Driver validateDeliveryChange(User user, Delivery delivery, boolean shouldHaveDriver) throws DeliveryAlreadyHasDriverException, AccountCantDeliverException, DeliveryDoesntHaveSameDriverException, DeliveryHasNoDriverException {
        if (delivery == null)
            throw new NullPointerException("Delivery doesn't exist");
        if (shouldHaveDriver) {
            if (delivery.getDriver() == null)
                throw new DeliveryHasNoDriverException();
            if (delivery.getDriver() != user.getDriver())
                throw new DeliveryDoesntHaveSameDriverException();
        } else {
            if (delivery.getDriver() != null)
                throw new DeliveryAlreadyHasDriverException();
        }
        Driver driver = user.getDriver();
        if (driver == null
                || driver.getPhoneNo().isEmpty()
                || Boolean.TRUE.equals(driver.getStatus()))
            throw new AccountCantDeliverException();
        return driver;
    }

    public List<Delivery> getDeliveriesByStage(Delivery.Stage requested) {
        return deliveryRepository.findAllDeliveriesByStage(requested);
    }
}
