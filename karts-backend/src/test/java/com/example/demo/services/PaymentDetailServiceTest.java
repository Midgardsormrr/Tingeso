package com.example.demo.services;

import com.example.demo.entities.PaymentDetailEntity;
import com.example.demo.repositories.PaymentDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDetailServiceTest {

    @Mock
    private PaymentDetailRepository detailRepository;

    @InjectMocks
    private PaymentDetailService paymentDetailService;

    PaymentDetailEntity paymentDetail1;
    PaymentDetailEntity paymentDetail2;

    @BeforeEach
    void setUp() {
        paymentDetail1 = new PaymentDetailEntity();
        paymentDetail1.setId(1L);
        paymentDetail1.setReceiptId(100L);
        paymentDetail1.setAmount(50.0f);

        paymentDetail2 = new PaymentDetailEntity();
        paymentDetail2.setId(2L);
        paymentDetail2.setReceiptId(101L);
        paymentDetail2.setAmount(75.0f);
    }

    @Test
    void whenGetPaymentDetails_thenReturnListOfPaymentDetails() {
        // Given
        ArrayList<PaymentDetailEntity> expectedPaymentDetails = new ArrayList<>(Arrays.asList(paymentDetail1, paymentDetail2));
        when(detailRepository.findAll()).thenReturn(expectedPaymentDetails);

        // When
        ArrayList<PaymentDetailEntity> actualPaymentDetails = paymentDetailService.getPaymentDetails();

        // Then
        assertThat(actualPaymentDetails).hasSize(2);
        assertThat(actualPaymentDetails.get(0).getReceiptId()).isEqualTo(100L);
        assertThat(actualPaymentDetails.get(1).getAmount()).isEqualTo(75.0f);
    }

    @Test
    void whenSavePaymentDetail_thenPaymentDetailIsSaved() {
        // Given
        when(detailRepository.save(paymentDetail1)).thenReturn(paymentDetail1);

        // When
        PaymentDetailEntity savedPaymentDetail = paymentDetailService.savePaymentDetail(paymentDetail1);

        // Then
        assertThat(savedPaymentDetail.getId()).isEqualTo(1L);
        assertThat(savedPaymentDetail.getAmount()).isEqualTo(50.0f);
    }

    @Test
    void whenGetPaymentDetailById_thenReturnPaymentDetails() {
        // Given
        when(detailRepository.findByReceiptId(100L)).thenReturn(new ArrayList<>(Arrays.asList(paymentDetail1)));

        // When
        List<PaymentDetailEntity> foundPaymentDetails = paymentDetailService.getPaymentDetailById(100L);

        // Then
        assertThat(foundPaymentDetails).hasSize(1);
        assertThat(foundPaymentDetails.get(0).getReceiptId()).isEqualTo(100L);
    }

    @Test
    void whenUpdatePaymentDetail_thenPaymentDetailIsUpdated() {
        // Given
        paymentDetail1.setAmount(60.0f);
        when(detailRepository.save(paymentDetail1)).thenReturn(paymentDetail1);

        // When
        PaymentDetailEntity updatedPaymentDetail = paymentDetailService.updatePaymentDetail(paymentDetail1);

        // Then
        assertThat(updatedPaymentDetail.getAmount()).isEqualTo(60.0f);
    }

    @Test
    void whenDeletePaymentDetail_thenPaymentDetailIsDeleted() throws Exception {
        // Given
        doNothing().when(detailRepository).deleteById(1L);

        // When
        boolean deleted = paymentDetailService.deletePaymentDetail(1L);

        // Then
        assertThat(deleted).isTrue();
        verify(detailRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeletePaymentDetailFails_thenThrowsException() {
        // Given
        doThrow(new RuntimeException("Error al eliminar")).when(detailRepository).deleteById(1L);

        // When
        Exception exception = assertThrows(Exception.class, () -> paymentDetailService.deletePaymentDetail(1L));

        // Then
        assertThat(exception.getMessage()).contains("Error al eliminar");
    }
}
