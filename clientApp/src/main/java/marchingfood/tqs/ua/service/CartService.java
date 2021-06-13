package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Menu;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private Map<Client, List<Menu>> cart = new HashMap<>();

    public void addMenu(Menu menuAdded,Client client){
        if(!cart.containsKey(client))cart.put(client,new ArrayList<>());
        cart.get(client).add(menuAdded);
    }

    public List<Menu> getClientCart(Client client){
        return cart.get(client);
    }

    public void cleanClientCart(Client client) {cart.get(client).clear();}
}