// ======= CLIENT SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.ClientEntity;
import com.example.demo.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public ArrayList<ClientEntity> getClients() {
        return (ArrayList<ClientEntity>) clientRepository.findAll();
    }

    public ClientEntity saveClient(ClientEntity client) {
        return clientRepository.save(client);
    }

    public ClientEntity getClientByRut(String rut) {
        return (ClientEntity) clientRepository.findByRut(rut).orElse(null);
    }

    public ClientEntity updateClient(ClientEntity client) {
        return clientRepository.save(client);
    }
    @Transactional
    public void deleteClientByRut(String rut) throws Exception {
        clientRepository.deleteByRut(rut);
    }
}
