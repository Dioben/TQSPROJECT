package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.model.Delivery;
import logisticsmarshall.tqs.ua.model.Driver;
import logisticsmarshall.tqs.ua.model.User;
import logisticsmarshall.tqs.ua.repository.CompanyRepository;
import logisticsmarshall.tqs.ua.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock(lenient = true)
    private CompanyRepository companyRepository;

    @Mock(lenient = true)
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private User user;

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setVehicle(Driver.Vehicle.CAR);
        driver.setPhoneNo("933333333");
        driver.setStatus(true);

        user = new User();
        user.setId(1L);
        user.setName("driver");
        user.setEmail("driver@mail.com");
        user.setPassword("password");
        user.setDriver(driver);

        delivery = new Delivery();
        delivery.setId(1L);
        delivery.setAddress("A place");


        Mockito.when(deliveryRepository.findDeliveryById(1L)).thenReturn(delivery);
        Mockito.when(deliveryRepository.save(delivery)).thenReturn(delivery);
    }

    @Test
    void whenValidInAcceptDeliveryThenSuccess() {
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { fail(e); }
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertEquals(delivery.getDriver(), user.getDriver());
        assertEquals(delivery.getStage(), Delivery.Stage.ACCEPTED);
    }

    @Test
    void whenInvalidDeliveryIdInAcceptDeliveryThenThrowException() {
        try { deliveryService.acceptDelivery(user, -1L); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDeliveryHasDriverInAcceptDeliveryThenThrowException() {
        delivery.setDriver(new Driver());
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverIsNullInAcceptDeliveryThenThrowException() {
        user.setDriver(null);
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInAcceptDeliveryThenThrowException() {
        user.getDriver().setPhoneNo("");
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasFalseStatusInAcceptDeliveryThenThrowException() {
        user.getDriver().setStatus(false);
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasAcceptedStateInAcceptDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasPickedUpStateInAcceptDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasDeliveredStateInAcceptDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.DELIVERED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.acceptDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenValidInCancelDeliveryThenSuccess() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.cancelDelivery(user, delivery.getId()); }
        catch (Exception e) { fail(e); }
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertNull(delivery.getDriver());
        assertEquals(delivery.getStage(), Delivery.Stage.CANCELED);
    }

    @Test
    void whenInvalidDeliveryIdInCancelDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.cancelDelivery(user, -1L); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDeliveryHasDifferentDriverInCancelDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(new Driver());
        try { deliveryService.cancelDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverIsNullInCancelDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        user.setDriver(null);
        try { deliveryService.cancelDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInCancelDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        user.getDriver().setPhoneNo("");
        try { deliveryService.cancelDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasFalseStatusInCancelDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        user.getDriver().setStatus(false);
        try { deliveryService.cancelDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenValidInPickUpDeliveryThenSuccess() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { fail(e); }
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertEquals(delivery.getStage(), Delivery.Stage.PICKEDUP);
    }

    @Test
    void whenInvalidDeliveryIdInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.pickUpDelivery(user, -1L); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDeliveryHasDifferentDriverInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(new Driver());
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverIsNullInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        user.setDriver(null);
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        user.getDriver().setPhoneNo("");
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasFalseStatusInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        user.getDriver().setStatus(false);
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasRequestedStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.REQUESTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasCanceledStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.CANCELED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasPickedUpStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasDeliveredStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.DELIVERED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.pickUpDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenValidInFinishDeliveryThenSuccess() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { fail(e); }
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertEquals(delivery.getStage(), Delivery.Stage.DELIVERED);
    }

    @Test
    void whenInvalidDeliveryIdInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        try { deliveryService.finishDelivery(user, -1L); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDeliveryHasDifferentDriverInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(new Driver());
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverIsNullInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        user.setDriver(null);
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        user.getDriver().setPhoneNo("");
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasFalseStatusInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        user.getDriver().setStatus(false);
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasRequestedStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.REQUESTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasCanceledStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.CANCELED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasPickedUpStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

    @Test
    void whenDriverHasDeliveredStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.DELIVERED);
        delivery.setDriver(user.getDriver());
        try { deliveryService.finishDelivery(user, delivery.getId()); }
        catch (Exception e) { return; }
        fail();
    }

}
