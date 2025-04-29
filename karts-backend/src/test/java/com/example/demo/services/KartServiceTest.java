package com.example.demo.services;

import com.example.demo.entities.KartEntity;
import com.example.demo.repositories.KartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KartServiceTest {

    @Mock
    private KartRepository kartRepository;

    @InjectMocks
    private KartService kartService;

    KartEntity kart1;
    KartEntity kart2;

    @BeforeEach
    void setUp() {
        kart1 = new KartEntity();
        kart1.setId(1L);
        kart1.setCode("K001");
        kart1.setStatus("AVAILABLE");

        kart2 = new KartEntity();
        kart2.setId(2L);
        kart2.setCode("K002");
        kart2.setStatus("NOT AVAILABLE");
    }

    @Test
    void whenGetKarts_thenReturnListOfKarts() {
        // Given
        ArrayList<KartEntity> expectedKarts = new ArrayList<>(Arrays.asList(kart1, kart2));
        when(kartRepository.findAll()).thenReturn(expectedKarts);

        // When
        ArrayList<KartEntity> actualKarts = kartService.getKarts();

        // Then
        assertThat(actualKarts).hasSize(2);
        assertThat(actualKarts.get(0).getCode()).isEqualTo("K001");
        assertThat(actualKarts.get(1).getStatus()).isEqualTo("NOT AVAILABLE");
    }

    @Test
    void whenSaveKart_thenKartIsSaved() {
        // Given
        when(kartRepository.save(kart1)).thenReturn(kart1);

        // When
        KartEntity savedKart = kartService.saveKart(kart1);

        // Then
        assertThat(savedKart.getCode()).isEqualTo("K001");
        assertThat(savedKart.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    void whenGetKartById_thenReturnKart() {
        // Given
        when(kartRepository.findById(1L)).thenReturn(Optional.of(kart1));

        // When
        KartEntity foundKart = kartService.getKartById(1L);

        // Then
        assertThat(foundKart).isNotNull();
        assertThat(foundKart.getCode()).isEqualTo("K001");
    }

    @Test
    void whenUpdateKartStatus_thenStatusIsUpdated() {
        // Given
        when(kartRepository.findById(1L)).thenReturn(Optional.of(kart1));
        when(kartRepository.save(any(KartEntity.class))).thenAnswer(i -> i.getArgument(0));

        // When
        KartEntity updatedKart = kartService.updateKartStatus(1L, "NOT AVAILABLE");

        // Then
        assertThat(updatedKart.getStatus()).isEqualTo("NOT AVAILABLE");
    }

    @Test
    void whenDeleteKart_thenKartIsDeleted() throws Exception {
        // Given
        doNothing().when(kartRepository).deleteById(1L);

        // When
        boolean deleted = kartService.deleteKart(1L);

        // Then
        assertThat(deleted).isTrue();
        verify(kartRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteKartFails_thenThrowsException() {
        // Given
        doThrow(new RuntimeException("Error al eliminar")).when(kartRepository).deleteById(1L);

        // When
        Exception exception = assertThrows(Exception.class, () -> kartService.deleteKart(1L));

        // Then
        assertThat(exception.getMessage()).contains("Error al eliminar");
    }

    @Test
    void whenKartNotFound_thenReturnNull() {
        // Given
        Long nonExistentId = 999L;

        // Simulamos que el kart no existe en el repositorio
        when(kartRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        KartEntity result = kartService.updateKartStatus(nonExistentId, "Active");

        // Then
        assertNull(result, "El kart no debería ser encontrado, por lo que el resultado debe ser null");
    }

}
