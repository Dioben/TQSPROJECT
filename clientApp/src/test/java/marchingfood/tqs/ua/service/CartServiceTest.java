package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Menu;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartServiceTest {

    CartService service = new CartService();
    Client client = new Client();
    Menu menu = new Menu();
    @Test
    @Order(1)
    void startCleanTest(){
        assertEquals(service.getClientCart(client),new ArrayList<>());
    }
    @Test
    @Order(2)
    void addandGetTest(){
        ArrayList<Menu> cart = new ArrayList<>();
        cart.add(menu);
        cart.add(menu);
        assertDoesNotThrow(()->service.addMenu(menu,client));
        assertDoesNotThrow(()->service.addMenu(menu,client));
        assertEquals(cart,service.getClientCart(client));
    }


    @Test
    @Order(3)
    void clearTest(){
        service.addMenu(menu,client);
        service.cleanClientCart(client);
        assertEquals(service.getClientCart(client),new ArrayList<>());
    }
}
