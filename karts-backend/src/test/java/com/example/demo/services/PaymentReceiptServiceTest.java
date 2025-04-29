package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentReceiptServiceTest {

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PaymentReceiptRepository paymentReceiptRepository;

    @Mock
    private SpecialDayRepository specialDayRepository;

    @InjectMocks
    private PaymentReceiptService paymentReceiptService;

    private ReservationEntity reservation;
    private ClientEntity client;
    private SpecialDayEntity specialDay;
    private PaymentDetailEntity paymentDetail;

    @BeforeEach
    void setUp() {
        reservation = new ReservationEntity();
        reservation.setReservationCode("RES123");
        reservation.setStartDateTime(LocalDate.of(2025, 5, 1).atStartOfDay());
        reservation.setLaps(3);
        reservation.setNumberOfPeople(5);
        reservation.setClientRuts(Arrays.asList("12345678", "23456789"));

        client = new ClientEntity();
        client.setName("John Doe");
        client.setRut("12345678");
        client.setBirthDate(LocalDate.of(1990, 5, 1));
        client.setMonthlyVisitCount(8);

        specialDay = new SpecialDayEntity();
        specialDay.setPriceMultiplier(1.5);
        specialDay.setType("WEEKEND");

        paymentDetail = new PaymentDetailEntity();
        paymentDetail.setClientName("John Doe");
        paymentDetail.setAmount(100.0f);
    }


    @Test
    void whenGenerateReceipt_noPriceConfigured_thenThrowException() {
        // Given
        when(pricingRepository.getTotalPriceByLaps(3)).thenReturn(null);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentReceiptService.generateReceipt(reservation);
        });
        assertThat(exception.getMessage()).contains("No hay precio configurado para esta cantidad de vueltas");
    }

    @Test
    void whenReceiptExists_thenReturnReceipt() {
        // Given
        String reservationCode = "123ABC";
        PaymentReceiptEntity mockReceipt = new PaymentReceiptEntity();
        mockReceipt.setReservationCode(reservationCode);

        // Simulamos que el repositorio devuelve un recibo para el código de reserva dado
        when(paymentReceiptRepository.findByReservationCode(reservationCode)).thenReturn(mockReceipt);

        // When
        PaymentReceiptEntity result = paymentReceiptService.getReceiptByReservationCode(reservationCode);

        // Then
        assertNotNull(result, "El recibo no debe ser nulo");
        assertEquals(reservationCode, result.getReservationCode(), "El código de reserva debe coincidir");
    }

    @Test
    void whenReceiptNotExists_thenReturnNull() {
        // Given
        String reservationCode = "123ABC";

        // Simulamos que el repositorio no encuentra un recibo para el código de reserva dado
        when(paymentReceiptRepository.findByReservationCode(reservationCode)).thenReturn(null);

        // When
        PaymentReceiptEntity result = paymentReceiptService.getReceiptByReservationCode(reservationCode);

        // Then
        assertNull(result, "El recibo debe ser nulo si no se encuentra en el repositorio");
    }






    @Test
    void whenGetAllReceipts_thenReturnListOfReceipts() {
        // Given
        PaymentReceiptEntity receipt = new PaymentReceiptEntity();
        when(paymentReceiptRepository.findAll()).thenReturn(Collections.singletonList(receipt));

        // When
        List<PaymentReceiptEntity> receipts = paymentReceiptService.getAllReceipts();

        // Then
        assertThat(receipts).hasSize(1);
        assertThat(receipts.get(0)).isEqualTo(receipt);
    }

    @Test
    void whenGenerateReceipt_onHoliday_thenHolidayMultiplierIsApplied() {
        // GIVEN
        ReservationEntity reservation = new ReservationEntity();
        reservation.setLaps(5);
        reservation.setNumberOfPeople(2);
        reservation.setStartDateTime(LocalDateTime.of(2025, 4, 29, 10, 0)); // Fecha de feriado simulada
        reservation.setClientRuts(List.of("111", "222"));
        reservation.setReservationCode("ABC123");

        // Precio total por vueltas
        Mockito.when(pricingRepository.getTotalPriceByLaps(5)).thenReturn(200.0f);

        // Simula día feriado
        SpecialDayEntity holiday = new SpecialDayEntity();
        holiday.setPriceMultiplier(1.5);
        Mockito.when(specialDayRepository.findByDate(reservation.getStartDateTime().toLocalDate()))
                .thenReturn(holiday);

        // Simula que no hay especial de cumpleaños
        Mockito.when(specialDayRepository.findByType("BIRTHDAY")).thenReturn(null);

        // Clientes
        ClientEntity client1 = new ClientEntity();
        client1.setName("Juan");
        client1.setRut("111");
        client1.setBirthDate(LocalDate.of(1990, 5, 10));
        client1.setMonthlyVisitCount(1);

        ClientEntity client2 = new ClientEntity();
        client2.setName("Maria");
        client2.setRut("222");
        client2.setBirthDate(LocalDate.of(1992, 6, 15));
        client2.setMonthlyVisitCount(1);

        Mockito.when(clientRepository.findByRutIn(List.of("111", "222")))
                .thenReturn(List.of(client1, client2));

        // Captura del resultado
        ArgumentCaptor<PaymentReceiptEntity> captor = ArgumentCaptor.forClass(PaymentReceiptEntity.class);
        Mockito.when(paymentReceiptRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0)); // devuelve el mismo objeto

        // WHEN
        PaymentReceiptEntity result = paymentReceiptService.generateReceipt(reservation);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals("ABC123", result.getReservationCode());
        Assertions.assertEquals(2, result.getPaymentDetails().size());

        for (PaymentDetailEntity detail : result.getPaymentDetails()) {
            // basePricePerPerson = 200 / 2 = 100
            // priceAfterMultiplier = 100 * 1.5 = 150
            // no hay descuento grupal ni frecuencia ni cumpleaños
            Assertions.assertEquals(150.0f, detail.getAmount(), 0.01);
        }

        // Verifica que se guardó el recibo
        Mockito.verify(paymentReceiptRepository, Mockito.times(1)).save(Mockito.any());
    }


}

