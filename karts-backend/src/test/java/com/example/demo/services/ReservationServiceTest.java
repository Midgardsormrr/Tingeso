import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private KartRepository kartRepository;

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private PaymentReceiptRepository paymentReceiptRepository;

    @Mock
    private PaymentReceiptService paymentReceiptService;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidReservation_whenSaveReservation_thenReservationIsSavedSuccessfully() {
        // Given
        ReservationEntity reservation = createValidReservation();
        mockPricingAndAvailability();

        // When
        ReservationEntity result = reservationService.saveReservation(reservation);

        // Then
        assertEquals("CONFIRMED", result.getStatus());
        verify(reservationRepository).save(any());
        verify(paymentReceiptRepository).save(any());
    }

    @Test
    void givenInvalidPeopleCount_whenSaveReservation_thenThrowException() {
        // Given
        ReservationEntity reservation = createValidReservation();
        reservation.setNumberOfPeople(0); // inválido

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.saveReservation(reservation));
        assertTrue(ex.getMessage().contains("Número de personas inválido"));
    }

    @Test
    void givenOverlappingReservation_whenSaveReservation_thenThrowException() {
        // Given
        ReservationEntity reservation = createValidReservation();
        ReservationEntity conflicting = new ReservationEntity();
        when(reservationRepository.findByStartDateTimeLessThanAndEndDateTimeGreaterThan(any(), any()))
                .thenReturn(List.of(conflicting));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.saveReservation(reservation));
        assertTrue(ex.getMessage().contains("Horario no disponible"));
    }

    @Test
    void givenNonexistentClients_whenSaveReservation_thenThrowException() {
        // Given
        ReservationEntity reservation = createValidReservation();
        when(reservationRepository.findByStartDateTimeLessThanAndEndDateTimeGreaterThan(any(), any()))
                .thenReturn(Collections.emptyList());
        when(clientRepository.findByRutIn(anyList())).thenReturn(Collections.emptyList());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.saveReservation(reservation));
        assertTrue(ex.getMessage().contains("Uno o más RUTs no están registrados en el sistema."));
    }

    @Test
    void givenUnavailableKarts_whenSaveReservation_thenThrowException() {
        // Given
        ReservationEntity reservation = createValidReservation();
        when(reservationRepository.findByStartDateTimeLessThanAndEndDateTimeGreaterThan(any(), any()))
                .thenReturn(Collections.emptyList());
        when(clientRepository.findByRutIn(anyList())).thenReturn(List.of(new ClientEntity()));
        when(kartRepository.findByCodeInAndStatus(anyList(), eq("AVAILABLE")))
                .thenReturn(Collections.emptyList());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.saveReservation(reservation));
        assertTrue(ex.getMessage().contains("Uno o más karts no están disponibles"));
    }

    // --- Test para getAllReservations() ---

    @Test
    void whenGetAllReservations_thenReturnAllReservations() {
        // Given
        List<ReservationEntity> mockList = new ArrayList<>();
        mockList.add(new ReservationEntity());
        mockList.add(new ReservationEntity());

        when(reservationRepository.findAll()).thenReturn(mockList);

        // When
        List<ReservationEntity> result = reservationService.getReservations();

        // Then
        assertEquals(2, result.size());
        verify(reservationRepository).findAll();
    }

    // --- Test para getReservationById() ---

    @Test
    void givenValidId_whenGetReservationById_thenReturnReservation() {
        // Given
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // When
        ReservationEntity result = reservationService.getReservationById(1L);

        // Then
        assertEquals(1L, result.getId());
        verify(reservationRepository).findById(1L);
    }

    // --- Test para deleteReservation() ---

    @Test
    void givenValidId_whenDeleteReservation_thenDeleteCalledOnce() {
        // Given
        doNothing().when(reservationRepository).deleteById(1L);

        // When
        try {
            reservationService.deleteReservation(1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Then
        verify(reservationRepository).deleteById(1L);
    }

    // --- Métodos auxiliares ---

    private ReservationEntity createValidReservation() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setNumberOfPeople(3);
        reservation.setLaps(5);
        reservation.setStartDateTime(LocalDateTime.of(2025, 6, 1, 14, 0));
        reservation.setClientRuts(List.of("12.345.678-9"));
        reservation.setKartCodes(List.of("K003", "K004", "K005"));
        return reservation;
    }

    private void mockPricingAndAvailability() {
        when(pricingRepository.getTotalDurationByLaps(anyInt())).thenReturn(15); // duración 15 min

        when(reservationRepository.findByStartDateTimeLessThanAndEndDateTimeGreaterThan(any(), any()))
                .thenReturn(Collections.emptyList());

        when(clientRepository.findByRutIn(anyList()))
                .thenReturn(List.of(new ClientEntity()));

        when(kartRepository.findByCodeInAndStatus(anyList(), eq("AVAILABLE")))
                .thenReturn(List.of(new KartEntity(), new KartEntity(), new KartEntity()));

        when(reservationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentReceiptService.generateReceipt(any())).thenReturn(new PaymentReceiptEntity());
    }
    @Test
    void generateRevenueReport_shouldReturnCorrectTotals() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationCode("ABC123");
        reservation.setStartDateTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        reservation.setLaps(10);

        PaymentDetailEntity detail = new PaymentDetailEntity();
        detail.setAmount(100.0f);

        PaymentReceiptEntity receipt = new PaymentReceiptEntity();
        receipt.setPaymentDetails(List.of(detail));

        when(reservationRepository.findByStartDateTimeBetweenAndStatus(
                any(), any(), eq("CONFIRMED"))).thenReturn(List.of(reservation));
        when(paymentReceiptRepository.findByReservationCode("ABC123")).thenReturn(receipt);

        Map<String, Map<YearMonth, Long>> result = reservationService.generateRevenueReport(startDate, endDate);

        assertEquals(100L, result.get("10 vueltas o máx 10 min").get(YearMonth.of(2024, 1)));
        assertEquals(100L, result.get("TOTAL").get(YearMonth.of(2024, 1)));
    }

    @Test
    void generatePeopleReport_shouldReturnCorrectTotalsByPeopleCategory() {
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 28);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationCode("DEF456");
        reservation.setStartDateTime(LocalDateTime.of(2024, 2, 10, 12, 0));
        reservation.setNumberOfPeople(5);

        PaymentDetailEntity detail = new PaymentDetailEntity();
        detail.setAmount(150.0f);

        PaymentReceiptEntity receipt = new PaymentReceiptEntity();
        receipt.setPaymentDetails(List.of(detail));

        when(reservationRepository.findByStartDateTimeBetweenAndStatus(any(), any(), eq("CONFIRMED")))
                .thenReturn(List.of(reservation));
        when(paymentReceiptRepository.findByReservationCode("DEF456")).thenReturn(receipt);

        Map<String, Map<YearMonth, Long>> result = reservationService.generatePeopleReport(startDate, endDate);

        assertEquals(150L, result.get("3-5 personas").get(YearMonth.of(2024, 2)));
        assertEquals(150L, result.get("TOTAL").get(YearMonth.of(2024, 2)));
    }

    @Test
    void getWeeklySchedule_shouldReturnReservationSummary() {
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 7);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationCode("XYZ789");
        reservation.setStartDateTime(LocalDateTime.of(2024, 3, 2, 14, 0));
        reservation.setEndDateTime(LocalDateTime.of(2024, 3, 2, 15, 0));

        when(reservationRepository.findByStartDateTimeBetweenAndStatus(any(), any(), eq("CONFIRMED")))
                .thenReturn(List.of(reservation));

        List<Map<String, Object>> result = reservationService.getWeeklySchedule(startDate, endDate);

        assertEquals(1, result.size());
        assertEquals("XYZ789", result.get(0).get("reservationCode"));
        assertEquals(LocalDateTime.of(2024, 3, 2, 14, 0), result.get(0).get("start"));
        assertEquals(LocalDateTime.of(2024, 3, 2, 15, 0), result.get(0).get("end"));
    }

    @Test
    void whenGetReservationsByDate_thenReturnReservationsForThatDate() {
        // Given
        LocalDate date = LocalDate.of(2023, 4, 28);
        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setStartDateTime(LocalDateTime.of(2023, 4, 28, 10, 0)); // Same date
        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setStartDateTime(LocalDateTime.of(2023, 4, 29, 10, 0)); // Different date
        List<ReservationEntity> mockList = List.of(reservation1, reservation2);

        // Simulamos el comportamiento del repository
        when(reservationRepository.findAll()).thenReturn(mockList);

        // When
        List<ReservationEntity> result = reservationService.getReservationsByDate(date);

        // Then
        assertEquals(1, result.size()); // Should only contain reservation1
        assertTrue(result.contains(reservation1));
        assertFalse(result.contains(reservation2));
        verify(reservationRepository).findAll();
    }

    @Test
    void whenUpdateReservation_thenReturnUpdatedReservation() {
        // Given
        ReservationEntity reservationToUpdate = new ReservationEntity();
        reservationToUpdate.setReservationCode("ABC123");
        reservationToUpdate.setStartDateTime(LocalDateTime.of(2023, 4, 28, 10, 0));

        ReservationEntity updatedReservation = new ReservationEntity();
        updatedReservation.setReservationCode("ABC123");
        updatedReservation.setStartDateTime(LocalDateTime.of(2023, 4, 28, 12, 0)); // Same code but different start time

        // Simulamos el comportamiento del repository
        when(reservationRepository.save(reservationToUpdate)).thenReturn(updatedReservation);

        // When
        ReservationEntity result = reservationService.updateReservation(reservationToUpdate);

        // Then
        assertEquals(updatedReservation.getStartDateTime(), result.getStartDateTime()); // Assert that the times are updated
        assertEquals(updatedReservation.getReservationCode(), result.getReservationCode()); // Assert that the code is the same
        verify(reservationRepository).save(reservationToUpdate); // Ensure the save method is called
    }

    @Test
    void whenDeleteFails_thenThrowsException() {
        // GIVEN
        Long reservationId = 1L;

        // Simulamos que deleteById lanza una excepción
        doThrow(new RuntimeException("Error al eliminar")).when(reservationRepository).deleteById(reservationId);

        // WHEN & THEN
        Exception exception = assertThrows(Exception.class, () -> {
            reservationService.deleteReservation(reservationId);
        });

        assertEquals("Error al eliminar", exception.getMessage());

        // Verificamos que se llamó a deleteById
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }
   
}
