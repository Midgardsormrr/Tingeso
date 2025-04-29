package com.example.demo.services;

import com.example.demo.entities.SpecialDayEntity;
import com.example.demo.repositories.SpecialDayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecialDayServiceTest {

    @Mock
    private SpecialDayRepository specialDayRepository;

    @InjectMocks
    private SpecialDayService specialDayService;

    private SpecialDayEntity specialDayEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        specialDayEntity = new SpecialDayEntity();
        specialDayEntity.setDate(LocalDate.of(2025, 4, 29));
        specialDayEntity.setType("BIRTHDAY");
    }

    // Test para obtener todos los días especiales
    @Test
    void givenSpecialDaysExist_whenGetSpecialDays_thenReturnSpecialDaysList() {
        // Given
        ArrayList<SpecialDayEntity> specialDaysList = new ArrayList<>();
        specialDaysList.add(specialDayEntity);
        when(specialDayRepository.findAll()).thenReturn(specialDaysList);

        // When
        ArrayList<SpecialDayEntity> result = specialDayService.getSpecialDays();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("BIRTHDAY", result.get(0).getType());
        verify(specialDayRepository, times(1)).findAll();
    }

    // Test para obtener un día especial por fecha
    @Test
    void givenSpecialDayExists_whenGetSpecialDayByDate_thenReturnSpecialDay() {
        // Given
        LocalDate date = LocalDate.of(2025, 4, 29);
        when(specialDayRepository.findByDate(date)).thenReturn(specialDayEntity);

        // When
        SpecialDayEntity result = specialDayService.getSpecialDayByDate(date);

        // Then
        assertNotNull(result);
        assertEquals("BIRTHDAY", result.getType());
        assertEquals(LocalDate.of(2025, 4, 29), result.getDate());
        verify(specialDayRepository, times(1)).findByDate(date);
    }

    // Test cuando no se encuentra un día especial por fecha
    @Test
    void givenSpecialDayDoesNotExist_whenGetSpecialDayByDate_thenReturnNull() {
        // Given
        LocalDate date = LocalDate.of(2025, 5, 1);
        when(specialDayRepository.findByDate(date)).thenReturn(null);

        // When
        SpecialDayEntity result = specialDayService.getSpecialDayByDate(date);

        // Then
        assertNull(result);
        verify(specialDayRepository, times(1)).findByDate(date);
    }

    // Test para verificar si es un día especial
    @Test
    void givenSpecialDayExists_whenIsSpecialDay_thenReturnTrue() {
        // Given
        LocalDate date = LocalDate.of(2025, 4, 29);
        when(specialDayRepository.existsByDate(date)).thenReturn(true);

        // When
        boolean result = specialDayService.isSpecialDay(date);

        // Then
        assertTrue(result);
        verify(specialDayRepository, times(1)).existsByDate(date);
    }

    // Test para verificar si no es un día especial
    @Test
    void givenSpecialDayDoesNotExist_whenIsSpecialDay_thenReturnFalse() {
        // Given
        LocalDate date = LocalDate.of(2025, 5, 1);
        when(specialDayRepository.existsByDate(date)).thenReturn(false);

        // When
        boolean result = specialDayService.isSpecialDay(date);

        // Then
        assertFalse(result);
        verify(specialDayRepository, times(1)).existsByDate(date);
    }
}
