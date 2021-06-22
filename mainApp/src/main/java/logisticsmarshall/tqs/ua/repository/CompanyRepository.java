package logisticsmarshall.tqs.ua.repository;

import logisticsmarshall.tqs.ua.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findCompanyByApiKey(String apiKey);
    List<Company> findAllByApiKey(String apiKey);

    Company findCompanyById(Long id);
}
