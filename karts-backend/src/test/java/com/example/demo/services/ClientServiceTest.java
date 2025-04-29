package com.example.demo.services;

import com.example.demo.entities.ClientEntity;
import com.example.demo.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private ClientEntity client1;
    private ClientEntity client2;

    @BeforeEach
    void setUp() {
        client1 = new ClientEntity();
        client1.setRut("12.345.678-5");
        client1.setName("Alice");

        client2 = new ClientEntity();
        client2.setRut("23.456.789-6");
        client2.setName("Bob");
    }

    @Test
    void testGetClients() {
        //Given
        ArrayList<ClientEntity> mockedList = new ArrayList<>();
        mockedList.add(client1);
        mockedList.add(client2);
        when(clientRepository.findAll()).thenReturn(mockedList);

        //When
        ArrayList<ClientEntity> result = clientService.getClients();

        //Then
        assertEquals(2, result.size());
        assertTrue(result.contains(client1));
        assertTrue(result.contains(client2));
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testSaveClient() {
        //Given
        when(clientRepository.save(client1)).thenReturn(client1);

        //When
        ClientEntity result = clientService.saveClient(client1);

        //Then
        assertEquals(client1, result);
        verify(clientRepository, times(1)).save(client1);
    }

    @Test
    void testGetClientByRut_Found() {
        //Given
        when(clientRepository.findByRut("12.345.678-5")).thenReturn(Optional.of(client1));

        //When
        ClientEntity result = clientService.getClientByRut("12.345.678-5");

        //Then
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        verify(clientRepository, times(1)).findByRut("12.345.678-5");
    }

    @Test
    void testGetClientByRut_NotFound() {
        //Given
        when(clientRepository.findByRut("34.567.890-7")).thenReturn(Optional.empty());

        //When
        ClientEntity result = clientService.getClientByRut("34.567.890-7");

        //Then
        assertNull(result);
        verify(clientRepository, times(1)).findByRut("34.567.890-7");
    }

    @Test
    void testUpdateClient() {
        //Given
        when(clientRepository.save(client2)).thenReturn(client2);

        //When
        ClientEntity result = clientService.updateClient(client2);

        //Then
        assertEquals(client2, result);
        verify(clientRepository, times(1)).save(client2);
    }

    @Test
    void testDeleteClient_Success() {
        //Given
        doNothing().when(clientRepository).deleteByRut("12.345.678-5");

        //When & Then
        assertDoesNotThrow(() -> clientService.deleteClientByRut("12.345.678-5"));
        verify(clientRepository, times(1)).deleteByRut("12.345.678-5");
    }

    @Test
    void testDeleteClient_ThrowsException() {
        //Given
        doThrow(new RuntimeException("DB error")).when(clientRepository).deleteByRut("98.765.432-1");

        //When & Then
        Exception ex = assertThrows(Exception.class, () -> clientService.deleteClientByRut("98.765.432-1"));
        assertTrue(ex.getMessage().contains("DB error"));
        verify(clientRepository, times(1)).deleteByRut("98.765.432-1");
    }
}
