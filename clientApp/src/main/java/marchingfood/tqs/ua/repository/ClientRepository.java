package marchingfood.tqs.ua.repository;

import marchingfood.tqs.ua.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    Client findById(long id);
    List<Client> findAllByAdmin(boolean admin);
    Client findByEmail(String email);
    Client findByName(String username);
}
