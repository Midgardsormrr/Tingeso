// ======= CLIENT CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.ClientEntity;
import com.example.demo.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping
    public ArrayList<ClientEntity> getAllClients() {
        return clientService.getClients();
    }

    @PostMapping
    public ClientEntity createClient(@RequestBody ClientEntity client) {
        return clientService.saveClient(client);
    }

    @GetMapping("/{rut}")
    public ClientEntity getClient(@PathVariable String rut) {
        return clientService.getClientByRut(rut);
    }

    @PutMapping
    public ClientEntity updateClient(@RequestBody ClientEntity client) {
        return clientService.updateClient(client);
    }

    @DeleteMapping("/{rut}")
    public boolean deleteClient(@PathVariable String rut) throws Exception {
        return clientService.deleteClient(rut);
    }
}