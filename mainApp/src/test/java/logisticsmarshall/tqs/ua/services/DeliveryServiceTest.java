package logisticsmarshall.tqs.ua.services;

import logisticsmarshall.tqs.ua.exceptions.*;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

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
        assertDoesNotThrow(()->deliveryService.acceptDelivery(user, delivery.getId()));
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertEquals(delivery.getDriver(), user.getDriver());
        assertEquals(Delivery.Stage.ACCEPTED, delivery.getStage());
    }

    @Test
    void whenInvalidDeliveryIdInAcceptDeliveryThenThrowException() {
        assertThrows(NullPointerException.class, ()->deliveryService.acceptDelivery(user, -1L));
    }

    @Test
    void whenDeliveryHasDriverInAcceptDeliveryThenThrowException() {
        delivery.setDriver(new Driver());
        assertThrows(DeliveryAlreadyHasDriverException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverIsNullInAcceptDeliveryThenThrowException() {
        user.setDriver(null);
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInAcceptDeliveryThenThrowException() {
        user.getDriver().setPhoneNo("");
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasFalseStatusInAcceptDeliveryThenThrowException() {
        user.getDriver().setBusy(true);
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasAcceptedStateInAcceptDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasPickedUpStateInAcceptDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasDeliveredStateInAcceptDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.DELIVERED);
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.acceptDelivery(user, delivery.getId()));
    }

    @Test
    void whenValidInPickUpDeliveryThenSuccess() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        assertDoesNotThrow(()->deliveryService.pickUpDelivery(user, delivery.getId()));
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertEquals(Delivery.Stage.PICKEDUP, delivery.getStage());
    }

    @Test
    void whenInvalidDeliveryIdInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        assertThrows(NullPointerException.class, ()->deliveryService.pickUpDelivery(user, -1L));
    }

    @Test
    void whenDeliveryHasDifferentDriverInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(new Driver());
        assertThrows(DeliveryDoesntHaveSameDriverException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverIsNullInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        user.setDriver(null);
        assertThrows(DeliveryHasNoDriverException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        user.getDriver().setPhoneNo("");
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasFalseStatusInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        user.getDriver().setBusy(true);
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasRequestedStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.REQUESTED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasCanceledStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.CANCELED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasPickedUpStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasDeliveredStateInPickUpDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.DELIVERED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.pickUpDelivery(user, delivery.getId()));
    }

    @Test
    void whenValidInFinishDeliveryThenSuccess() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        assertDoesNotThrow(()->deliveryService.finishDelivery(user, delivery.getId()));
        Mockito.verify(deliveryRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        assertEquals(Delivery.Stage.DELIVERED, delivery.getStage());
    }

    @Test
    void whenInvalidDeliveryIdInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        assertThrows(NullPointerException.class, ()->deliveryService.finishDelivery(user, -1L));
    }

    @Test
    void whenDeliveryHasDifferentDriverInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(new Driver());
        assertThrows(DeliveryDoesntHaveSameDriverException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverIsNullInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        user.setDriver(null);
        assertThrows(DeliveryHasNoDriverException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasEmptyPhoneNumberInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        user.getDriver().setPhoneNo("");
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasFalseStatusInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.PICKEDUP);
        delivery.setDriver(user.getDriver());
        user.getDriver().setBusy(true);
        assertThrows(AccountCantDeliverException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasRequestedStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.REQUESTED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasCanceledStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.CANCELED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasPickedUpStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.ACCEPTED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void whenDriverHasDeliveredStateInFinishDeliveryThenThrowException() {
        delivery.setStage(Delivery.Stage.DELIVERED);
        delivery.setDriver(user.getDriver());
        assertThrows(DeliveryCantSkipStagesException.class, ()->deliveryService.finishDelivery(user, delivery.getId()));
    }

    @Test
    void driverlessCancelTest(){
        Delivery delivery = new Delivery();
        delivery.setDriver(new Driver());
        Mockito.when(deliveryRepository.findDeliveryById(Mockito.any())).thenReturn(delivery);
        deliveryService.cancelDelivery(1);
        assertNull(delivery.getDriver());
        assertEquals(Delivery.Stage.CANCELED,delivery.getStage());
    }

    @Test
    void driverCancelTest(){
        Delivery delivery = new Delivery();
        Mockito.when(deliveryRepository.findDeliveryById(Mockito.any())).thenReturn(delivery);
        deliveryService.cancelDelivery(1);
        assertNull(delivery.getDriver());
        assertEquals(Delivery.Stage.CANCELED,delivery.getStage());
    }

}
