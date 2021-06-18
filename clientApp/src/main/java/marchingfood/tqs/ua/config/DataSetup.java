package marchingfood.tqs.ua.config;

import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.repository.MenuRepository;
import marchingfood.tqs.ua.service.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSetup {
    @Bean
    CommandLineRunner setUpData(UserServiceImpl userService, MenuRepository menuRepository){
        return args -> {
            Client admin = new Client();
            admin.setAdmin(true);
            admin.setEmail("admin@ua.pt");
            admin.setName("admin");
            admin.setPassword("admin");
            admin.setAddress("ADMIN");
            userService.encryptPasswordAndStoreUser(admin);

            Client user1 = new Client();
            user1.setEmail("user1@ua.pt");
            user1.setName("user1");
            user1.setAddress("Userhouse");
            user1.setPassword("12345");
            user1.setAddress("somewhere");
            userService.encryptPasswordAndStoreUser(user1);

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
            Menu menu3 = new Menu();
            menu3.setPrice(5.00);
            menu3.setName("Sbubby Bread");
            menu3.setDescription("Eat Freef");
            menu3.setImageurl("https://i.kym-cdn.com/entries/icons/facebook/000/023/320/subwaylogo.jpg");
            Menu menu4 = new Menu();
            menu4.setPrice(4.5);
            menu4.setName("Ice cream");
            menu4.setDescription("Please buy our icecream online, there is no way this could go wrong :)");
            menu4.setImageurl("https://www.thespruceeats.com/thmb/btLT5e97Xl3vBzNo37xPlUgfQcI=/3135x3900/filters:fill(auto,1)/GettyImages-90053856-588b7aff5f9b5874ee534b04.jpg");
            Menu menu5 = new Menu();
            menu5.setPrice(15);
            menu5.setName("Pizza Mozarella");
            menu5.setDescription("Now with absolutely no sauce whatsoever\n Tomatoes have gone extinct in this dreadfull world of ours\n I fear for what will become of my children");
            menu5.setImageurl("https://images-gmi-pmc.edge-generalmills.com/4cb4c043-e72c-49cf-ba90-99bc4e76cd7b.jpg");
            Menu menu6 = new Menu();
            menu6.setPrice(10);
            menu6.setName("Chow Mein");
            menu6.setDescription("Very good noodles with vegetables that you should try out");
            menu6.setImageurl("https://greenbowl2soul.com/wp-content/uploads/2019/11/veg-chowmein.jpg");
            menuRepository.save(menu1);
            menuRepository.save(menu2);
            menuRepository.save(menu3);
            menuRepository.save(menu4);
            menuRepository.save(menu5);
            menuRepository.save(menu6);
        };
    }
}
