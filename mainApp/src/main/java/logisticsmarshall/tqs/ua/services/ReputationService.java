package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Reputation;
import logisticsmarshall.tqs.ua.repository.ReputationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReputationService {
    @Autowired
    ReputationRepository reputationRepository;

    public void postReputation(Reputation reputation){
        reputationRepository.save(reputation);
    }
}
