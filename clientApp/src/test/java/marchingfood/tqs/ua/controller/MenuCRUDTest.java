package marchingfood.tqs.ua.controller;


import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import lombok.SneakyThrows;
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

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


}
