package marchingfood.tqs.ua.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.service.CartService;
import marchingfood.tqs.ua.service.DeliveryService;
import marchingfood.tqs.ua.service.MenuService;
import marchingfood.tqs.ua.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = DeliveryController.class)
class DeliveryCRUDTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MenuService menuService;

    @MockBean
    CartService cartService;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    DeliveryService deliveryService;

    ObjectMapper jsonParser = new ObjectMapper();

    @SneakyThrows
    @Test
    void getRestaurant_thenReturnAllMenus(){
        Menu menu1 = new Menu();
        menu1.setPrice(7.55);
        menu1.setName("Big MEC");
        menu1.setDescription("We've got your number");
        menu1.setImageurl("https://super.abril.com.br/wp-content/uploads/2017/03/bigmac.png");

        Menu menu2 = new Menu();
        menu2.setPrice(10.55);
        menu2.setName("Sushi");
        menu2.setDescription("Literally everyone sells this now");
        menu2.setImageurl("https://previews.123rf.com/images/yatomo/yatomo1304/yatomo130400133/18975972-sushi-and-rolls-in-bento-box.jpg");
        List<Menu> menusInserted = Arrays.asList(menu1,menu2);
        Mockito.when(menuService.getMenus()).thenReturn(menusInserted);

        mvc.perform(get("/restaurant")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(menu1.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu1.getPrice()))))
                .andExpect(content().string(containsString(menu2.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu2.getPrice()))));
    }

    @SneakyThrows
    @Test
    void postRestaurant_givenNonExistentId_thenResourceNotFound(){
        Mockito.when(menuService.getMenuById(1000000)).thenReturn(null);

        mvc.perform(post("/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParser.writeValueAsString(1000000)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void postRestaurant_givenExistentId_thenSuccess(){
        Menu menu1 = new Menu();
        menu1.setPrice(7.55);
        menu1.setName("Big MEC");
        menu1.setDescription("We've got your number");
        menu1.setImageurl("https://super.abril.com.br/wp-content/uploads/2017/03/bigmac.png");

        Mockito.when(menuService.getMenuById(1)).thenReturn(menu1);

        Client user1 = new Client();
        user1.setEmail("user1@ua.pt");
        user1.setName("user1");
        user1.setAddress("Userhouse");
        user1.setPassword("12345");
        user1.setAddress("somewhere");

        Mockito.when(userService.getUserFromAuthOrException()).thenReturn(user1);

        mvc.perform(post("/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParser.writeValueAsString(1)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getCart_thenReturnAllMenusAdded(){
        Menu menu1 = new Menu();
        menu1.setPrice(7.55);
        menu1.setName("Big MEC");
        menu1.setDescription("We've got your number");
        menu1.setImageurl("https://super.abril.com.br/wp-content/uploads/2017/03/bigmac.png");

        Menu menu2 = new Menu();
        menu2.setPrice(10.55);
        menu2.setName("Sushi");
        menu2.setDescription("Literally everyone sells this now");
        menu2.setImageurl("https://previews.123rf.com/images/yatomo/yatomo1304/yatomo130400133/18975972-sushi-and-rolls-in-bento-box.jpg");
        List<Menu> menusInserted = Arrays.asList(menu1,menu2);

        Client user1 = new Client();
        user1.setEmail("user1@ua.pt");
        user1.setName("user1");
        user1.setAddress("Userhouse");
        user1.setPassword("12345");
        user1.setAddress("somewhere");

        Mockito.when(userService.getUserFromAuthOrException()).thenReturn(user1);

        Mockito.when(cartService.getClientCart(user1)).thenReturn(menusInserted);

        mvc.perform(get("/cart")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(menu1.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu1.getPrice()))))
                .andExpect(content().string(containsString(menu2.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu2.getPrice()))));
    }

    @SneakyThrows
    @Test
    void postCart_whenCartEmpty_thenEmptyPage(){
        Menu menu1 = new Menu();
        menu1.setPrice(7.55);
        menu1.setName("Big MEC");
        menu1.setDescription("We've got your number");
        menu1.setImageurl("https://super.abril.com.br/wp-content/uploads/2017/03/bigmac.png");


        List<Menu> menusInserted = Arrays.asList();

        Client client = new Client();
        Mockito.when(userService.getUserFromAuthOrException()).thenReturn(client);
        Mockito.when(cartService.getClientCart(Mockito.any())).thenReturn(menusInserted);

        mvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(menu1.getName()))));
    }

    @SneakyThrows
    @Test
    void postCart_whenCartWithItems_thenSuccess(){
        Menu menu1 = new Menu();
        menu1.setPrice(7.55);
        menu1.setName("Big MEC");
        menu1.setDescription("We've got your number");
        menu1.setImageurl("https://super.abril.com.br/wp-content/uploads/2017/03/bigmac.png");

        Menu menu2 = new Menu();
        menu2.setPrice(10.55);
        menu2.setName("Sushi");
        menu2.setDescription("Literally everyone sells this now");
        menu2.setImageurl("https://previews.123rf.com/images/yatomo/yatomo1304/yatomo130400133/18975972-sushi-and-rolls-in-bento-box.jpg");
        List<Menu> menusInserted = Arrays.asList(menu1,menu2);

        Client client = new Client();
        Mockito.when(userService.getUserFromAuthOrException()).thenReturn(client);
        Mockito.when(cartService.getClientCart(client)).thenReturn(menusInserted);

        mvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}