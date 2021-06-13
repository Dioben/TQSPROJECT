package marchingfood.tqs.ua.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import lombok.SneakyThrows;
import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.service.CartService;
import marchingfood.tqs.ua.service.MenuService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
class DeliveryCRUDTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MenuService menuService;

    @MockBean
    CartService cartService;

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
                .andExpect(content().string(containsString(menu1.getDescription())))
                .andExpect(content().string(containsString(menu2.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu2.getPrice()))))
                .andExpect(content().string(containsString(menu2.getDescription())));
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

        mvc.perform(post("/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParser.writeValueAsString(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1)));
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

        //TODO: REPLACE THIS FOR A MOCK OF SOME WAY OF GETTING CURRENT USER
        Client client = new Client();

        Mockito.when(cartService.getClientCart(client)).thenReturn(menusInserted);

        mvc.perform(get("/cart")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(menu1.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu1.getPrice()))))
                .andExpect(content().string(containsString(menu1.getDescription())))
                .andExpect(content().string(containsString(menu2.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu2.getPrice()))))
                .andExpect(content().string(containsString(menu2.getDescription())));
    }

    @SneakyThrows
    @Test
    void postCart_whenCartEmpty_thenResourceNotFound(){
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

        //TODO: REPLACE THIS FOR A MOCK OF SOME WAY OF GETTING CURRENT USER
        Client client = new Client();

        Mockito.when(cartService.getClientCart(client)).thenReturn(null);

        mvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isNotFound());
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

        //TODO: REPLACE THIS FOR A MOCK OF SOME WAY OF GETTING CURRENT USER
        Client client = new Client();

        Mockito.when(cartService.getClientCart(client)).thenReturn(menusInserted);

        mvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}