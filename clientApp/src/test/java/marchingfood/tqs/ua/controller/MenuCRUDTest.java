package marchingfood.tqs.ua.controller;


import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import lombok.SneakyThrows;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.service.MenuService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminController.class)
public class MenuCRUDTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MenuService serviceMock;

    @SneakyThrows
    @Test
    void badPriceNewMenuTest(){
        mvc.perform(post("/admin/menu")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("price", "-1"),
                        new BasicNameValuePair("name", "test"),
                        new BasicNameValuePair("description", "test")
                ))))).andExpect(status().is(400));
    }
    @SneakyThrows
    @Test
    void badNameNewMenuTest(){
        mvc.perform(post("/admin/menu")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("price", "10"),
                        new BasicNameValuePair("name", ""),
                        new BasicNameValuePair("description", "test")
                ))))).andExpect(status().is(400));
    }
    @SneakyThrows
    @Test
    void badDescriptionNewMenuTest(){
        mvc.perform(post("/admin/menu")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("price", "6.48"),
                        new BasicNameValuePair("name", "test"),
                        new BasicNameValuePair("description", "")
                ))))).andExpect(status().is(400));
    }
    @SneakyThrows
    @Test
    void GoodMenuDataNewMenuTest(){
        mvc.perform(post("/admin/menu")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("price", "6.48"),
                        new BasicNameValuePair("name", "test"),
                        new BasicNameValuePair("description", "test")
                ))))).andExpect(status().is(302));
    }

    @SneakyThrows
    @Test
    void deleteNXTest(){
        Mockito.doThrow(ResourceNotFoundException.class).when(serviceMock).tryDelete(Mockito.anyLong());
        mvc.perform(get("/admin/menu/delete/1"))
                .andExpect(status().is(404));
    }
    @SneakyThrows
    @Test
    void deleteOKTest(){
        Mockito.reset(serviceMock);
        mvc.perform(get("/admin/menu/delete/1"))
                .andExpect(status().is(302));
    }


    //see menus
    @SneakyThrows
    @Test
    void whenSeeMenus_thenReturnAllMenuData() {
        Menu menu = new Menu("menu1", 10.5,"tasty");
        given(serviceMock.getMenus()).willReturn(Arrays.asList(menu));
        mvc.perform(get("/admin/menus")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(menu.getName())))
                .andExpect(content().string(containsString(String.valueOf(menu.getPrice()))))
                .andExpect(content().string(containsString(menu.getDescription())));
    }




}