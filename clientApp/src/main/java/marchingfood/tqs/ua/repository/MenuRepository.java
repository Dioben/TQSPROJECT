package marchingfood.tqs.ua.repository;

import marchingfood.tqs.ua.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByNameContains(String contains);
    Menu findById(long id);
}
