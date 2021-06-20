package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.Company;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    public boolean apiKeyExits(String apiKey){
        Company company = companyRepository.findCompanyByApiKey(apiKey);
        return company != null;
    }
}
