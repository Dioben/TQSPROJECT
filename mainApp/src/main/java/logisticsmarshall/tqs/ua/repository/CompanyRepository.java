package logisticsmarshall.tqs.ua.repository;

import logisticsmarshall.tqs.ua.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findCompanyByApiKey(String apiKey);
}
