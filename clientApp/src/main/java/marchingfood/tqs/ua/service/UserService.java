package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    ClientRepository clientRepository;

    public Client getClientById(long client_id){return clientRepository.findById(client_id);}


}
