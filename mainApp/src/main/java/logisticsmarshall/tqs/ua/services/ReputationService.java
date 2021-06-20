package logisticsmarshall.tqs.ua.services;


import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.Reputation;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;

import logisticsmarshall.tqs.ua.repository.ReputationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ReputationService {

    @Autowired
    ReputationRepository reputationRepository;

    public void postReputation(Reputation reputation){
        reputationRepository.save(reputation);
    }

    public Reputation getReputationById(long id) {
        return reputationRepository.findReputationById(id);
    }

    public Reputation getReputationByDelivery(Delivery delivery){
        return reputationRepository.findReputationByDelivery(delivery);
    }

    public Set<Reputation> getReputationsByDriver(Driver driver) {
        return driver.getReputation();
    }

    public List<Reputation> getAllReputations() {
        return reputationRepository.findAll();
    }

}
