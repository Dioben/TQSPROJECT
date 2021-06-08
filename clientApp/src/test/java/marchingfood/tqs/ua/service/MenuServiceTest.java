package marchingfood.tqs.ua.service;

import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock(lenient = true)
    MenuRepository menuRepository;
    @InjectMocks
    MenuService service;
    @Test
    void deleteNXErrorTest(){
        Mockito.when(menuRepository.findById(Mockito.anyLong())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class,()-> service.tryDelete(1));
    }
    @Test
    void editNXErrorTest(){
        Mockito.when(menuRepository.findById(Mockito.anyLong())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class,()-> service.edit(1,new Menu("test",1,"test")) );
    }
}
