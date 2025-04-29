package com.example.demo.services;

import com.example.demo.entities.PricingEntity;
import com.example.demo.repositories.PricingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PricingServiceTest {

    @Mock
    private PricingRepository pricingRepository;

    @InjectMocks
    private PricingService pricingService;

    private PricingEntity pricingEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pricingEntity = new PricingEntity();
        pricingEntity.setLaps(5);
        pricingEntity.setBasePrice(100.0f);
        pricingEntity.setTotalDuration(60);
    }

    // Test para obtener todos los precios
    @Test
    void givenPricingExists_whenGetPricing_thenReturnPricingList() {
        // Given
        ArrayList<PricingEntity> pricingList = new ArrayList<>();
        pricingList.add(pricingEntity);
        when(pricingRepository.findAll()).thenReturn(pricingList);

        // When
        ArrayList<PricingEntity> result = pricingService.getPricing();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(100.0f, result.get(0).getBasePrice());
        verify(pricingRepository, times(1)).findAll();
    }

    // Test para obtener un pricing por laps
    @Test
    void givenPricingExists_whenGetPricingByLaps_thenReturnPricing() {
        // Given
        when(pricingRepository.findByLaps(5)).thenReturn(Optional.of(pricingEntity));

        // When
        PricingEntity result = pricingService.getPricingByLaps(5);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getLaps());
        assertEquals(100.0f, result.getBasePrice());
        verify(pricingRepository, times(1)).findByLaps(5);
    }

    // Test cuando no se encuentra pricing por laps
    @Test
    void givenPricingDoesNotExist_whenGetPricingByLaps_thenReturnNull() {
        // Given
        when(pricingRepository.findByLaps(10)).thenReturn(Optional.empty());

        // When
        PricingEntity result = pricingService.getPricingByLaps(10);

        // Then
        assertNull(result);
        verify(pricingRepository, times(1)).findByLaps(10);
    }

    // Test para obtener la duración total por laps
    @Test
    void givenPricingExists_whenGetTotalDurationByLaps_thenReturnDuration() {
        // Given
        when(pricingRepository.getTotalDurationByLaps(5)).thenReturn(60);

        // When
        Integer result = pricingService.getTotalDurationByLaps(5);

        // Then
        assertNotNull(result);
        assertEquals(60, result);
        verify(pricingRepository, times(1)).getTotalDurationByLaps(5);
    }

    // Test cuando no se encuentra duración por laps
    @Test
    void givenPricingDoesNotExist_whenGetTotalDurationByLaps_thenReturnNull() {
        // Given
        when(pricingRepository.getTotalDurationByLaps(10)).thenReturn(null);

        // When
        Integer result = pricingService.getTotalDurationByLaps(10);

        // Then
        assertNull(result);
        verify(pricingRepository, times(1)).getTotalDurationByLaps(10);
    }
}
